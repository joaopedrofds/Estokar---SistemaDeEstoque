package com.studiomuda.estoque.model;

public class RelatorioCategoriaLinha {
    private int id;
    private int relatorioId;
    private int categoriaId;
    private String categoriaNome;
    private String tipoCategoria;
    private double valorPeriodo;
    private double valorPeriodoAnterior;
    private Double variacaoPercentual;
    private String origemRastreio;
    private boolean ajusteManual;

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

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getCategoriaNome() {
        return categoriaNome;
    }

    public void setCategoriaNome(String categoriaNome) {
        this.categoriaNome = categoriaNome;
    }

    public String getTipoCategoria() {
        return tipoCategoria;
    }

    public void setTipoCategoria(String tipoCategoria) {
        this.tipoCategoria = tipoCategoria;
    }

    public double getValorPeriodo() {
        return valorPeriodo;
    }

    public void setValorPeriodo(double valorPeriodo) {
        this.valorPeriodo = valorPeriodo;
    }

    public double getValorPeriodoAnterior() {
        return valorPeriodoAnterior;
    }

    public void setValorPeriodoAnterior(double valorPeriodoAnterior) {
        this.valorPeriodoAnterior = valorPeriodoAnterior;
    }

    public Double getVariacaoPercentual() {
        return variacaoPercentual;
    }

    public void setVariacaoPercentual(Double variacaoPercentual) {
        this.variacaoPercentual = variacaoPercentual;
    }

    public String getOrigemRastreio() {
        return origemRastreio;
    }

    public void setOrigemRastreio(String origemRastreio) {
        this.origemRastreio = origemRastreio;
    }

    public boolean isAjusteManual() {
        return ajusteManual;
    }

    public void setAjusteManual(boolean ajusteManual) {
        this.ajusteManual = ajusteManual;
    }
}
