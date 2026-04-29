package com.studiomuda.estoque.infrastructure.persistence.funcionario;

import com.studiomuda.estoque.application.funcionario.ports.ClienteCpfCheckPort;
import com.studiomuda.estoque.domain.cliente.ClienteRepository;
import com.studiomuda.estoque.domain.cliente.CpfCnpj;
import com.studiomuda.estoque.domain.cliente.TipoPessoa;
import org.springframework.stereotype.Component;

@Component
public class ClienteCpfCheckAdapter implements ClienteCpfCheckPort {
    private final ClienteRepository clienteRepository;

    public ClienteCpfCheckAdapter(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public boolean existeClienteComCpfCnpj(String digitos) {
        if (digitos == null) return false;
        TipoPessoa tipo = digitos.length() == 14 ? TipoPessoa.PJ : TipoPessoa.PF;
        try {
            CpfCnpj cpfCnpj = CpfCnpj.of(digitos, tipo);
            return clienteRepository.buscarPorCpfCnpj(cpfCnpj).isPresent();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
