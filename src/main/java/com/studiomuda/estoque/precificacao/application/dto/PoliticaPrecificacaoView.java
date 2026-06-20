package com.studiomuda.estoque.precificacao.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PoliticaPrecificacaoView {
    private final Long id;
    private final int produtoId;
    private final String produtoNome;
    private final BigDecimal margemLucroDesejada;
    private final BigDecimal aliquotaImpostos;
    private final BigDecimal percentualDespesasOperacionais;
    private final BigDecimal descontoMaximoPermitido;
    private final boolean ativa;
    private final String observacao;
    private final LocalDateTime atualizadoEm;

    public PoliticaPrecificacaoView(Long id, int produtoId, String produtoNome, BigDecimal margemLucroDesejada,
                                    BigDecimal aliquotaImpostos, BigDecimal percentualDespesasOperacionais,
                                    BigDecimal descontoMaximoPermitido, boolean ativa, String observacao,
                                    LocalDateTime atualizadoEm) {
        this.id = id;
        this.produtoId = produtoId;
        this.produtoNome = produtoNome;
        this.margemLucroDesejada = margemLucroDesejada;
        this.aliquotaImpostos = aliquotaImpostos;
        this.percentualDespesasOperacionais = percentualDespesasOperacionais;
        this.descontoMaximoPermitido = descontoMaximoPermitido;
        this.ativa = ativa;
        this.observacao = observacao;
        this.atualizadoEm = atualizadoEm;
    }

    public Long getId() { return id; }
    public int getProdutoId() { return produtoId; }
    public String getProdutoNome() { return produtoNome; }
    public BigDecimal getMargemLucroDesejada() { return margemLucroDesejada; }
    public BigDecimal getAliquotaImpostos() { return aliquotaImpostos; }
    public BigDecimal getPercentualDespesasOperacionais() { return percentualDespesasOperacionais; }
    public BigDecimal getDescontoMaximoPermitido() { return descontoMaximoPermitido; }
    public boolean isAtiva() { return ativa; }
    public String getObservacao() { return observacao; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
