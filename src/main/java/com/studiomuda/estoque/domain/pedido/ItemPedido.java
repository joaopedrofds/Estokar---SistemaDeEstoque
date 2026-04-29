package com.studiomuda.estoque.domain.pedido;

public class ItemPedido {
    private final int id;
    private final int pedidoId;
    private final int produtoId;
    private final int quantidade;

    public ItemPedido(int id, int pedidoId, int produtoId, int quantidade) {
        if (produtoId <= 0) throw new IllegalArgumentException("Produto é obrigatório.");
        if (quantidade <= 0) throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        this.id = id;
        this.pedidoId = pedidoId;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
    }

    public static ItemPedido novo(int pedidoId, int produtoId, int quantidade) {
        return new ItemPedido(0, pedidoId, produtoId, quantidade);
    }

    public double subtotal(double valorUnitario) {
        return quantidade * valorUnitario;
    }

    public int id() { return id; }
    public int pedidoId() { return pedidoId; }
    public int produtoId() { return produtoId; }
    public int quantidade() { return quantidade; }
}
