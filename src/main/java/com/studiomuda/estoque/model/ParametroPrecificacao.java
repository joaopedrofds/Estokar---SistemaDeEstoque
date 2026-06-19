package com.studiomuda.estoque.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade JPA — parâmetros globais do motor de precificação.
 * Nível Tático DDD: Value Object persistido
 * Persistência: ORM via Spring Data JPA
 */
@Entity
@Table(name = "parametro_precificacao")
public class ParametroPrecificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "margem_minima_global")
    private double margemMinimaGlobal = 30.00;

    @Column(name = "desconto_maximo_global")
    private double descontoMaximoGlobal = 25.00;

    @Column(name = "estoque_excesso_threshold")
    private int estoqueExcessoThreshold = 70;

    @Column(name = "markup_padrao")
    private double markupPadrao = 85.00;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }
    public double getMargemMinimaGlobal()           { return margemMinimaGlobal; }
    public void setMargemMinimaGlobal(double v)     { this.margemMinimaGlobal = v; }
    public double getDescontoMaximoGlobal()         { return descontoMaximoGlobal; }
    public void setDescontoMaximoGlobal(double v)   { this.descontoMaximoGlobal = v; }
    public int getEstoqueExcessoThreshold()         { return estoqueExcessoThreshold; }
    public void setEstoqueExcessoThreshold(int v)   { this.estoqueExcessoThreshold = v; }
    public double getMarkupPadrao()                 { return markupPadrao; }
    public void setMarkupPadrao(double v)           { this.markupPadrao = v; }
    public LocalDateTime getAtualizadoEm()          { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime d)    { this.atualizadoEm = d; }
}