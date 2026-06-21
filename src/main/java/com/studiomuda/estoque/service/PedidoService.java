package com.studiomuda.estoque.service;

import com.studiomuda.estoque.dao.ItemPedidoDAO;
import com.studiomuda.estoque.dao.PedidoDAO;
import com.studiomuda.estoque.jpa.entity.ItemPedidoJpaEntity;
import com.studiomuda.estoque.jpa.entity.MovimentacaoEstoqueJpaEntity;
import com.studiomuda.estoque.jpa.entity.ParametroCancelamentoJpaEntity;
import com.studiomuda.estoque.jpa.entity.PedidoJpaEntity;
import com.studiomuda.estoque.jpa.entity.ProdutoJpaEntity;
import com.studiomuda.estoque.jpa.repository.ItemPedidoJpaRepository;
import com.studiomuda.estoque.jpa.repository.MovimentacaoEstoqueJpaRepository;
import com.studiomuda.estoque.jpa.repository.ParametroCancelamentoJpaRepository;
import com.studiomuda.estoque.jpa.repository.PedidoJpaRepository;
import com.studiomuda.estoque.jpa.repository.ProdutoJpaRepository;
import com.studiomuda.estoque.model.ItemPedido;
import com.studiomuda.estoque.model.Pedido;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
public class PedidoService {
    private final PedidoDAO pedidoDAO;
    private final ItemPedidoDAO itemPedidoDAO;
    private final PedidoJpaRepository pedidoRepository;
    private final ItemPedidoJpaRepository itemPedidoRepository;
    private final ProdutoJpaRepository produtoRepository;
    private final MovimentacaoEstoqueJpaRepository movimentacaoRepository;
    private final ParametroCancelamentoJpaRepository parametroCancelamentoRepository;
    private final FidelidadeService fidelidadeService;

    public PedidoService() {
        this(new PedidoDAO(), new ItemPedidoDAO());
    }

    public PedidoService(PedidoDAO pedidoDAO, ItemPedidoDAO itemPedidoDAO) {
        this.pedidoDAO = pedidoDAO;
        this.itemPedidoDAO = itemPedidoDAO;
        this.pedidoRepository = null;
        this.itemPedidoRepository = null;
        this.produtoRepository = null;
        this.movimentacaoRepository = null;
        this.parametroCancelamentoRepository = null;
        this.fidelidadeService = null;
    }

    @Autowired
    public PedidoService(ObjectProvider<PedidoJpaRepository> pedidoRepository,
                         ObjectProvider<ItemPedidoJpaRepository> itemPedidoRepository,
                         ObjectProvider<ProdutoJpaRepository> produtoRepository,
                         ObjectProvider<MovimentacaoEstoqueJpaRepository> movimentacaoRepository,
                         ObjectProvider<ParametroCancelamentoJpaRepository> parametroCancelamentoRepository,
                         ObjectProvider<FidelidadeService> fidelidadeService) {
        this.pedidoDAO = null;
        this.itemPedidoDAO = null;
        this.pedidoRepository = pedidoRepository.getIfAvailable();
        this.itemPedidoRepository = itemPedidoRepository.getIfAvailable();
        this.produtoRepository = produtoRepository.getIfAvailable();
        this.movimentacaoRepository = movimentacaoRepository.getIfAvailable();
        this.parametroCancelamentoRepository = parametroCancelamentoRepository.getIfAvailable();
        this.fidelidadeService = fidelidadeService.getIfAvailable();
    }

    @Transactional
    public ResultadoFechamentoPedido fecharPedido(int pedidoId) {
        if (pedidoRepository == null || fidelidadeService == null) {
            throw new IllegalStateException("O fechamento com fidelidade requer persistencia JPA ativa.");
        }
        PedidoJpaEntity pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido nao encontrado."));
        if (PedidoDAO.STATUS_PEDIDO_CONCLUIDO.equals(normalizarStatusPedido(pedido.getStatus()))) {
            return new ResultadoFechamentoPedido(pedidoId, BigDecimal.ZERO, pedido.getValorDesconto(),
                    pedido.getCliente().getFaixaFidelidade() != null ? pedido.getCliente().getFaixaFidelidade().getNome() : null);
        }
        if (pedido.getItens().isEmpty()) {
            throw new IllegalStateException("Adicione ao menos um item antes de finalizar o pedido.");
        }
        pedido.getItens().forEach(item -> item.getProduto().getValor());
        BigDecimal descontoFidelidade = fidelidadeService.aplicarBeneficio(pedido);
        pedido.setStatus(PedidoDAO.STATUS_PEDIDO_CONCLUIDO);
        pedidoRepository.save(pedido);
        fidelidadeService.recalcularCategoria(pedido.getClienteId());
        String faixa = pedido.getCliente().getFaixaFidelidade() != null
                ? pedido.getCliente().getFaixaFidelidade().getNome()
                : null;
        return new ResultadoFechamentoPedido(pedidoId, descontoFidelidade, pedido.getValorDesconto(), faixa);
    }

    @Transactional
    public ResultadoCancelamento cancelarPedido(int pedidoId,
                                                UsuarioOperacao solicitante,
                                                String justificativa,
                                                Collection<String> autoridades) throws SQLException {
        if (pedidoRepository != null) {
            return cancelarPedidoComJpa(pedidoId, solicitante, justificativa, autoridades);
        }
        return cancelarPedidoComDao(pedidoId, solicitante, justificativa, autoridades);
    }

    public List<PedidoJpaEntity> listarCancelamentosPendentes() {
        if (pedidoRepository == null) {
            return java.util.Collections.emptyList();
        }
        return pedidoRepository.findByStatusOrderByDataCancelamentoDesc(PedidoDAO.STATUS_PEDIDO_CANCELAMENTO_PENDENTE);
    }

    @Transactional(readOnly = true)
    public List<PedidoJpaEntity> listarCancelamentosAuditados() {
        if (pedidoRepository == null) {
            return java.util.Collections.emptyList();
        }
        List<PedidoJpaEntity> cancelados = pedidoRepository.findByStatusOrderByDataCancelamentoDesc(PedidoDAO.STATUS_PEDIDO_CANCELADO);
        List<PedidoJpaEntity> pendentes = pedidoRepository.findByStatusOrderByDataCancelamentoDesc(PedidoDAO.STATUS_PEDIDO_CANCELAMENTO_PENDENTE);
        cancelados.addAll(pendentes);
        return cancelados;
    }

    @Transactional(readOnly = true)
    public PedidoJpaEntity buscarPedidoJpa(int pedidoId) {
        if (pedidoRepository == null) {
            return null;
        }
        PedidoJpaEntity pedido = pedidoRepository.findById(pedidoId).orElse(null);
        if (pedido != null) {
            pedido.getItens().forEach(item -> {
                item.getProduto().getNome();
                item.getProduto().getQuantidade();
            });
        }
        return pedido;
    }

    public int buscarLimiteQuantidadeCancelamento() throws SQLException {
        if (parametroCancelamentoRepository != null) {
            return buscarLimiteQuantidadeCancelamentoJpa();
        }
        return pedidoDAO.buscarLimiteQuantidadeCancelamento();
    }

    @Transactional
    public void adicionarItemPedido(ItemPedido item) {
        if (produtoRepository == null || itemPedidoRepository == null || movimentacaoRepository == null
                || pedidoRepository == null) {
            throw new IllegalStateException("Persistência JPA não disponível para adicionar item.");
        }
        ProdutoJpaEntity produto = produtoRepository.findById(item.getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        if (item.getQuantidade() <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero");
        }
        if (item.getQuantidade() > produto.getQuantidade()) {
            throw new IllegalStateException("Estoque insuficiente. Quantidade disponível: " + produto.getQuantidade());
        }
        pedidoRepository.findById(item.getPedidoId())
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
        PedidoJpaEntity pedidoJpa = pedidoRepository.getReferenceById(item.getPedidoId());
        ItemPedidoJpaEntity novoItem = new ItemPedidoJpaEntity(pedidoJpa, produto, item.getQuantidade());
        itemPedidoRepository.save(novoItem);
        produto.adicionarQuantidade(-item.getQuantidade());
        produtoRepository.save(produto);
        movimentacaoRepository.save(new MovimentacaoEstoqueJpaEntity(
                produto.getId(), "saida", item.getQuantidade(),
                "Venda - Pedido #" + item.getPedidoId(), Date.valueOf(LocalDate.now())));
    }

    @Transactional
    public int removerItemPedido(int itemId) {
        if (itemPedidoRepository == null || produtoRepository == null || movimentacaoRepository == null) {
            throw new IllegalStateException("Persistência JPA não disponível para remover item.");
        }
        ItemPedidoJpaEntity item = itemPedidoRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado"));
        int pedidoId = item.getPedido().getId();
        ProdutoJpaEntity produto = item.getProduto();
        produto.adicionarQuantidade(item.getQuantidade());
        produtoRepository.save(produto);
        movimentacaoRepository.save(new MovimentacaoEstoqueJpaEntity(
                produto.getId(), "entrada", item.getQuantidade(),
                "Estorno - Cancelamento Item Pedido #" + pedidoId, Date.valueOf(LocalDate.now())));
        itemPedidoRepository.deleteById(itemId);
        return pedidoId;
    }

    public List<String> listarStatusDisponiveis() {
        if (pedidoRepository != null) {
            return pedidoRepository.findDistinctStatus();
        }
        if (pedidoDAO != null) {
            try {
                return pedidoDAO.listarStatusDisponiveis();
            } catch (SQLException e) {
                return java.util.Collections.emptyList();
            }
        }
        return java.util.Collections.emptyList();
    }

    private ResultadoCancelamento cancelarPedidoComJpa(int pedidoId,
                                                       UsuarioOperacao solicitante,
                                                       String justificativa,
                                                       Collection<String> autoridades) {
        validarJustificativa(justificativa);
        validarSolicitante(solicitante);

        PedidoJpaEntity pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido nao encontrado."));

        String statusAtual = normalizarStatusPedido(pedido.getStatus());
        if (PedidoDAO.STATUS_PEDIDO_CANCELADO.equals(statusAtual)) {
            throw new IllegalStateException("Pedido ja cancelado. Nova tentativa bloqueada por idempotencia.");
        }

        List<ItemPedidoJpaEntity> itens = itemPedidoRepository.findByPedidoId(pedidoId);
        validarItensJpa(itens);

        int quantidadeTotal = somarQuantidadeJpa(itens);
        int limite = buscarLimiteQuantidadeCancelamentoJpa();
        boolean exigeAprovacao = quantidadeTotal > limite;
        boolean usuarioAprovador = possuiAlcadaAprovacao(autoridades);

        if (PedidoDAO.STATUS_PEDIDO_CANCELAMENTO_PENDENTE.equals(statusAtual)) {
            if (!usuarioAprovador) {
                throw new IllegalStateException("Pedido ja possui cancelamento pendente de aprovacao.");
            }
            aplicarCancelamentoJpa(
                    pedido,
                    itens,
                    pedido.getCancelamentoSolicitanteId() != null ? pedido.getCancelamentoSolicitanteId() : solicitante.getId(),
                    pedido.getCancelamentoSolicitanteNome() != null ? pedido.getCancelamentoSolicitanteNome() : solicitante.getNome(),
                    pedido.getJustificativaCancelamento() != null ? pedido.getJustificativaCancelamento() : justificativa.trim(),
                    solicitante.getId(),
                    solicitante.getNome()
            );
            return ResultadoCancelamento.cancelado(pedidoId, quantidadeTotal, limite, true);
        }

        if (!PedidoDAO.STATUS_PEDIDO_PENDENTE.equals(statusAtual)
                && !PedidoDAO.STATUS_PEDIDO_CONCLUIDO.equals(statusAtual)) {
            throw new IllegalStateException("Pedido deve estar PENDENTE ou CONCLUIDO para cancelamento.");
        }

        if (exigeAprovacao && !usuarioAprovador) {
            pedido.setStatus(PedidoDAO.STATUS_PEDIDO_CANCELAMENTO_PENDENTE);
            pedido.setCancelamentoSolicitanteId(solicitante.getId());
            pedido.setCancelamentoSolicitanteNome(solicitante.getNome());
            pedido.setJustificativaCancelamento(justificativa.trim());
            pedido.setDataCancelamento(Timestamp.valueOf(LocalDateTime.now()));
            pedidoRepository.save(pedido);
            return ResultadoCancelamento.pendenteAprovacao(pedidoId, quantidadeTotal, limite);
        }

        Integer aprovadorId = exigeAprovacao ? solicitante.getId() : null;
        String aprovadorNome = exigeAprovacao ? solicitante.getNome() : null;
        aplicarCancelamentoJpa(pedido, itens, solicitante.getId(), solicitante.getNome(), justificativa.trim(), aprovadorId, aprovadorNome);
        return ResultadoCancelamento.cancelado(pedidoId, quantidadeTotal, limite, exigeAprovacao);
    }

    private ResultadoCancelamento cancelarPedidoComDao(int pedidoId,
                                                       UsuarioOperacao solicitante,
                                                       String justificativa,
                                                       Collection<String> autoridades) throws SQLException {
        validarJustificativa(justificativa);
        validarSolicitante(solicitante);

        Pedido pedido = pedidoDAO.buscarPorId(pedidoId);
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido nao encontrado.");
        }

        String statusAtual = normalizarStatusPedido(pedido.getStatus());
        if (PedidoDAO.STATUS_PEDIDO_CANCELADO.equals(statusAtual)) {
            throw new IllegalStateException("Pedido ja cancelado. Nova tentativa bloqueada por idempotencia.");
        }

        List<ItemPedido> itens = itemPedidoDAO.listarPorPedido(pedidoId);
        validarItens(itens);

        int quantidadeTotal = somarQuantidade(itens);
        int limite = pedidoDAO.buscarLimiteQuantidadeCancelamento();
        boolean exigeAprovacao = quantidadeTotal > limite;
        boolean usuarioAprovador = possuiAlcadaAprovacao(autoridades);

        if (PedidoDAO.STATUS_PEDIDO_CANCELAMENTO_PENDENTE.equals(statusAtual)) {
            if (!usuarioAprovador) {
                throw new IllegalStateException("Pedido ja possui cancelamento pendente de aprovacao.");
            }
            pedidoDAO.cancelarComEstorno(
                    pedidoId,
                    itens,
                    pedido.getCancelamentoSolicitanteId() != null ? pedido.getCancelamentoSolicitanteId() : solicitante.getId(),
                    pedido.getCancelamentoSolicitanteNome() != null ? pedido.getCancelamentoSolicitanteNome() : solicitante.getNome(),
                    pedido.getJustificativaCancelamento() != null ? pedido.getJustificativaCancelamento() : justificativa.trim(),
                    solicitante.getId(),
                    solicitante.getNome()
            );
            return ResultadoCancelamento.cancelado(pedidoId, quantidadeTotal, limite, true);
        }

        if (!PedidoDAO.STATUS_PEDIDO_PENDENTE.equals(statusAtual)
                && !PedidoDAO.STATUS_PEDIDO_CONCLUIDO.equals(statusAtual)) {
            throw new IllegalStateException("Pedido deve estar PENDENTE ou CONCLUIDO para cancelamento.");
        }

        if (exigeAprovacao && !usuarioAprovador) {
            pedidoDAO.registrarCancelamentoPendente(
                    pedidoId,
                    solicitante.getId(),
                    solicitante.getNome(),
                    justificativa.trim()
            );
            return ResultadoCancelamento.pendenteAprovacao(pedidoId, quantidadeTotal, limite);
        }

        Integer aprovadorId = exigeAprovacao ? solicitante.getId() : null;
        String aprovadorNome = exigeAprovacao ? solicitante.getNome() : null;
        pedidoDAO.cancelarComEstorno(
                pedidoId,
                itens,
                solicitante.getId(),
                solicitante.getNome(),
                justificativa.trim(),
                aprovadorId,
                aprovadorNome
        );
        return ResultadoCancelamento.cancelado(pedidoId, quantidadeTotal, limite, exigeAprovacao);
    }

    private void aplicarCancelamentoJpa(PedidoJpaEntity pedido,
                                        List<ItemPedidoJpaEntity> itens,
                                        int solicitanteId,
                                        String solicitanteNome,
                                        String justificativa,
                                        Integer aprovadorId,
                                        String aprovadorNome) {
        for (ItemPedidoJpaEntity item : itens) {
            ProdutoJpaEntity produto = item.getProduto();
            produto.adicionarQuantidade(item.getQuantidade());
            produtoRepository.save(produto);

            movimentacaoRepository.save(new MovimentacaoEstoqueJpaEntity(
                    produto.getId(),
                    "entrada",
                    item.getQuantidade(),
                    "EntradaPorCancelamento - Pedido #" + pedido.getId(),
                    Date.valueOf(LocalDate.now())
            ));
        }

        pedido.setStatus(PedidoDAO.STATUS_PEDIDO_CANCELADO);
        pedido.setCancelamentoSolicitanteId(solicitanteId);
        pedido.setCancelamentoSolicitanteNome(solicitanteNome);
        pedido.setJustificativaCancelamento(justificativa);
        pedido.setDataCancelamento(Timestamp.valueOf(LocalDateTime.now()));
        if (aprovadorId != null && aprovadorId > 0) {
            pedido.setCancelamentoAprovadorId(aprovadorId);
            pedido.setCancelamentoAprovadorNome(aprovadorNome);
            pedido.setDataAprovacaoCancelamento(Timestamp.valueOf(LocalDateTime.now()));
        }
        pedidoRepository.save(pedido);
    }

    public String normalizarStatusPedido(String status) {
        if (status == null || status.trim().isEmpty()) {
            return PedidoDAO.STATUS_PEDIDO_PENDENTE;
        }
        String normalizado = status.trim().toUpperCase();
        if ("CONCLUÍDO".equals(normalizado)) {
            return PedidoDAO.STATUS_PEDIDO_CONCLUIDO;
        }
        return normalizado;
    }

    public boolean possuiAlcadaAprovacao(Collection<String> autoridades) {
        if (autoridades == null) {
            return false;
        }
        for (String autoridade : autoridades) {
            if (autoridade == null) {
                continue;
            }
            String valor = autoridade.toUpperCase();
            if (valor.equals("ROLE_DIRETOR")
                    || valor.equals("ROLE_GERENTE")
                    || valor.equals("ROLE_GERENTE_OPERACIONAL")) {
                return true;
            }
        }
        return false;
    }

    private void validarJustificativa(String justificativa) {
        if (justificativa == null) {
            throw new IllegalArgumentException("Informe a justificativa do cancelamento.");
        }
        int tamanho = justificativa.trim().length();
        if (tamanho < 10 || tamanho > 300) {
            throw new IllegalArgumentException("A justificativa deve ter entre 10 e 300 caracteres.");
        }
    }

    private void validarSolicitante(UsuarioOperacao solicitante) {
        if (solicitante == null || solicitante.getId() <= 0 || solicitante.getNome() == null
                || solicitante.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o solicitante do cancelamento.");
        }
    }

    private void validarItens(List<ItemPedido> itens) {
        if (itens == null || itens.isEmpty()) {
            throw new IllegalStateException("Pedido sem itens para estorno.");
        }
        for (ItemPedido item : itens) {
            if (item.getQuantidade() <= 0) {
                throw new IllegalStateException("Todos os itens do pedido devem possuir quantidade maior que zero.");
            }
        }
    }

    private void validarItensJpa(List<ItemPedidoJpaEntity> itens) {
        if (itens == null || itens.isEmpty()) {
            throw new IllegalStateException("Pedido sem itens para estorno.");
        }
        for (ItemPedidoJpaEntity item : itens) {
            if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
                throw new IllegalStateException("Todos os itens do pedido devem possuir quantidade maior que zero.");
            }
        }
    }

    private int somarQuantidade(List<ItemPedido> itens) {
        int total = 0;
        for (ItemPedido item : itens) {
            total += item.getQuantidade();
        }
        return total;
    }

    private int somarQuantidadeJpa(List<ItemPedidoJpaEntity> itens) {
        return itens.stream().mapToInt(ItemPedidoJpaEntity::getQuantidade).sum();
    }

    private int buscarLimiteQuantidadeCancelamentoJpa() {
        return parametroCancelamentoRepository.findFirstByOrderByIdAsc()
                .map(ParametroCancelamentoJpaEntity::getLimiteQuantidadeSemAprovacao)
                .orElse(10);
    }

    public static class UsuarioOperacao {
        private final int id;
        private final String nome;

        public UsuarioOperacao(int id, String nome) {
            this.id = id;
            this.nome = nome;
        }

        public int getId() {
            return id;
        }

        public String getNome() {
            return nome;
        }
    }

    public static class ResultadoFechamentoPedido {
        private final int pedidoId;
        private final BigDecimal descontoFidelidade;
        private final Double descontoTotal;
        private final String faixaCliente;

        public ResultadoFechamentoPedido(int pedidoId, BigDecimal descontoFidelidade, Double descontoTotal, String faixaCliente) {
            this.pedidoId = pedidoId;
            this.descontoFidelidade = descontoFidelidade;
            this.descontoTotal = descontoTotal;
            this.faixaCliente = faixaCliente;
        }

        public int getPedidoId() { return pedidoId; }
        public BigDecimal getDescontoFidelidade() { return descontoFidelidade; }
        public Double getDescontoTotal() { return descontoTotal; }
        public String getFaixaCliente() { return faixaCliente; }
    }

    public static class ResultadoCancelamento {
        private final int pedidoId;
        private final String status;
        private final int quantidadeTotal;
        private final int limiteQuantidadeSemAprovacao;
        private final boolean exigiuAprovacao;

        private ResultadoCancelamento(int pedidoId,
                                      String status,
                                      int quantidadeTotal,
                                      int limiteQuantidadeSemAprovacao,
                                      boolean exigiuAprovacao) {
            this.pedidoId = pedidoId;
            this.status = status;
            this.quantidadeTotal = quantidadeTotal;
            this.limiteQuantidadeSemAprovacao = limiteQuantidadeSemAprovacao;
            this.exigiuAprovacao = exigiuAprovacao;
        }

        public static ResultadoCancelamento cancelado(int pedidoId, int quantidadeTotal, int limite, boolean exigiuAprovacao) {
            return new ResultadoCancelamento(pedidoId, PedidoDAO.STATUS_PEDIDO_CANCELADO, quantidadeTotal, limite, exigiuAprovacao);
        }

        public static ResultadoCancelamento pendenteAprovacao(int pedidoId, int quantidadeTotal, int limite) {
            return new ResultadoCancelamento(
                    pedidoId,
                    PedidoDAO.STATUS_PEDIDO_CANCELAMENTO_PENDENTE,
                    quantidadeTotal,
                    limite,
                    true
            );
        }

        public int getPedidoId() {
            return pedidoId;
        }

        public String getStatus() {
            return status;
        }

        public int getQuantidadeTotal() {
            return quantidadeTotal;
        }

        public int getLimiteQuantidadeSemAprovacao() {
            return limiteQuantidadeSemAprovacao;
        }

        public boolean isExigiuAprovacao() {
            return exigiuAprovacao;
        }
    }
}
