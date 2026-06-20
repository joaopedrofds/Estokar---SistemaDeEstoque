package com.studiomuda.estoque.service;

import com.studiomuda.estoque.jpa.entity.BeneficioCategoriaJpaEntity;
import com.studiomuda.estoque.jpa.entity.ClienteJpaEntity;
import com.studiomuda.estoque.jpa.entity.FaixaFidelidadeJpaEntity;
import com.studiomuda.estoque.jpa.entity.ItemPedidoJpaEntity;
import com.studiomuda.estoque.jpa.entity.PedidoJpaEntity;
import com.studiomuda.estoque.jpa.repository.AcaoRetencaoJpaRepository;
import com.studiomuda.estoque.jpa.repository.BeneficioCategoriaJpaRepository;
import com.studiomuda.estoque.jpa.repository.ClienteJpaRepository;
import com.studiomuda.estoque.jpa.repository.FaixaFidelidadeJpaRepository;
import com.studiomuda.estoque.jpa.repository.PedidoJpaRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
//@ConditionalOnBean(FaixaFidelidadeJpaRepository.class)
public class FidelidadeService {
    public static final String BENEFICIO_PERCENTUAL = "PERCENTUAL_DESCONTO";

    private final FaixaFidelidadeJpaRepository faixaRepository;
    private final BeneficioCategoriaJpaRepository beneficioRepository;
    private final ClienteJpaRepository clienteRepository;
    private final PedidoJpaRepository pedidoRepository;
    private final AcaoRetencaoJpaRepository acaoRetencaoRepository;

    public FidelidadeService(FaixaFidelidadeJpaRepository faixaRepository,
                             BeneficioCategoriaJpaRepository beneficioRepository,
                             ClienteJpaRepository clienteRepository,
                             PedidoJpaRepository pedidoRepository,
                             AcaoRetencaoJpaRepository acaoRetencaoRepository) {
        this.faixaRepository = faixaRepository;
        this.beneficioRepository = beneficioRepository;
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
        this.acaoRetencaoRepository = acaoRetencaoRepository;
    }

    @Transactional(readOnly = true)
    public List<FaixaFidelidadeJpaEntity> listarFaixas() {
        return faixaRepository.findAllByOrderByDiasMinimoAsc();
    }

    @Transactional(readOnly = true)
    public List<BeneficioCategoriaJpaEntity> listarBeneficios() {
        return beneficioRepository.findAllByOrderByFaixaDiasMinimoAsc();
    }

    @Transactional(readOnly = true)
    public List<ClienteJpaEntity> listarClientes() {
        return clienteRepository.findAllByOrderByNomeAsc();
    }

    @Transactional
    public FaixaFidelidadeJpaEntity salvarFaixa(FaixaFidelidadeJpaEntity faixa) {
        validarFaixa(faixa);
        if (faixaRepository.contarSobreposicoes(faixa.getId(), faixa.getDiasMinimo(), faixa.getDiasMaximo()) > 0) {
            throw new IllegalArgumentException("A janela informada intersecta outra faixa de fidelidade ativa.");
        }
        return faixaRepository.save(faixa);
    }

    @Transactional(readOnly = true)
    public BeneficioCategoriaJpaEntity buscarBeneficioPorId(Integer id) {
        return beneficioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Beneficio nao encontrado."));
    }

    @Transactional
    public BeneficioCategoriaJpaEntity alternarStatusBeneficio(Integer id) {
        BeneficioCategoriaJpaEntity beneficio = beneficioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Beneficio nao encontrado."));
        beneficio.setAtivo(!beneficio.getAtivo());
        return beneficioRepository.save(beneficio);
    }

    @Transactional
    public void excluirBeneficio(Integer id) {
        BeneficioCategoriaJpaEntity beneficio = beneficioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Beneficio nao encontrado."));

        long pedidosVinculados = pedidoRepository.countByBeneficioAplicadoId(id);
        if (pedidosVinculados > 0) {
            throw new IllegalStateException(
                    "Este beneficio ja foi aplicado em " + pedidosVinculados
                            + " pedido(s) e nao pode ser excluido. Use pausar/desativar em vez de excluir.");
        }

        beneficioRepository.delete(beneficio);
    }

    @Transactional
    public BeneficioCategoriaJpaEntity salvarBeneficio(BeneficioCategoriaJpaEntity beneficio, Integer faixaId) {
        FaixaFidelidadeJpaEntity faixa = faixaRepository.findById(faixaId)
                .orElseThrow(() -> new IllegalArgumentException("Faixa de fidelidade nao encontrada."));
        if (beneficio.getPercentualDesconto() == null
                || beneficio.getPercentualDesconto().compareTo(BigDecimal.ZERO) < 0
                || beneficio.getPercentualDesconto().compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("O percentual deve estar entre 0 e 100.");
        }
        beneficio.setFaixa(faixa);
        beneficio.setTipo(BENEFICIO_PERCENTUAL);
        return beneficioRepository.save(beneficio);
    }

    @Transactional
    public ClienteJpaEntity recalcularCategoria(Integer clienteId) {
        ClienteJpaEntity cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado."));
        List<Date> datas = pedidoRepository.listarDatasComprasConfirmadas(clienteId);
        double media = calcularMediaDias(datas);
        // Arredonda a média para inteiro (dias) antes de comparar com faixas de dias inteiros.
        // diasMinimo/diasMaximo são Integer na entidade, e a query usa o mesmo :param em
        // comparações contra ambos os lados — Hibernate 5.x infere o tipo do primeiro bind
        // resolvido e falha se receber double. Como faixas representam janelas de dias inteiros,
        // o arredondamento da média é semanticamente correto.
        int mediaInt = (int) Math.round(media);
        FaixaFidelidadeJpaEntity faixa = faixaRepository.buscarPorMedia(mediaInt).orElse(null);
        cliente.setMediaDiasCompras(media);
        cliente.setFaixaFidelidade(faixa);
        cliente.setDataRecalculoFidelidade(LocalDateTime.now());
        return clienteRepository.save(cliente);
    }

    @Transactional
    public BigDecimal aplicarBeneficio(PedidoJpaEntity pedido) {
        ClienteJpaEntity cliente = recalcularCategoria(pedido.getClienteId());
        if (cliente.getFaixaFidelidade() == null) {
            return BigDecimal.ZERO;
        }
        BeneficioCategoriaJpaEntity beneficio = beneficioRepository
                .findFirstByFaixaIdAndAtivoTrueAndTipoOrderByIdAsc(cliente.getFaixaFidelidade().getId(), BENEFICIO_PERCENTUAL)
                .orElse(null);
        if (beneficio == null || beneficio.getPercentualDesconto() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal subtotal = pedido.getItens().stream()
                .map(this::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal descontoFidelidade = subtotal
                .multiply(beneficio.getPercentualDesconto())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal descontoExistente = BigDecimal.valueOf(pedido.getValorDesconto() != null ? pedido.getValorDesconto() : 0.0);
        BigDecimal descontoTotal = descontoExistente.add(descontoFidelidade).min(subtotal);
        pedido.setBeneficioAplicado(beneficio);
        pedido.setValorDesconto(descontoTotal.doubleValue());
        return descontoFidelidade;
    }

    double calcularMediaDias(List<Date> datas) {
        if (datas == null || datas.size() < 2) {
            return 0.0;
        }
        long totalDias = 0;
        for (int i = 1; i < datas.size(); i++) {
            totalDias += Math.abs(ChronoUnit.DAYS.between(datas.get(i - 1).toLocalDate(), datas.get(i).toLocalDate()));
        }
        return BigDecimal.valueOf((double) totalDias / (datas.size() - 1))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private BigDecimal calcularSubtotal(ItemPedidoJpaEntity item) {
        if (item.getProduto() == null || item.getProduto().getValor() == null || item.getQuantidade() == null) {
            return BigDecimal.ZERO;
        }
        return item.getProduto().getValor().multiply(BigDecimal.valueOf(item.getQuantidade()));
    }

    @Transactional(readOnly = true)
    public FaixaFidelidadeJpaEntity buscarFaixaPorId(Integer id) {
        return faixaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Faixa de fidelidade nao encontrada."));
    }

    @Transactional
    public void excluirFaixa(Integer id) {
        FaixaFidelidadeJpaEntity faixa = faixaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Faixa de fidelidade nao encontrada."));

        long beneficiosVinculados = beneficioRepository.countByFaixaId(id);
        long clientesVinculados = clienteRepository.countByFaixaFidelidadeId(id);
        long acoesVinculadas = acaoRetencaoRepository.countByFaixaId(id);

        if (beneficiosVinculados > 0 || clientesVinculados > 0 || acoesVinculadas > 0) {
            throw new IllegalStateException(
                    "Esta faixa possui vinculos (" +
                            (beneficiosVinculados > 0 ? beneficiosVinculados + " beneficio(s), " : "") +
                            (clientesVinculados > 0 ? clientesVinculados + " cliente(s), " : "") +
                            (acoesVinculadas > 0 ? acoesVinculadas + " acao(oes) de retencao, " : "") +
                            "). Desative-a em vez de excluir, ou primeiro reatribua/exclua os vinculos.");
        }

        faixaRepository.delete(faixa);
    }

    @Transactional
    public FaixaFidelidadeJpaEntity alternarStatusFaixa(Integer id) {
        FaixaFidelidadeJpaEntity faixa = faixaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Faixa de fidelidade nao encontrada."));
        faixa.setAtiva(!faixa.getAtiva());
        return faixaRepository.save(faixa);
    }

    private void validarFaixa(FaixaFidelidadeJpaEntity faixa) {
        if (faixa.getNome() == null || faixa.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o nome da faixa.");
        }
        if (faixa.getDiasMinimo() == null || faixa.getDiasMaximo() == null
                || faixa.getDiasMinimo() < 0 || faixa.getDiasMaximo() < faixa.getDiasMinimo()) {
            throw new IllegalArgumentException("Informe uma janela de dias valida.");
        }
    }
}
