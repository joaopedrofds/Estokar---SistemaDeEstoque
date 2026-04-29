package com.studiomuda.estoque.application.funcionario;

import com.studiomuda.estoque.domain.funcionario.Cpf;
import com.studiomuda.estoque.domain.funcionario.Funcionario;
import com.studiomuda.estoque.domain.funcionario.FuncionarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BuscarFuncionarioUseCase {
    private final FuncionarioRepository funcionarioRepository;

    public BuscarFuncionarioUseCase(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public Optional<Funcionario> porId(int id) {
        return funcionarioRepository.buscarPorId(id);
    }

    public Optional<Funcionario> porCpf(String cpfDigitos) {
        return funcionarioRepository.buscarPorCpf(Cpf.of(cpfDigitos));
    }
}
