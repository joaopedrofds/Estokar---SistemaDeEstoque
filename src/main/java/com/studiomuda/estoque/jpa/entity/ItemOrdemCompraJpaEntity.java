package com.studiomuda.estoque.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "item_ordem_compra")
public class ItemOrdemCompraJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_compra_id")
    private OrdemCompraJpaEntity ordemCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id")
    private ProdutoJpaEntity produto;

    @Column(name = "quantidade")
    private Integer quantidade;

    @Column(name = "valor_unitario")
    private BigDecimal valorUnitario;

    public Integer getId() {
        return id;
    }

    public OrdemCompraJpaEntity getOrdemCompra() {
        return ordemCompra;
    }

    public ProdutoJpaEntity getProduto() {
        return produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setOrdemCompra(OrdemCompraJpaEntity ordemCompra) {
        this.ordemCompra = ordemCompra;
    }

    public void setProduto(ProdutoJpaEntity produto) {
        this.produto = produto;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }
}
