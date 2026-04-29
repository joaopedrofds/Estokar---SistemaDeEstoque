package com.studiomuda.estoque.domain.cliente.exceptions;

public class ClienteJaExisteException extends RuntimeException {
    public ClienteJaExisteException(String message) {
        super(message);
    }
}
