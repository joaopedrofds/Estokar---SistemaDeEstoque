package com.studiomuda.estoque.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fornecedor")
public class FornecedorJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "lead_time_dias")
    private Integer leadTimeDias;

    @Column(name = "ativo")
    private Boolean ativo;

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Integer getLeadTimeDias() {
        return leadTimeDias;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public boolean isAtivo() {
        return Boolean.TRUE.equals(ativo);
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setLeadTimeDias(Integer leadTimeDias) {
        this.leadTimeDias = leadTimeDias;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
