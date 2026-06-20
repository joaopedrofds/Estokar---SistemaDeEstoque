package com.studiomuda.estoque.precificacao.application.command;

import java.math.BigDecimal;

public class SalvarParametrosPrecificacaoCommand {
    private Long id;
    private BigDecimal margemMinimaGlobal;
    private BigDecimal descontoMaximoGlobal;
    private BigDecimal margemPadraoLucro;
    private BigDecimal impostoPadraoPercentual;
    private BigDecimal despesaOperacionalPadraoPercentual;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getMargemMinimaGlobal() { return margemMinimaGlobal; }
    public void setMargemMinimaGlobal(BigDecimal margemMinimaGlobal) { this.margemMinimaGlobal = margemMinimaGlobal; }
    public BigDecimal getDescontoMaximoGlobal() { return descontoMaximoGlobal; }
    public void setDescontoMaximoGlobal(BigDecimal descontoMaximoGlobal) { this.descontoMaximoGlobal = descontoMaximoGlobal; }
    public BigDecimal getMargemPadraoLucro() { return margemPadraoLucro; }
    public void setMargemPadraoLucro(BigDecimal margemPadraoLucro) { this.margemPadraoLucro = margemPadraoLucro; }
    public BigDecimal getImpostoPadraoPercentual() { return impostoPadraoPercentual; }
    public void setImpostoPadraoPercentual(BigDecimal impostoPadraoPercentual) { this.impostoPadraoPercentual = impostoPadraoPercentual; }
    public BigDecimal getDespesaOperacionalPadraoPercentual() { return despesaOperacionalPadraoPercentual; }
    public void setDespesaOperacionalPadraoPercentual(BigDecimal despesaOperacionalPadraoPercentual) { this.despesaOperacionalPadraoPercentual = despesaOperacionalPadraoPercentual; }
}
