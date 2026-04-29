package com.studiomuda.estoque.domain.produto;

public class Produto {
    private final int id;
    private String nome;
    private String descricao;
    private TipoProduto tipo;
    private int quantidade;
    private double valor;

    public Produto(int id, String nome, String descricao, TipoProduto tipo, int quantidade, double valor) {
        this.id = id;
        atualizarNome(nome);
        this.descricao = descricao;
        this.tipo = exigir(tipo, "Tipo");
        atualizarQuantidade(quantidade);
        atualizarValor(valor);
    }

    public static Produto novo(String nome, String descricao, TipoProduto tipo, int quantidade, double valor) {
        return new Produto(0, nome, descricao, tipo, quantidade, valor);
    }

    public void atualizarDados(String nome, String descricao, TipoProduto tipo, int quantidade, double valor) {
        atualizarNome(nome);
        this.descricao = descricao;
        this.tipo = exigir(tipo, "Tipo");
        atualizarQuantidade(quantidade);
        atualizarValor(valor);
    }

    public void incrementarEstoque(int delta) {
        if (delta < 0) throw new IllegalArgumentException("Incremento deve ser não-negativo.");
        this.quantidade += delta;
    }

    public void decrementarEstoque(int delta) {
        if (delta < 0) throw new IllegalArgumentException("Decremento deve ser não-negativo.");
        if (delta > this.quantidade) {
            throw new IllegalStateException("Estoque insuficiente para o produto " + nome);
        }
        this.quantidade -= delta;
    }

    public StatusEstoque statusEstoque() {
        return StatusEstoque.desdeQuantidade(quantidade);
    }

    private void atualizarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do produto é obrigatório.");
        }
        this.nome = nome.trim();
    }

    private void atualizarQuantidade(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa.");
        }
        this.quantidade = quantidade;
    }

    private void atualizarValor(double valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("Valor não pode ser negativo.");
        }
        this.valor = valor;
    }

    private static <T> T exigir(T valor, String campo) {
        if (valor == null) throw new IllegalArgumentException("O " + campo + " é obrigatório.");
        return valor;
    }

    public int id() { return id; }
    public String nome() { return nome; }
    public String descricao() { return descricao; }
    public TipoProduto tipo() { return tipo; }
    public int quantidade() { return quantidade; }
    public double valor() { return valor; }
}
