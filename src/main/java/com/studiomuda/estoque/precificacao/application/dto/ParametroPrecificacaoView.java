package com.studiomuda.estoque.precificacao.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ParametroPrecificacaoView {
    private final Long id;
    private final BigDecimal margemMinimaGlobal;
    private final BigDecimal descontoMaximoGlobal;
    private final BigDecimal margemPadraoLucro;
    private final BigDecimal impostoPadraoPercentual;
    private final BigDecimal despesaOperacionalPadraoPercentual;
    private final LocalDateTime atualizadoEm;

    public ParametroPrecificacaoView(Long id, BigDecimal margemMinimaGlobal, BigDecimal descontoMaximoGlobal,
                                     BigDecimal margemPadraoLucro, BigDecimal impostoPadraoPercentual,
                                     BigDecimal despesaOperacionalPadraoPercentual, LocalDateTime atualizadoEm) {
        this.id = id;
        this.margemMinimaGlobal = margemMinimaGlobal;
        this.descontoMaximoGlobal = descontoMaximoGlobal;
        this.margemPadraoLucro = margemPadraoLucro;
        this.impostoPadraoPercentual = impostoPadraoPercentual;
        this.despesaOperacionalPadraoPercentual = despesaOperacionalPadraoPercentual;
        this.atualizadoEm = atualizadoEm;
    }

    public Long getId() { return id; }
    public BigDecimal getMargemMinimaGlobal() { return margemMinimaGlobal; }
    public BigDecimal getDescontoMaximoGlobal() { return descontoMaximoGlobal; }
    public BigDecimal getMargemPadraoLucro() { return margemPadraoLucro; }
    public BigDecimal getImpostoPadraoPercentual() { return impostoPadraoPercentual; }
    public BigDecimal getDespesaOperacionalPadraoPercentual() { return despesaOperacionalPadraoPercentual; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
