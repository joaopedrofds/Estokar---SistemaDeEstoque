package com.studiomuda.estoque.precificacao.application.command;

import java.math.BigDecimal;

public class SimularPrecoCommand {
    private int produtoId;
    private BigDecimal custoCompra;
    private BigDecimal margemLucroDesejada;
    private BigDecimal aliquotaImpostos;
    private BigDecimal percentualDespesasOperacionais;
    private BigDecimal descontoMaximoPermitido;

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }
    public BigDecimal getCustoCompra() { return custoCompra; }
    public void setCustoCompra(BigDecimal custoCompra) { this.custoCompra = custoCompra; }
    public BigDecimal getMargemLucroDesejada() { return margemLucroDesejada; }
    public void setMargemLucroDesejada(BigDecimal margemLucroDesejada) { this.margemLucroDesejada = margemLucroDesejada; }
    public BigDecimal getAliquotaImpostos() { return aliquotaImpostos; }
    public void setAliquotaImpostos(BigDecimal aliquotaImpostos) { this.aliquotaImpostos = aliquotaImpostos; }
    public BigDecimal getPercentualDespesasOperacionais() { return percentualDespesasOperacionais; }
    public void setPercentualDespesasOperacionais(BigDecimal percentualDespesasOperacionais) { this.percentualDespesasOperacionais = percentualDespesasOperacionais; }
    public BigDecimal getDescontoMaximoPermitido() { return descontoMaximoPermitido; }
    public void setDescontoMaximoPermitido(BigDecimal descontoMaximoPermitido) { this.descontoMaximoPermitido = descontoMaximoPermitido; }
}
