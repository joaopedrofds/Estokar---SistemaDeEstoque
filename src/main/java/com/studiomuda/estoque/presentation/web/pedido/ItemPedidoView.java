package com.studiomuda.estoque.presentation.web.pedido;

import com.studiomuda.estoque.domain.pedido.ItemPedidoComProduto;

public class ItemPedidoView {
    private final int id;
    private final int pedidoId;
    private final int produtoId;
    private final int quantidade;
    private final String produtoNome;
    private final double produtoValor;
    private final double subtotal;

    public ItemPedidoView(ItemPedidoComProduto item) {
        this.id = item.item().id();
        this.pedidoId = item.item().pedidoId();
        this.produtoId = item.item().produtoId();
        this.quantidade = item.item().quantidade();
        this.produtoNome = item.produtoNome();
        this.produtoValor = item.produtoValor();
        this.subtotal = item.subtotal();
    }

    public int getId() { return id; }
    public int getPedidoId() { return pedidoId; }
    public int getProdutoId() { return produtoId; }
    public int getQuantidade() { return quantidade; }
    public String getProdutoNome() { return produtoNome; }
    public double getProdutoValor() { return produtoValor; }
    public double getSubtotal() { return subtotal; }
}
