package com.studiomuda.estoque.application.cliente.ports;

public interface FuncionarioCpfCheckPort {
    boolean existeFuncionarioComCpf(String digitos);
}
