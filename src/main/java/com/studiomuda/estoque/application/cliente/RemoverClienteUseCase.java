package com.studiomuda.estoque.application.cliente;

import com.studiomuda.estoque.domain.cliente.ClienteRepository;
import org.springframework.stereotype.Service;

@Service
public class RemoverClienteUseCase {
    private final ClienteRepository clienteRepository;

    public RemoverClienteUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public void executar(int id) {
        clienteRepository.desativar(id);
    }
}
