package com.studiomuda.estoque.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PoliticaCredito {
    private int id;
    private String nome;
    private int diasLimiteAtraso;
    private boolean ativa;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private LocalDateTime criadoEm;

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

    public int getDiasLimiteAtraso() {
        return diasLimiteAtraso;
    }

    public void setDiasLimiteAtraso(int diasLimiteAtraso) {
        this.diasLimiteAtraso = diasLimiteAtraso;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
