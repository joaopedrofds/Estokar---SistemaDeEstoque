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

@Entity
@Table(name = "parametro_estoque")
public class ParametroEstoqueJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id")
    private ProdutoJpaEntity produto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id")
    private FornecedorJpaEntity fornecedor;

    @Column(name = "margem_seguranca")
    private Integer margemSeguranca;

    public Integer getId() {
        return id;
    }

    public ProdutoJpaEntity getProduto() {
        return produto;
    }

    public FornecedorJpaEntity getFornecedor() {
        return fornecedor;
    }

    public Integer getMargemSeguranca() {
        return margemSeguranca;
    }

    public void setProduto(ProdutoJpaEntity produto) {
        this.produto = produto;
    }

    public void setFornecedor(FornecedorJpaEntity fornecedor) {
        this.fornecedor = fornecedor;
    }

    public void setMargemSeguranca(Integer margemSeguranca) {
        this.margemSeguranca = margemSeguranca;
    }
}
