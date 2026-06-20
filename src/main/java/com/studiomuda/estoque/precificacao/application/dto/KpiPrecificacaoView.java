package com.studiomuda.estoque.precificacao.application.dto;

import java.math.BigDecimal;

public class KpiPrecificacaoView {
    private final long totalProdutos;
    private final long politicasAtivas;
    private final long simulacoesAprovadas;
    private final long simulacoesBloqueadas;
    private final BigDecimal margemMedia;

    public KpiPrecificacaoView(long totalProdutos, long politicasAtivas, long simulacoesAprovadas,
                               long simulacoesBloqueadas, BigDecimal margemMedia) {
        this.totalProdutos = totalProdutos;
        this.politicasAtivas = politicasAtivas;
        this.simulacoesAprovadas = simulacoesAprovadas;
        this.simulacoesBloqueadas = simulacoesBloqueadas;
        this.margemMedia = margemMedia;
    }

    public long getTotalProdutos() { return totalProdutos; }
    public long getPoliticasAtivas() { return politicasAtivas; }
    public long getSimulacoesAprovadas() { return simulacoesAprovadas; }
    public long getSimulacoesBloqueadas() { return simulacoesBloqueadas; }
    public BigDecimal getMargemMedia() { return margemMedia; }
}
