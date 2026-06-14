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
@Table(name = "inventario_escopo_produto")
public class InventarioEscopoProdutoJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sessao_id")
    private Integer sessaoId;

    @Column(name = "produto_id")
    private Integer produtoId;

    @Column(name = "quantidade_sistema_abertura")
    private Integer quantidadeSistemaAbertura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", insertable = false, updatable = false)
    private ProdutoJpaEntity produto;

    public Integer getId() { return id; }
    public Integer getSessaoId() { return sessaoId; }
    public Integer getProdutoId() { return produtoId; }
    public Integer getQuantidadeSistemaAbertura() { return quantidadeSistemaAbertura; }
    public ProdutoJpaEntity getProduto() { return produto; }

    public void setSessaoId(Integer sessaoId) { this.sessaoId = sessaoId; }
    public void setProdutoId(Integer produtoId) { this.produtoId = produtoId; }
    public void setQuantidadeSistemaAbertura(Integer quantidadeSistemaAbertura) {
        this.quantidadeSistemaAbertura = quantidadeSistemaAbertura;
    }
}
