package com.studiomuda.estoque.application.funcionario;

import com.studiomuda.estoque.application.funcionario.dto.CadastrarFuncionarioCommand;
import com.studiomuda.estoque.application.funcionario.ports.ClienteCpfCheckPort;
import com.studiomuda.estoque.domain.funcionario.Cargo;
import com.studiomuda.estoque.domain.funcionario.Cpf;
import com.studiomuda.estoque.domain.funcionario.Endereco;
import com.studiomuda.estoque.domain.funcionario.Funcionario;
import com.studiomuda.estoque.domain.funcionario.FuncionarioRepository;
import com.studiomuda.estoque.domain.funcionario.exceptions.FuncionarioJaExisteException;
import org.springframework.stereotype.Service;

@Service
public class CadastrarFuncionarioUseCase {
    private final FuncionarioRepository funcionarioRepository;
    private final ClienteCpfCheckPort clienteCpfCheck;

    public CadastrarFuncionarioUseCase(FuncionarioRepository funcionarioRepository,
                                       ClienteCpfCheckPort clienteCpfCheck) {
        this.funcionarioRepository = funcionarioRepository;
        this.clienteCpfCheck = clienteCpfCheck;
    }

    public Funcionario executar(CadastrarFuncionarioCommand cmd) {
        Cpf cpf = Cpf.of(cmd.cpf());
        Cargo cargo = Cargo.desdeRotulo(cmd.cargo());

        if (funcionarioRepository.buscarPorCpf(cpf).isPresent()
                || clienteCpfCheck.existeClienteComCpfCnpj(cpf.digitos())) {
            throw new FuncionarioJaExisteException(
                    "Já existe um cliente ou funcionário com esse CPF/CNPJ cadastrado.");
        }

        Endereco endereco = new Endereco(cmd.cep(), cmd.rua(), cmd.numero(),
                cmd.bairro(), cmd.cidade(), cmd.estado());
        Funcionario funcionario = Funcionario.novo(cmd.nome(), cpf, cargo,
                cmd.dataNascimento(), cmd.telefone(), endereco);
        if (!cmd.ativo()) funcionario.desativar();
        return funcionarioRepository.salvar(funcionario);
    }
}
