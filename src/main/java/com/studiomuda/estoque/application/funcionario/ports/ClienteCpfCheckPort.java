package com.studiomuda.estoque.application.funcionario.ports;

public interface ClienteCpfCheckPort {
    boolean existeClienteComCpfCnpj(String digitos);
}
