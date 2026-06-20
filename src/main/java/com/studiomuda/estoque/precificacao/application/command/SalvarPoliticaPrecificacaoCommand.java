package com.studiomuda.estoque.precificacao.application.command;

import java.math.BigDecimal;

public class SalvarPoliticaPrecificacaoCommand {
    private Long id;
    private int produtoId;
    private BigDecimal margemLucroDesejada;
    private BigDecimal aliquotaImpostos;
    private BigDecimal percentualDespesasOperacionais;
    private BigDecimal descontoMaximoPermitido;
    private boolean ativa = true;
    private String observacao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }
    public BigDecimal getMargemLucroDesejada() { return margemLucroDesejada; }
    public void setMargemLucroDesejada(BigDecimal margemLucroDesejada) { this.margemLucroDesejada = margemLucroDesejada; }
    public BigDecimal getAliquotaImpostos() { return aliquotaImpostos; }
    public void setAliquotaImpostos(BigDecimal aliquotaImpostos) { this.aliquotaImpostos = aliquotaImpostos; }
    public BigDecimal getPercentualDespesasOperacionais() { return percentualDespesasOperacionais; }
    public void setPercentualDespesasOperacionais(BigDecimal percentualDespesasOperacionais) { this.percentualDespesasOperacionais = percentualDespesasOperacionais; }
    public BigDecimal getDescontoMaximoPermitido() { return descontoMaximoPermitido; }
    public void setDescontoMaximoPermitido(BigDecimal descontoMaximoPermitido) { this.descontoMaximoPermitido = descontoMaximoPermitido; }
    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
