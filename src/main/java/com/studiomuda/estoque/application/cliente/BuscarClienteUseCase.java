package com.studiomuda.estoque.application.cliente;

import com.studiomuda.estoque.domain.cliente.Cliente;
import com.studiomuda.estoque.domain.cliente.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BuscarClienteUseCase {
    private final ClienteRepository clienteRepository;

    public BuscarClienteUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Optional<Cliente> porId(int id) {
        return clienteRepository.buscarPorId(id);
    }
}
