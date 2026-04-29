package com.studiomuda.estoque.domain.estoque.exceptions;

public class MovimentacaoNaoEncontradaException extends RuntimeException {
    public MovimentacaoNaoEncontradaException(int id) {
        super("Movimentação não encontrada: " + id);
    }
}
