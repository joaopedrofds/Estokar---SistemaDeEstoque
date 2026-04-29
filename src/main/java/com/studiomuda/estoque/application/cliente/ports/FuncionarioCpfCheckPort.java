package com.studiomuda.estoque.application.cliente.ports;

import com.studiomuda.estoque.domain.cliente.CpfCnpj;

public interface FuncionarioCpfCheckPort {
    boolean existeFuncionarioComCpf(CpfCnpj cpfCnpj);
}
