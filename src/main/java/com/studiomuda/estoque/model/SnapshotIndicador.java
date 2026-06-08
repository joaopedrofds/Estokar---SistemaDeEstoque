package com.studiomuda.estoque.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SnapshotIndicador {
    private int id;
    private int indicadorId;
    private double valorCalculado;
    private LocalDate periodoInicio;
    private LocalDate periodoFim;
    private Integer executadoPorId;
    private String executadoPor;
    private LocalDateTime dataExecucao;
    private String detalheRastreio;

    // Nome do indicador para exibição
    private String indicadorNome;
    private String indicadorCodigo;

    public SnapshotIndicador() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIndicadorId() { return indicadorId; }
    public void setIndicadorId(int indicadorId) { this.indicadorId = indicadorId; }

    public double getValorCalculado() { return valorCalculado; }
    public void setValorCalculado(double valorCalculado) { this.valorCalculado = valorCalculado; }

    public LocalDate getPeriodoInicio() { return periodoInicio; }
    public void setPeriodoInicio(LocalDate periodoInicio) { this.periodoInicio = periodoInicio; }

    public LocalDate getPeriodoFim() { return periodoFim; }
    public void setPeriodoFim(LocalDate periodoFim) { this.periodoFim = periodoFim; }

    public Integer getExecutadoPorId() { return executadoPorId; }
    public void setExecutadoPorId(Integer executadoPorId) { this.executadoPorId = executadoPorId; }

    public String getExecutadoPor() { return executadoPor; }
    public void setExecutadoPor(String executadoPor) { this.executadoPor = executadoPor; }

    public LocalDateTime getDataExecucao() { return dataExecucao; }
    public void setDataExecucao(LocalDateTime dataExecucao) { this.dataExecucao = dataExecucao; }

    public String getDetalheRastreio() { return detalheRastreio; }
    public void setDetalheRastreio(String detalheRastreio) { this.detalheRastreio = detalheRastreio; }

    public String getIndicadorNome() { return indicadorNome; }
    public void setIndicadorNome(String indicadorNome) { this.indicadorNome = indicadorNome; }

    public String getIndicadorCodigo() { return indicadorCodigo; }
    public void setIndicadorCodigo(String indicadorCodigo) { this.indicadorCodigo = indicadorCodigo; }
}
