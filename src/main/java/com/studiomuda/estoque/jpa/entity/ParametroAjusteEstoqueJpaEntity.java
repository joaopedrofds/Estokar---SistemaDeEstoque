package com.studiomuda.estoque.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "parametro_ajuste_estoque")
public class ParametroAjusteEstoqueJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "limite_quantidade_sem_aprovacao")
    private Integer limiteQuantidadeSemAprovacao;

    @Column(name = "percentual_risco_alto")
    private Integer percentualRiscoAlto;

    public Integer getId() { return id; }
    public Integer getLimiteQuantidadeSemAprovacao() { return limiteQuantidadeSemAprovacao; }
    public Integer getPercentualRiscoAlto() { return percentualRiscoAlto; }
}
