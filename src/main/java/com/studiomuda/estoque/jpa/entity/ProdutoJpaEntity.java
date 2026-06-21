package com.studiomuda.estoque.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "produto")
public class ProdutoJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "quantidade")
    private Integer quantidade;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "custo")
    private BigDecimal custo;

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public Integer getQuantidade() { return quantidade; }
    public BigDecimal getValor() { return valor; }
    public BigDecimal getCusto() { return custo; }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void setCusto(BigDecimal custo) {
        this.custo = custo;
    }

    public void adicionarQuantidade(int quantidade) {
        int atual = this.quantidade != null ? this.quantidade : 0;
        this.quantidade = atual + quantidade;
    }
}
