package com.studiomuda.estoque.application.pedido;

import com.studiomuda.estoque.domain.pedido.Pedido;
import com.studiomuda.estoque.domain.pedido.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BuscarPedidoUseCase {
    private final PedidoRepository pedidoRepository;

    public BuscarPedidoUseCase(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public Optional<Pedido> porId(int id) {
        return pedidoRepository.buscarPorId(id);
    }
}
