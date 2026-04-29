package com.studiomuda.estoque.application.funcionario;

import com.studiomuda.estoque.domain.funcionario.FuncionarioRepository;
import org.springframework.stereotype.Service;

@Service
public class RemoverFuncionarioUseCase {
    private final FuncionarioRepository funcionarioRepository;

    public RemoverFuncionarioUseCase(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public void executar(int id) {
        funcionarioRepository.desativar(id);
    }
}
