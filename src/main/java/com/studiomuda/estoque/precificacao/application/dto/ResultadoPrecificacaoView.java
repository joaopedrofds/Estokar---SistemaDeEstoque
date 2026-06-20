package com.studiomuda.estoque.precificacao.application.dto;

import com.studiomuda.estoque.precificacao.domain.model.StatusPrecificacao;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ResultadoPrecificacaoView {
    private final Long simulacaoId;
    private final int produtoId;
    private final String produtoNome;
    private final BigDecimal precoAtual;
    private final BigDecimal custoCompra;
    private final BigDecimal valorImpostos;
    private final BigDecimal valorDespesasOperacionais;
    private final BigDecimal custoTotal;
    private final BigDecimal precoSugerido;
    private final BigDecimal precoMinimoPermitido;
    private final BigDecimal margemLucroDesejada;
    private final BigDecimal margemMinimaGlobal;
    private final BigDecimal margemReal;
    private final BigDecimal descontoMaximoSolicitado;
    private final BigDecimal descontoMaximoEfetivo;
    private final StatusPrecificacao status;
    private final String justificativa;
    private final String usuarioResponsavel;
    private final boolean aplicado;
    private final LocalDateTime dataSimulacao;
    private final List<ComponenteCalculoView> componentes;

    public ResultadoPrecificacaoView(Long simulacaoId, int produtoId, String produtoNome, BigDecimal precoAtual,
                                     BigDecimal custoCompra, BigDecimal valorImpostos,
                                     BigDecimal valorDespesasOperacionais, BigDecimal custoTotal,
                                     BigDecimal precoSugerido, BigDecimal precoMinimoPermitido,
                                     BigDecimal margemLucroDesejada, BigDecimal margemMinimaGlobal,
                                     BigDecimal margemReal, BigDecimal descontoMaximoSolicitado,
                                     BigDecimal descontoMaximoEfetivo, StatusPrecificacao status,
                                     String justificativa, String usuarioResponsavel, boolean aplicado,
                                     LocalDateTime dataSimulacao, List<ComponenteCalculoView> componentes) {
        this.simulacaoId = simulacaoId;
        this.produtoId = produtoId;
        this.produtoNome = produtoNome;
        this.precoAtual = precoAtual;
        this.custoCompra = custoCompra;
        this.valorImpostos = valorImpostos;
        this.valorDespesasOperacionais = valorDespesasOperacionais;
        this.custoTotal = custoTotal;
        this.precoSugerido = precoSugerido;
        this.precoMinimoPermitido = precoMinimoPermitido;
        this.margemLucroDesejada = margemLucroDesejada;
        this.margemMinimaGlobal = margemMinimaGlobal;
        this.margemReal = margemReal;
        this.descontoMaximoSolicitado = descontoMaximoSolicitado;
        this.descontoMaximoEfetivo = descontoMaximoEfetivo;
        this.status = status;
        this.justificativa = justificativa;
        this.usuarioResponsavel = usuarioResponsavel;
        this.aplicado = aplicado;
        this.dataSimulacao = dataSimulacao;
        this.componentes = componentes;
    }

    public Long getSimulacaoId() { return simulacaoId; }
    public int getProdutoId() { return produtoId; }
    public String getProdutoNome() { return produtoNome; }
    public BigDecimal getPrecoAtual() { return precoAtual; }
    public BigDecimal getCustoCompra() { return custoCompra; }
    public BigDecimal getValorImpostos() { return valorImpostos; }
    public BigDecimal getValorDespesasOperacionais() { return valorDespesasOperacionais; }
    public BigDecimal getCustoTotal() { return custoTotal; }
    public BigDecimal getPrecoSugerido() { return precoSugerido; }
    public BigDecimal getPrecoMinimoPermitido() { return precoMinimoPermitido; }
    public BigDecimal getMargemLucroDesejada() { return margemLucroDesejada; }
    public BigDecimal getMargemMinimaGlobal() { return margemMinimaGlobal; }
    public BigDecimal getMargemReal() { return margemReal; }
    public BigDecimal getDescontoMaximoSolicitado() { return descontoMaximoSolicitado; }
    public BigDecimal getDescontoMaximoEfetivo() { return descontoMaximoEfetivo; }
    public StatusPrecificacao getStatus() { return status; }
    public String getJustificativa() { return justificativa; }
    public String getUsuarioResponsavel() { return usuarioResponsavel; }
    public boolean isAplicado() { return aplicado; }
    public LocalDateTime getDataSimulacao() { return dataSimulacao; }
    public List<ComponenteCalculoView> getComponentes() { return componentes; }

    public boolean isAprovado() { return status == StatusPrecificacao.APROVADO; }
    public boolean isBloqueado() { return status == StatusPrecificacao.BLOQUEADO_DESCONTO || status == StatusPrecificacao.BLOQUEADO_MARGEM; }

    public BigDecimal getVariacaoPercentual() {
        if (precoAtual == null || precoAtual.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return precoSugerido.subtract(precoAtual)
                .multiply(BigDecimal.valueOf(100))
                .divide(precoAtual, 2, java.math.RoundingMode.HALF_UP);
    }
}
