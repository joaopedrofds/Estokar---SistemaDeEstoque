package com.studiomuda.estoque.application.produto.dto;

public class SalvarProdutoCommand {
    private final int id;
    private final String nome;
    private final String descricao;
    private final String tipo;
    private final int quantidade;
    private final double valor;

    public SalvarProdutoCommand(int id, String nome, String descricao, String tipo, int quantidade, double valor) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.valor = valor;
    }

    public int id() { return id; }
    public String nome() { return nome; }
    public String descricao() { return descricao; }
    public String tipo() { return tipo; }
    public int quantidade() { return quantidade; }
    public double valor() { return valor; }
}
