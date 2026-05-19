package com.studiomuda.estoque.model;

public class RelatorioIndicadorLinha {
    private int id;
    private int relatorioId;
    private String indicador;
    private double valor;
    private Double valorAnterior;
    private Double variacaoPercentual;
    private String formulaDescricao;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRelatorioId() {
        return relatorioId;
    }

    public void setRelatorioId(int relatorioId) {
        this.relatorioId = relatorioId;
    }

    public String getIndicador() {
        return indicador;
    }

    public void setIndicador(String indicador) {
        this.indicador = indicador;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Double getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(Double valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public Double getVariacaoPercentual() {
        return variacaoPercentual;
    }

    public void setVariacaoPercentual(Double variacaoPercentual) {
        this.variacaoPercentual = variacaoPercentual;
    }

    public String getFormulaDescricao() {
        return formulaDescricao;
    }

    public void setFormulaDescricao(String formulaDescricao) {
        this.formulaDescricao = formulaDescricao;
    }
}
