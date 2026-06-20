package com.studiomuda.estoque.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "indicador_operacional")
public class IndicadorOperacional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "nome")
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "tipo_calculo")
    private String tipoCalculo;

    @Column(name = "periodo_padrao")
    private String periodoPadrao;

    @Column(name = "ativo")
    private boolean ativo;

    public IndicadorOperacional() {}

    public IndicadorOperacional(int id, String codigo, String nome, String descricao, String tipoCalculo, String periodoPadrao, boolean ativo) {
        this.id = id;
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.tipoCalculo = tipoCalculo;
        this.periodoPadrao = periodoPadrao;
        this.ativo = ativo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getTipoCalculo() { return tipoCalculo; }
    public void setTipoCalculo(String tipoCalculo) { this.tipoCalculo = tipoCalculo; }

    public String getPeriodoPadrao() { return periodoPadrao; }
    public void setPeriodoPadrao(String periodoPadrao) { this.periodoPadrao = periodoPadrao; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
