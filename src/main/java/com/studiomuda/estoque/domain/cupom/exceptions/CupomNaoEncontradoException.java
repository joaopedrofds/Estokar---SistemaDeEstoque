package com.studiomuda.estoque.domain.cupom.exceptions;

public class CupomNaoEncontradoException extends RuntimeException {
    public CupomNaoEncontradoException(int id) {
        super("Cupom não encontrado: " + id);
    }
    public CupomNaoEncontradoException(String codigo) {
        super("Cupom não encontrado: " + codigo);
    }
}
