package com.studiomuda.estoque.infrastructure.persistence.cliente;

import com.studiomuda.estoque.application.cliente.ports.FuncionarioCpfCheckPort;
import com.studiomuda.estoque.domain.funcionario.Cpf;
import com.studiomuda.estoque.domain.funcionario.FuncionarioRepository;
import org.springframework.stereotype.Component;

@Component
public class FuncionarioCpfCheckAdapter implements FuncionarioCpfCheckPort {
    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioCpfCheckAdapter(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    @Override
    public boolean existeFuncionarioComCpf(String digitos) {
        if (digitos == null) return false;
        try {
            return funcionarioRepository.buscarPorCpf(Cpf.of(digitos)).isPresent();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
