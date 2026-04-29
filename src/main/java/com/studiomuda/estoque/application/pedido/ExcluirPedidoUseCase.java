package com.studiomuda.estoque.application.pedido;

import com.studiomuda.estoque.domain.pedido.ItemPedidoRepository;
import com.studiomuda.estoque.domain.pedido.PedidoRepository;
import org.springframework.stereotype.Service;

@Service
public class ExcluirPedidoUseCase {
    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public ExcluirPedidoUseCase(PedidoRepository pedidoRepository,
                                 ItemPedidoRepository itemPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    public void executar(int id) {
        itemPedidoRepository.removerPorPedido(id);
        pedidoRepository.remover(id);
    }
}
