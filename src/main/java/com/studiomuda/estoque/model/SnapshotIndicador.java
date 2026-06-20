package com.studiomuda.estoque.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "snapshot_indicador")
public class SnapshotIndicador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "indicador_id")
    private int indicadorId;

    @Column(name = "valor_calculado")
    private double valorCalculado;

    @Column(name = "periodo_inicio")
    private LocalDate periodoInicio;

    @Column(name = "periodo_fim")
    private LocalDate periodoFim;

    @Column(name = "executado_por_id")
    private Integer executadoPorId;

    @Column(name = "executado_por")
    private String executadoPor;

    // Preenchido pelo banco (DEFAULT CURRENT_TIMESTAMP)
    @Column(name = "data_execucao", insertable = false, updatable = false)
    private LocalDateTime dataExecucao;

    @Column(name = "detalhe_rastreio")
    private String detalheRastreio;

    // Nome do indicador para exibição (não persistido)
    @Transient
    private String indicadorNome;
    @Transient
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
