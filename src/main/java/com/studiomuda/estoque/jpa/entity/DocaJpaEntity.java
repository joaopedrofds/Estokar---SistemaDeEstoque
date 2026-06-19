package com.studiomuda.estoque.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "doca")
public class DocaJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "capacidade_paletes_diaria")
    private Integer capacidadePaletesDiaria;

    @Column(name = "ativa")
    private Boolean ativa;

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Integer getCapacidadePaletesDiaria() {
        return capacidadePaletesDiaria;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public boolean isAtiva() {
        return Boolean.TRUE.equals(ativa);
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCapacidadePaletesDiaria(Integer capacidadePaletesDiaria) {
        this.capacidadePaletesDiaria = capacidadePaletesDiaria;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }
}
