package com.studiomuda.estoque.domain.funcionario.exceptions;

public class FuncionarioJaExisteException extends RuntimeException {
    public FuncionarioJaExisteException(String message) {
        super(message);
    }
}
