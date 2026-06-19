package com.studiomuda.estoque.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "calendario_excecao")
public class CalendarioExcecaoJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "data")
    private Date data;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "ativa")
    private Boolean ativa;

    public Integer getId() {
        return id;
    }

    public Date getData() {
        return data;
    }

    public String getMotivo() {
        return motivo;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public boolean isAtiva() {
        return Boolean.TRUE.equals(ativa);
    }

    public void setData(Date data) {
        this.data = data;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }
}
