package com.studiomuda.estoque.model;

import javax.persistence.*;

/**
 * Entidade de item devolvido.
 * Nível Tático DDD: Entity
 */
@Entity
@Table(name = "item_devolucao")
public class ItemDevolucao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "devolucao_id", nullable = false)
    private int devolucaoId;

    @Column(name = "produto_id", nullable = false)
    private int produtoId;

    @Transient
    private String produtoNome;

    @Column(name = "quantidade", nullable = false)
    private int quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 10, scale = 2)
    private double valorUnitario;

    @Column(name = "condicao", length = 20, nullable = false)
    private String condicao;

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }
    public int getDevolucaoId()                 { return devolucaoId; }
    public void setDevolucaoId(int d)           { this.devolucaoId = d; }
    public int getProdutoId()                   { return produtoId; }
    public void setProdutoId(int p)             { this.produtoId = p; }
    public String getProdutoNome()              { return produtoNome; }
    public void setProdutoNome(String n)        { this.produtoNome = n; }
    public int getQuantidade()                  { return quantidade; }
    public void setQuantidade(int q)            { this.quantidade = q; }
    public double getValorUnitario()            { return valorUnitario; }
    public void setValorUnitario(double v)      { this.valorUnitario = v; }
    public String getCondicao()                 { return condicao; }
    public void setCondicao(String c)           { this.condicao = c; }
    public double getSubtotal()                 { return quantidade * valorUnitario; }
}