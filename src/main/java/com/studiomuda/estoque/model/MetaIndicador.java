package com.studiomuda.estoque.model;

import java.time.LocalDate;

public class MetaIndicador {
    private int id;
    private int indicadorId;
    private double valorAlvo;
    private double limiteCritico;
    private String operador; // MAIOR_IGUAL, MENOR_IGUAL
    private LocalDate vigenciaInicio;
    private LocalDate vigenciaFim;
    private boolean ativo;
    
    // Nome do indicador para facilitar exibições na interface
    private String indicadorNome;
    private String indicadorCodigo;

    public MetaIndicador() {}

    public MetaIndicador(int id, int indicadorId, double valorAlvo, double limiteCritico, String operador, LocalDate vigenciaInicio, LocalDate vigenciaFim, boolean ativo) {
        this.id = id;
        this.indicadorId = indicadorId;
        this.valorAlvo = valorAlvo;
        this.limiteCritico = limiteCritico;
        this.operador = operador;
        this.vigenciaInicio = vigenciaInicio;
        this.vigenciaFim = vigenciaFim;
        this.ativo = ativo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIndicadorId() { return indicadorId; }
    public void setIndicadorId(int indicadorId) { this.indicadorId = indicadorId; }

    public double getValorAlvo() { return valorAlvo; }
    public void setValorAlvo(double valorAlvo) { this.valorAlvo = valorAlvo; }

    public double getLimiteCritico() { return limiteCritico; }
    public void setLimiteCritico(double limiteCritico) { this.limiteCritico = limiteCritico; }

    public String getOperador() { return operador; }
    public void setOperador(String operador) { this.operador = operador; }

    public LocalDate getVigenciaInicio() { return vigenciaInicio; }
    public void setVigenciaInicio(LocalDate vigenciaInicio) { this.vigenciaInicio = vigenciaInicio; }

    public LocalDate getVigenciaFim() { return vigenciaFim; }
    public void setVigenciaFim(LocalDate vigenciaFim) { this.vigenciaFim = vigenciaFim; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public String getIndicadorNome() { return indicadorNome; }
    public void setIndicadorNome(String indicadorNome) { this.indicadorNome = indicadorNome; }

    public String getIndicadorCodigo() { return indicadorCodigo; }
    public void setIndicadorCodigo(String indicadorCodigo) { this.indicadorCodigo = indicadorCodigo; }

    public boolean isViolada(double valorCalculado) {
        if ("MAIOR_IGUAL".equals(operador)) {
            return valorCalculado < valorAlvo;
        } else if ("MENOR_IGUAL".equals(operador)) {
            return valorCalculado > valorAlvo;
        }
        return false;
    }

    public boolean isCritico(double valorCalculado) {
        if ("MAIOR_IGUAL".equals(operador)) {
            return valorCalculado < limiteCritico;
        } else if ("MENOR_IGUAL".equals(operador)) {
            return valorCalculado > limiteCritico;
        }
        return false;
    }
}
