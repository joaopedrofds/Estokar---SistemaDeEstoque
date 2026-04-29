package com.studiomuda.estoque.application.funcionario;

import com.studiomuda.estoque.application.funcionario.dto.AtualizarFuncionarioCommand;
import com.studiomuda.estoque.domain.funcionario.Cargo;
import com.studiomuda.estoque.domain.funcionario.Endereco;
import com.studiomuda.estoque.domain.funcionario.Funcionario;
import com.studiomuda.estoque.domain.funcionario.FuncionarioRepository;
import com.studiomuda.estoque.domain.funcionario.exceptions.FuncionarioNaoEncontradoException;
import org.springframework.stereotype.Service;

@Service
public class AtualizarFuncionarioUseCase {
    private final FuncionarioRepository funcionarioRepository;

    public AtualizarFuncionarioUseCase(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public void executar(AtualizarFuncionarioCommand cmd) {
        Funcionario existente = funcionarioRepository.buscarPorId(cmd.id())
                .orElseThrow(() -> new FuncionarioNaoEncontradoException(cmd.id()));

        Cargo cargo = Cargo.desdeRotulo(cmd.cargo());
        Endereco endereco = new Endereco(cmd.cep(), cmd.rua(), cmd.numero(),
                cmd.bairro(), cmd.cidade(), cmd.estado());
        existente.atualizarDados(cmd.nome(), cargo, cmd.telefone(), endereco);
        if (cmd.ativo()) existente.ativar(); else existente.desativar();

        funcionarioRepository.atualizar(existente);
    }
}
