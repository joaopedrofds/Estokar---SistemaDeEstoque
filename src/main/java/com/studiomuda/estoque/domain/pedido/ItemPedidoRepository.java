package com.studiomuda.estoque.domain.pedido;

import java.util.List;
import java.util.Optional;

public interface ItemPedidoRepository {

    ItemPedido salvar(ItemPedido item);

    Optional<ItemPedido> buscarPorId(int id);

    List<ItemPedidoComProduto> listarPorPedido(int pedidoId);

    void remover(int id);

    void removerPorPedido(int pedidoId);
}
