package com.studiomuda.estoque.model;

import javax.persistence.*;

@Entity
@Table(name = "item_pedido")
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "id_pedido")
    private int pedidoId;

    @Column(name = "id_produto")
    private int produtoId;

    @Column(name = "quantidade")
    private int quantidade;

    @Transient
    private String produtoNome;

    @Transient
    private double produtoValor;

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

    public String getProdutoNome() {
        return produtoNome;
    }

    public void setProdutoNome(String produtoNome) {
        this.produtoNome = produtoNome;
    }

    public double getProdutoValor() {
        return produtoValor;
    }

    public void setProdutoValor(double produtoValor) {
        this.produtoValor = produtoValor;
    }

    public double getSubtotal() {
        return quantidade * produtoValor;
    }

    @Override
    public String toString() {
        return "ItemPedido{" +
                "ID: " + id +
                ", Pedido ID: " + pedidoId +
                ", Produto ID: " + produtoId +
                ", Quantidade: " + quantidade +
                "}";
    }
}