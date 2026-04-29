package com.studiomuda.estoque.presentation.web.produto;

import com.studiomuda.estoque.domain.produto.Produto;

public class ProdutoView {
    private final int id;
    private final String nome;
    private final String descricao;
    private final String tipo;
    private final int quantidade;
    private final double valor;

    public ProdutoView(Produto p) {
        this.id = p.id();
        this.nome = p.nome();
        this.descricao = p.descricao();
        this.tipo = p.tipo().name();
        this.quantidade = p.quantidade();
        this.valor = p.valor();
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getTipo() { return tipo; }
    public int getQuantidade() { return quantidade; }
    public double getValor() { return valor; }
}
