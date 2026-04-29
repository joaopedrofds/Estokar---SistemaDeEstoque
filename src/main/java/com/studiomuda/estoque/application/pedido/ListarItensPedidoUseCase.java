package com.studiomuda.estoque.application.pedido;

import com.studiomuda.estoque.domain.pedido.ItemPedidoComProduto;
import com.studiomuda.estoque.domain.pedido.ItemPedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarItensPedidoUseCase {
    private final ItemPedidoRepository itemPedidoRepository;

    public ListarItensPedidoUseCase(ItemPedidoRepository itemPedidoRepository) {
        this.itemPedidoRepository = itemPedidoRepository;
    }

    public List<ItemPedidoComProduto> porPedido(int pedidoId) {
        return itemPedidoRepository.listarPorPedido(pedidoId);
    }
}
