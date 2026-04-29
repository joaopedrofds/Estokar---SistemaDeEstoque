package com.studiomuda.estoque.presentation.web.pedido;

import com.studiomuda.estoque.application.pedido.dto.AdicionarItemCommand;

public class ItemPedidoForm {
    private int id;
    private int pedidoId;
    private int produtoId;
    private int quantidade;

    public AdicionarItemCommand toCommand() {
        return new AdicionarItemCommand(pedidoId, produtoId, quantidade);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPedidoId() { return pedidoId; }
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }
    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}
