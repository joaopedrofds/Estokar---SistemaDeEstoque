package com.studiomuda.estoque.infrastructure.persistence.cliente;

import com.studiomuda.estoque.application.cliente.ports.PedidoQueryPort;
import com.studiomuda.estoque.domain.pedido.PedidoRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class PedidoQueryAdapter implements PedidoQueryPort {
    private final PedidoRepository pedidoRepository;

    public PedidoQueryAdapter(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public List<LocalDate> listarDatasCompraPorCliente(int clienteId) {
        return pedidoRepository.listarDatasCompraPorCliente(clienteId);
    }
}
