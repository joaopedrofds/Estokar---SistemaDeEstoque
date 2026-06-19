package com.studiomuda.estoque.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "parametro_inventario")
public class ParametroInventarioJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tolerancia_quantidade")
    private Integer toleranciaQuantidade;

    public Integer getId() { return id; }
    public Integer getToleranciaQuantidade() { return toleranciaQuantidade; }
}
