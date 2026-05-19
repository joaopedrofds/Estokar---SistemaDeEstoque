package com.studiomuda.estoque.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RelatorioGerado {
    private int id;
    private int templateId;
    private String templateNome;
    private Date dataInicio;
    private Date dataFim;
    private Date dataInicioAnterior;
    private Date dataFimAnterior;
    private Integer geradoPorUsuarioId;
    private String geradoPorUsername;
    private Timestamp dataGeracao;
    private double receitaOperacional;
    private double custoOperacional;
    private double resultadoOperacional;
    private double totalAjustesReceita;
    private double totalAjustesDespesa;
    private double resultadoConsolidado;
    private int quantidadePedidos;
    private List<RelatorioCategoriaLinha> linhasCategoria = new ArrayList<>();
    private List<RelatorioIndicadorLinha> linhasIndicador = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public String getTemplateNome() {
        return templateNome;
    }

    public void setTemplateNome(String templateNome) {
        this.templateNome = templateNome;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public Date getDataInicioAnterior() {
        return dataInicioAnterior;
    }

    public void setDataInicioAnterior(Date dataInicioAnterior) {
        this.dataInicioAnterior = dataInicioAnterior;
    }

    public Date getDataFimAnterior() {
        return dataFimAnterior;
    }

    public void setDataFimAnterior(Date dataFimAnterior) {
        this.dataFimAnterior = dataFimAnterior;
    }

    public Integer getGeradoPorUsuarioId() {
        return geradoPorUsuarioId;
    }

    public void setGeradoPorUsuarioId(Integer geradoPorUsuarioId) {
        this.geradoPorUsuarioId = geradoPorUsuarioId;
    }

    public String getGeradoPorUsername() {
        return geradoPorUsername;
    }

    public void setGeradoPorUsername(String geradoPorUsername) {
        this.geradoPorUsername = geradoPorUsername;
    }

    public Timestamp getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(Timestamp dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public double getReceitaOperacional() {
        return receitaOperacional;
    }

    public void setReceitaOperacional(double receitaOperacional) {
        this.receitaOperacional = receitaOperacional;
    }

    public double getCustoOperacional() {
        return custoOperacional;
    }

    public void setCustoOperacional(double custoOperacional) {
        this.custoOperacional = custoOperacional;
    }

    public double getResultadoOperacional() {
        return resultadoOperacional;
    }

    public void setResultadoOperacional(double resultadoOperacional) {
        this.resultadoOperacional = resultadoOperacional;
    }

    public double getTotalAjustesReceita() {
        return totalAjustesReceita;
    }

    public void setTotalAjustesReceita(double totalAjustesReceita) {
        this.totalAjustesReceita = totalAjustesReceita;
    }

    public double getTotalAjustesDespesa() {
        return totalAjustesDespesa;
    }

    public void setTotalAjustesDespesa(double totalAjustesDespesa) {
        this.totalAjustesDespesa = totalAjustesDespesa;
    }

    public double getResultadoConsolidado() {
        return resultadoConsolidado;
    }

    public void setResultadoConsolidado(double resultadoConsolidado) {
        this.resultadoConsolidado = resultadoConsolidado;
    }

    public int getQuantidadePedidos() {
        return quantidadePedidos;
    }

    public void setQuantidadePedidos(int quantidadePedidos) {
        this.quantidadePedidos = quantidadePedidos;
    }

    public List<RelatorioCategoriaLinha> getLinhasCategoria() {
        return linhasCategoria;
    }

    public void setLinhasCategoria(List<RelatorioCategoriaLinha> linhasCategoria) {
        this.linhasCategoria = linhasCategoria;
    }

    public List<RelatorioIndicadorLinha> getLinhasIndicador() {
        return linhasIndicador;
    }

    public void setLinhasIndicador(List<RelatorioIndicadorLinha> linhasIndicador) {
        this.linhasIndicador = linhasIndicador;
    }
}
