package com.studiomuda.estoque.application.cliente;

import com.studiomuda.estoque.application.cliente.dto.AtualizarClienteCommand;
import com.studiomuda.estoque.application.cliente.ports.FuncionarioCpfCheckPort;
import com.studiomuda.estoque.domain.cliente.Cliente;
import com.studiomuda.estoque.domain.cliente.ClienteRepository;
import com.studiomuda.estoque.domain.cliente.CpfCnpj;
import com.studiomuda.estoque.domain.cliente.Endereco;
import com.studiomuda.estoque.domain.cliente.TipoPessoa;
import com.studiomuda.estoque.domain.cliente.exceptions.ClienteJaExisteException;
import com.studiomuda.estoque.domain.cliente.exceptions.ClienteNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AtualizarClienteUseCase {
    private final ClienteRepository clienteRepository;
    private final FuncionarioCpfCheckPort funcionarioCpfCheck;

    public AtualizarClienteUseCase(ClienteRepository clienteRepository,
                                   FuncionarioCpfCheckPort funcionarioCpfCheck) {
        this.clienteRepository = clienteRepository;
        this.funcionarioCpfCheck = funcionarioCpfCheck;
    }

    public void executar(AtualizarClienteCommand cmd) {
        Cliente existente = clienteRepository.buscarPorId(cmd.id())
                .orElseThrow(() -> new ClienteNaoEncontradoException(cmd.id()));

        TipoPessoa tipo = TipoPessoa.fromCodigo(cmd.tipo());
        CpfCnpj cpfCnpj = CpfCnpj.of(cmd.cpfCnpj(), tipo);

        Optional<Cliente> outro = clienteRepository.buscarPorCpfCnpj(cpfCnpj);
        boolean conflitoCliente = outro.isPresent() && outro.get().id() != cmd.id();
        if (conflitoCliente || funcionarioCpfCheck.existeFuncionarioComCpf(cpfCnpj)) {
            throw new ClienteJaExisteException(
                    "Já existe um cliente ou funcionário com esse CPF/CNPJ cadastrado.");
        }

        Endereco endereco = new Endereco(cmd.cep(), cmd.rua(), cmd.numero(),
                cmd.bairro(), cmd.cidade(), cmd.estado());
        existente.atualizarDados(cmd.nome(), cmd.telefone(), cmd.email(), endereco, cmd.dataNascimento());
        if (cmd.ativo()) existente.ativar(); else existente.desativar();

        clienteRepository.atualizar(existente);
    }
}
