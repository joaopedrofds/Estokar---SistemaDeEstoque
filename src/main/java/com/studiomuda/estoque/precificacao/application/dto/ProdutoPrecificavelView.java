package com.studiomuda.estoque.precificacao.application.dto;

import java.math.BigDecimal;

public class ProdutoPrecificavelView {
    private final int id;
    private final String nome;
    private final String tipo;
    private final int quantidade;
    private final BigDecimal precoAtual;
    private final BigDecimal custoCompra;
    private final boolean possuiPolitica;

    public ProdutoPrecificavelView(int id, String nome, String tipo, int quantidade,
                                   BigDecimal precoAtual, BigDecimal custoCompra, boolean possuiPolitica) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.precoAtual = precoAtual;
        this.custoCompra = custoCompra;
        this.possuiPolitica = possuiPolitica;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getTipo() { return tipo; }
    public int getQuantidade() { return quantidade; }
    public BigDecimal getPrecoAtual() { return precoAtual; }
    public BigDecimal getCustoCompra() { return custoCompra; }
    public boolean isPossuiPolitica() { return possuiPolitica; }
}
