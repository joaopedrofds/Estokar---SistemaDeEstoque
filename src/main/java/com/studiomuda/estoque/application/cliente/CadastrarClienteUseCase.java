package com.studiomuda.estoque.application.cliente;

import com.studiomuda.estoque.application.cliente.dto.CadastrarClienteCommand;
import com.studiomuda.estoque.application.cliente.ports.FuncionarioCpfCheckPort;
import com.studiomuda.estoque.domain.cliente.Cliente;
import com.studiomuda.estoque.domain.cliente.ClienteRepository;
import com.studiomuda.estoque.domain.cliente.CpfCnpj;
import com.studiomuda.estoque.domain.cliente.Endereco;
import com.studiomuda.estoque.domain.cliente.TipoPessoa;
import com.studiomuda.estoque.domain.cliente.exceptions.ClienteJaExisteException;
import org.springframework.stereotype.Service;

@Service
public class CadastrarClienteUseCase {
    private final ClienteRepository clienteRepository;
    private final FuncionarioCpfCheckPort funcionarioCpfCheck;

    public CadastrarClienteUseCase(ClienteRepository clienteRepository,
                                   FuncionarioCpfCheckPort funcionarioCpfCheck) {
        this.clienteRepository = clienteRepository;
        this.funcionarioCpfCheck = funcionarioCpfCheck;
    }

    public Cliente executar(CadastrarClienteCommand cmd) {
        TipoPessoa tipo = TipoPessoa.fromCodigo(cmd.tipo());
        CpfCnpj cpfCnpj = CpfCnpj.of(cmd.cpfCnpj(), tipo);

        if (clienteRepository.buscarPorCpfCnpj(cpfCnpj).isPresent()
                || funcionarioCpfCheck.existeFuncionarioComCpf(cpfCnpj)) {
            throw new ClienteJaExisteException(
                    "Já existe um cliente ou funcionário com esse CPF/CNPJ cadastrado.");
        }

        Endereco endereco = new Endereco(cmd.cep(), cmd.rua(), cmd.numero(),
                cmd.bairro(), cmd.cidade(), cmd.estado());
        Cliente cliente = Cliente.novo(cmd.nome(), cpfCnpj, cmd.telefone(), cmd.email(),
                endereco, cmd.dataNascimento());
        if (!cmd.ativo()) cliente.desativar();
        return clienteRepository.salvar(cliente);
    }
}
