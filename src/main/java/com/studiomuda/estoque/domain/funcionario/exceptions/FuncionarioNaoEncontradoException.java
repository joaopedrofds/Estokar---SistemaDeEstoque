package com.studiomuda.estoque.domain.funcionario.exceptions;

public class FuncionarioNaoEncontradoException extends RuntimeException {
    public FuncionarioNaoEncontradoException(int id) {
        super("Funcionário não encontrado: " + id);
    }
}
