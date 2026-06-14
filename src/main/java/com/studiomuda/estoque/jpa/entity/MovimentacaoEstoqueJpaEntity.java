package com.studiomuda.estoque.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "movimentacao_estoque")
public class MovimentacaoEstoqueJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_produto")
    private Integer produtoId;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "quantidade")
    private Integer quantidade;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "data")
    private Date data;

    public MovimentacaoEstoqueJpaEntity() {
    }

    public MovimentacaoEstoqueJpaEntity(Integer produtoId, String tipo, Integer quantidade, String motivo, Date data) {
        this.produtoId = produtoId;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.motivo = motivo;
        this.data = data;
    }
}
