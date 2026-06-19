package com.studiomuda.estoque.model;

import java.time.LocalDateTime;

public class AlertaIndicador {
    private int id;
    private int indicadorId;
    private int snapshotId;
    private String tipoViolacao; // ABAIXO_META, ACIMA_CRITICO
    private double valorEsperado;
    private double valorEncontrado;
    private String mensagem;
    private String status; // ATIVO, RESOLVIDO
    private String resolvidoPor;
    private String observacao;
    private LocalDateTime dataAlerta;
    private LocalDateTime dataResolucao;

    // Campos auxiliares para UI
    private String indicadorNome;
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
