package com.studiomuda.estoque.domain.cliente.exceptions;

public class ClienteNaoEncontradoException extends RuntimeException {
    public ClienteNaoEncontradoException(int id) {
        super("Cliente não encontrado: " + id);
    }
}
