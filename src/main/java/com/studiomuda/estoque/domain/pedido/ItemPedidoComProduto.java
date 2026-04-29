package com.studiomuda.estoque.domain.pedido;

public class ItemPedidoComProduto {
    private final ItemPedido item;
    private final String produtoNome;
    private final double produtoValor;

    public ItemPedidoComProduto(ItemPedido item, String produtoNome, double produtoValor) {
        this.item = item;
        this.produtoNome = produtoNome;
        this.produtoValor = produtoValor;
    }

    public ItemPedido item() { return item; }
    public String produtoNome() { return produtoNome; }
    public double produtoValor() { return produtoValor; }
    public double subtotal() { return item.subtotal(produtoValor); }
}
