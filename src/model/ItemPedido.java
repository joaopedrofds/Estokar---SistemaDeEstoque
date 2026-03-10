package model;

public class ItemPedido {
    private int id;
    private int pedidoId;
    private int produtoId;
    private int quantidade;

    public ItemPedido() {}

    public ItemPedido(int id, int pedidoId, int produtoId, int quantidade) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
    }

    // Getters e Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(int pedidoId) {
        this.pedidoId = pedidoId;
    }

    public int getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(int produtoId) {
        this.produtoId = produtoId;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return "ItemPedido{" +
                "ID: " + id +
                ", Pedido ID: " + pedidoId +
                ", Produto ID: " + produtoId +
                ", Quantidade: " + quantidade +
                '}';
    }
}
