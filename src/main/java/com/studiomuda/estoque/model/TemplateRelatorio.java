package com.studiomuda.estoque.model;

import java.util.ArrayList;
import java.util.List;

public class TemplateRelatorio {
    private int id;
    private String nome;
    private String descricao;
    private String periodoPadrao;
    private String agrupamento;
    private boolean ativo;
    private List<Integer> categoriaIds = new ArrayList<>();
    private List<String> indicadores = new ArrayList<>();

    public TemplateRelatorio() {
        this.ativo = true;
        this.periodoPadrao = "MES";
        this.agrupamento = "MES";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPeriodoPadrao() {
        return periodoPadrao;
    }

    public void setPeriodoPadrao(String periodoPadrao) {
        this.periodoPadrao = periodoPadrao;
    }

    public String getAgrupamento() {
        return agrupamento;
    }

    public void setAgrupamento(String agrupamento) {
        this.agrupamento = agrupamento;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public List<Integer> getCategoriaIds() {
        return categoriaIds;
    }

    public void setCategoriaIds(List<Integer> categoriaIds) {
        this.categoriaIds = categoriaIds;
    }

    public List<String> getIndicadores() {
        return indicadores;
    }

    public void setIndicadores(List<String> indicadores) {
        this.indicadores = indicadores;
    }
}
