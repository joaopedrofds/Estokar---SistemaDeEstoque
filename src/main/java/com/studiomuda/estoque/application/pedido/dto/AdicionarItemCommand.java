package com.studiomuda.estoque.application.pedido.dto;

public class AdicionarItemCommand {
    private final int pedidoId;
    private final int produtoId;
    private final int quantidade;

    public AdicionarItemCommand(int pedidoId, int produtoId, int quantidade) {
        this.pedidoId = pedidoId;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
    }

    public int pedidoId() { return pedidoId; }
    public int produtoId() { return produtoId; }
    public int quantidade() { return quantidade; }
}
