package com.studiomuda.estoque.domain.estoque;

public class MovimentacaoEstoqueComProduto {
    private final MovimentacaoEstoque movimentacao;
    private final String produtoNome;

    public MovimentacaoEstoqueComProduto(MovimentacaoEstoque movimentacao, String produtoNome) {
        this.movimentacao = movimentacao;
        this.produtoNome = produtoNome;
    }

    public MovimentacaoEstoque movimentacao() { return movimentacao; }
    public String produtoNome() { return produtoNome; }
}
