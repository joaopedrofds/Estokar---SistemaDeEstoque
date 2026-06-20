package com.studiomuda.estoque.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "alerta_indicador")
public class AlertaIndicador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "indicador_id")
    private int indicadorId;

    @Column(name = "snapshot_id")
    private int snapshotId;

    @Column(name = "tipo_violacao")
    private String tipoViolacao; // ABAIXO_META, ACIMA_CRITICO

    @Column(name = "valor_esperado")
    private double valorEsperado;

    @Column(name = "valor_encontrado")
    private double valorEncontrado;

    @Column(name = "mensagem")
    private String mensagem;

    @Column(name = "status")
    private String status; // ATIVO, RESOLVIDO

    @Column(name = "resolvido_por")
    private String resolvidoPor;

    @Column(name = "observacao")
    private String observacao;

    // Preenchido pelo banco (DEFAULT CURRENT_TIMESTAMP)
    @Column(name = "data_alerta", insertable = false, updatable = false)
    private LocalDateTime dataAlerta;

    @Column(name = "data_resolucao")
    private LocalDateTime dataResolucao;

    // Campos auxiliares para UI (não persistido)
    @Transient
    private String indicadorNome;
    @Transient
    private String indicadorCodigo;

    public AlertaIndicador() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIndicadorId() { return indicadorId; }
    public void setIndicadorId(int indicadorId) { this.indicadorId = indicadorId; }

    public int getSnapshotId() { return snapshotId; }
    public void setSnapshotId(int snapshotId) { this.snapshotId = snapshotId; }

    public String getTipoViolacao() { return tipoViolacao; }
    public void setTipoViolacao(String tipoViolacao) { this.tipoViolacao = tipoViolacao; }

    public double getValorEsperado() { return valorEsperado; }
    public void setValorEsperado(double valorEsperado) { this.valorEsperado = valorEsperado; }

    public double getValorEncontrado() { return valorEncontrado; }
    public void setValorEncontrado(double valorEncontrado) { this.valorEncontrado = valorEncontrado; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResolvidoPor() { return resolvidoPor; }
    public void setResolvidoPor(String resolvidoPor) { this.resolvidoPor = resolvidoPor; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public LocalDateTime getDataAlerta() { return dataAlerta; }
    public void setDataAlerta(LocalDateTime dataAlerta) { this.dataAlerta = dataAlerta; }

    public LocalDateTime getDataResolucao() { return dataResolucao; }
    public void setDataResolucao(LocalDateTime dataResolucao) { this.dataResolucao = dataResolucao; }

    public String getIndicadorNome() { return indicadorNome; }
    public void setIndicadorNome(String indicadorNome) { this.indicadorNome = indicadorNome; }

    public String getIndicadorCodigo() { return indicadorCodigo; }
    public void setIndicadorCodigo(String indicadorCodigo) { this.indicadorCodigo = indicadorCodigo; }
}
