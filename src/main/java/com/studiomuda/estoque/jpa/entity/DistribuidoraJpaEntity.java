package com.studiomuda.estoque.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "distribuidora")
public class DistribuidoraJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "nivel_prioridade")
    private String nivelPrioridade;

    @Column(name = "ativa")
    private Boolean ativa;

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getNivelPrioridade() {
        return nivelPrioridade;
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

    public void setNivelPrioridade(String nivelPrioridade) {
        this.nivelPrioridade = nivelPrioridade;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }
}
