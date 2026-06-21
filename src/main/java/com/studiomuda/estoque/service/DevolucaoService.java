package com.studiomuda.estoque.service;

import com.studiomuda.estoque.jpa.entity.ItemPedidoJpaEntity;
import com.studiomuda.estoque.jpa.entity.PedidoJpaEntity;
import com.studiomuda.estoque.jpa.entity.ProdutoJpaEntity;
import com.studiomuda.estoque.jpa.repository.ItemPedidoJpaRepository;
import com.studiomuda.estoque.jpa.repository.PedidoJpaRepository;
import com.studiomuda.estoque.jpa.repository.ProdutoJpaRepository;
import com.studiomuda.estoque.model.Cliente;
import com.studiomuda.estoque.model.CreditoCliente;
import com.studiomuda.estoque.model.Pedido;
import com.studiomuda.estoque.repository.CreditoClienteRepository;
import com.studiomuda.estoque.repository.DevolucaoRepository;
import com.studiomuda.estoque.repository.ItemDevolucaoRepository;
import com.studiomuda.estoque.model.Devolucao;
import com.studiomuda.estoque.model.ItemDevolucao;
import com.studiomuda.estoque.model.ItemPedido;
import com.studiomuda.estoque.observer.DevolucaoDomainEvent;
import com.studiomuda.estoque.observer.ObservadorDeDevolucao;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de domínio para devoluções.
 * Subject do Observer + orquestrador do fluxo de aprovação.
 *
 * Padrão de Design: Observer (GoF) — ConcreteSubject
 * Nível Tático DDD: Domain Service
 * Camada: Aplicação (Arquitetura Limpa)
 */
@Service
public class DevolucaoService {

    private final DevolucaoRepository devolucaoRepository;
    private final ItemDevolucaoRepository itemRepository;
    private final CreditoClienteRepository creditoRepository;
    private final PedidoJpaRepository pedidoRepository;
    private final ItemPedidoJpaRepository itemPedidoRepository;
    private final ProdutoJpaRepository produtoRepository;
    private final ClienteService clienteService;
    private final List<ObservadorDeDevolucao> observadores;

    public DevolucaoService(DevolucaoRepository devolucaoRepository,
                            ItemDevolucaoRepository itemRepository,
                            CreditoClienteRepository creditoRepository,
                            PedidoJpaRepository pedidoRepository,
                            ItemPedidoJpaRepository itemPedidoRepository,
                            ProdutoJpaRepository produtoRepository,
                            ClienteService clienteService,
                            List<ObservadorDeDevolucao> observadores) {
        this.devolucaoRepository = devolucaoRepository;
        this.itemRepository = itemRepository;
        this.creditoRepository = creditoRepository;
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.produtoRepository = produtoRepository;
        this.clienteService = clienteService;
        this.observadores = List.copyOf(observadores);
    }

    private void notificar(DevolucaoDomainEvent evento) {
        for (ObservadorDeDevolucao obs : observadores) obs.aoAprovarDevolucao(evento);
    }

    /**
     * Solicita uma nova devolução — persiste com status PENDENTE.
     */
    @Transactional
    public Devolucao solicitar(Devolucao devolucao, List<ItemDevolucao> itens) {
        // Definir defaults explícitos antes de salvar
        if (devolucao.getStatus() == null || devolucao.getStatus().isBlank()) {
            devolucao.setStatus("PENDENTE");
        }
        if (devolucao.getDataSolicitacao() == null) {
            devolucao.setDataSolicitacao(LocalDateTime.now());
        }
        if (devolucao.getTipoRestituicao() == null || devolucao.getTipoRestituicao().isBlank()) {
            devolucao.setTipoRestituicao("CREDITO_LOJA");
        }

        devolucao = devolucaoRepository.save(devolucao);
        for (ItemDevolucao item : itens) {
            item.setDevolucaoId(devolucao.getId());
            itemRepository.save(item);
        }
        devolucao.setItens(itens);
        return devolucao;
    }

    /**
     * Aprova a devolução — dispara Observer que restaura estoque e executa Strategy.
     * Idempotência: valida que está PENDENTE antes de aprovar.
     */
    @Transactional
    public void aprovar(int devolucaoId, String observacaoGestor) {
        Devolucao devolucao = devolucaoRepository.findById(devolucaoId).orElse(null);
        if (devolucao == null) throw new IllegalStateException("Devolução não encontrada.");
        if (!devolucao.isPendente()) throw new IllegalStateException(
                "Devolução não pode ser aprovada — status atual: " + devolucao.getStatus());

        devolucao.setStatus("APROVADA");
        devolucao.setObservacaoGestor(observacaoGestor);
        devolucao.setDataResolucao(LocalDateTime.now());
        devolucaoRepository.save(devolucao);

        List<ItemDevolucao> itens = itemRepository.findByDevolucaoId(devolucaoId);
        devolucao.setItens(itens);

        notificar(new DevolucaoDomainEvent(devolucao));
    }

    /**
     * Rejeita a devolução — sem efeito no estoque.
     */
    @Transactional
    public void rejeitar(int devolucaoId, String observacaoGestor) {
        Devolucao devolucao = devolucaoRepository.findById(devolucaoId).orElse(null);
        if (devolucao == null) throw new IllegalStateException("Devolução não encontrada.");
        if (!devolucao.isPendente()) throw new IllegalStateException(
                "Devolução já foi processada — status: " + devolucao.getStatus());

        devolucao.setStatus("REJEITADA");
        devolucao.setObservacaoGestor(observacaoGestor);
        devolucao.setDataResolucao(LocalDateTime.now());
        devolucaoRepository.save(devolucao);
    }

    public Devolucao buscarPorId(int id) {
        return devolucaoRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Pedido buscarPedidoParaDevolucao(int pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .map(this::mapearPedido)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<ItemPedido> listarItensPedidoParaDevolucao(int pedidoId) {
        return itemPedidoRepository.findByPedidoId(pedidoId).stream()
                .map(this::mapearItemPedido)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ItemDevolucao> listarItensDevolucaoDetalhados(int devolucaoId) {
        List<ItemDevolucao> itens = itemRepository.findByDevolucaoId(devolucaoId);
        for (ItemDevolucao item : itens) {
            ProdutoJpaEntity produto = produtoRepository.findById(item.getProdutoId()).orElse(null);
            if (produto != null) {
                item.setProdutoNome(produto.getNome());
            }
        }
        return itens;
    }

    public List<Devolucao> listar() {
        return devolucaoRepository.findAll();
    }

    public List<Devolucao> listarPorStatus(String status) {
        return devolucaoRepository.findByStatusOrderByDataSolicitacaoDesc(status);
    }

    @Transactional(readOnly = true)
    public List<Devolucao> listarComNomeCliente(String status) {
        List<Devolucao> devolucoes = (status != null && !status.isEmpty())
                ? devolucaoRepository.findByStatusOrderByDataSolicitacaoDesc(status)
                : devolucaoRepository.findAll();
        for (Devolucao d : devolucoes) {
            Cliente cliente = clienteService.buscarPorId(d.getClienteId());
            d.setClienteNome(cliente != null ? cliente.getNome() : "Desconhecido");
        }
        return devolucoes;
    }

    @Transactional(readOnly = true)
    public Devolucao buscarPorIdComDetalhes(int id) {
        Devolucao devolucao = devolucaoRepository.findById(id).orElse(null);
        if (devolucao == null) return null;
        List<ItemDevolucao> itens = listarItensDevolucaoDetalhados(id);
        devolucao.setItens(itens);
        Cliente cliente = clienteService.buscarPorId(devolucao.getClienteId());
        devolucao.setClienteNome(cliente != null ? cliente.getNome() : "Desconhecido");
        return devolucao;
    }

    @Transactional(readOnly = true)
    public List<CreditoCliente> listarCreditosAtivos() {
        List<CreditoCliente> creditos = creditoRepository.findByStatus("ATIVO");
        for (CreditoCliente c : creditos) {
            Cliente cliente = clienteService.buscarPorId(c.getClienteId());
            c.setClienteNome(cliente != null ? cliente.getNome() : "Desconhecido");
        }
        return creditos;
    }

    private Pedido mapearPedido(PedidoJpaEntity entity) {
        Pedido pedido = new Pedido();
        pedido.setId(entity.getId() != null ? entity.getId() : 0);
        pedido.setDataRequisicao(entity.getDataRequisicao());
        pedido.setDataEntrega(entity.getDataEntrega());
        pedido.setClienteId(entity.getClienteId() != null ? entity.getClienteId() : 0);
        pedido.setClienteNome(entity.getCliente() != null ? entity.getCliente().getNome() : null);
        pedido.setCupomId(entity.getCupomId() != null ? entity.getCupomId() : 0);
        pedido.setFuncionarioId(entity.getFuncionarioId() != null ? entity.getFuncionarioId() : 0);
        pedido.setValorDesconto(entity.getValorDesconto() != null ? entity.getValorDesconto() : 0.0);
        pedido.setStatus(entity.getStatus());
        pedido.setStatusPagamento(entity.getStatusPagamento());
        pedido.setDataPagamento(entity.getDataPagamento());
        pedido.setCancelamentoSolicitanteId(entity.getCancelamentoSolicitanteId());
        pedido.setCancelamentoSolicitanteNome(entity.getCancelamentoSolicitanteNome());
        pedido.setJustificativaCancelamento(entity.getJustificativaCancelamento());
        pedido.setDataCancelamento(entity.getDataCancelamento());
        pedido.setCancelamentoAprovadorId(entity.getCancelamentoAprovadorId());
        pedido.setCancelamentoAprovadorNome(entity.getCancelamentoAprovadorNome());
        pedido.setDataAprovacaoCancelamento(entity.getDataAprovacaoCancelamento());
        return pedido;
    }

    private ItemPedido mapearItemPedido(ItemPedidoJpaEntity entity) {
        ItemPedido item = new ItemPedido();
        item.setId(entity.getId() != null ? entity.getId() : 0);
        item.setPedidoId(entity.getPedido() != null && entity.getPedido().getId() != null ? entity.getPedido().getId() : 0);
        item.setProdutoId(entity.getProduto() != null && entity.getProduto().getId() != null ? entity.getProduto().getId() : 0);
        item.setQuantidade(entity.getQuantidade() != null ? entity.getQuantidade() : 0);
        if (entity.getProduto() != null) {
            item.setProdutoNome(entity.getProduto().getNome());
            item.setProdutoValor(entity.getProduto().getValor() != null ? entity.getProduto().getValor().doubleValue() : 0.0);
        }
        return item;
    }
}
