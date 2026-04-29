package com.studiomuda.estoque.domain.produto.exceptions;

public class ProdutoNaoEncontradoException extends RuntimeException {
    public ProdutoNaoEncontradoException(int id) {
        super("Produto não encontrado: " + id);
    }
}
