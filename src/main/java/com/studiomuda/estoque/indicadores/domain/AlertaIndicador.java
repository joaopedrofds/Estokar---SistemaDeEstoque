package com.studiomuda.estoque.indicadores.domain;

import java.time.LocalDateTime;

/**
 * Alerta de não conformidade de um indicador (E-13) — agregado de domínio puro
 * com máquina de estados (padrão PetCollar). Nasce {@code ATIVO} quando um
 * snapshot viola a meta; pode ter a ocorrência reatualizada enquanto ativo e é
 * encerrado por {@link #resolver(String, String)} ({@code ATIVO → RESOLVIDO}).
 *
 * <p>{@code dataAlerta} é atribuída pelo banco ({@code DEFAULT CURRENT_TIMESTAMP}),
 * logo é nula num alerta recém-criado e preenchida apenas na reconstrução.</p>
 */
public class AlertaIndicador {

    private final AlertaId id;
    private final IndicadorId indicadorId;
    private SnapshotIndicadorId snapshotId;
    private TipoViolacao tipoViolacao;
    private final double valorEsperado;
    private double valorEncontrado;
    private String mensagem;
    private StatusAlerta status;
    private String resolvidoPor;
    private String observacao;
    private final LocalDateTime dataAlerta;
    private LocalDateTime dataResolucao;

    /** Cria um novo alerta ATIVO a partir de uma violação recém-detectada. */
    public static AlertaIndicador criar(AlertaId id, IndicadorId indicadorId, SnapshotIndicadorId snapshotId,
                                        TipoViolacao tipoViolacao, double valorEsperado, double valorEncontrado,
                                        String mensagem) {
        return new AlertaIndicador(id, indicadorId, snapshotId, tipoViolacao, valorEsperado, valorEncontrado,
                mensagem, StatusAlerta.ATIVO, null, null, null, null);
    }

    // Construtor de RECONSTRUÇÃO (infra → domínio): todos os campos, sem efeitos.
    public AlertaIndicador(AlertaId id, IndicadorId indicadorId, SnapshotIndicadorId snapshotId,
                           TipoViolacao tipoViolacao, double valorEsperado, double valorEncontrado,
                           String mensagem, StatusAlerta status, String resolvidoPor, String observacao,
                           LocalDateTime dataAlerta, LocalDateTime dataResolucao) {
        if (id == null) {
            throw new IllegalArgumentException("Id do alerta não pode ser nulo.");
        }
        if (indicadorId == null) {
            throw new IllegalArgumentException("Id do indicador do alerta não pode ser nulo.");
        }
        if (snapshotId == null) {
            throw new IllegalArgumentException("Id do snapshot do alerta não pode ser nulo.");
        }
        if (tipoViolacao == null) {
            throw new IllegalArgumentException("Tipo de violação do alerta não pode ser nulo.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status do alerta não pode ser nulo.");
        }
        this.id = id;
        this.indicadorId = indicadorId;
        this.snapshotId = snapshotId;
        this.tipoViolacao = tipoViolacao;
        this.valorEsperado = valorEsperado;
        this.valorEncontrado = valorEncontrado;
        this.mensagem = mensagem;
        this.status = status;
        this.resolvidoPor = resolvidoPor;
        this.observacao = observacao;
        this.dataAlerta = dataAlerta;
        this.dataResolucao = dataResolucao;
    }

    /**
     * Reatualiza um alerta ainda ativo com a violação mais recente (novo snapshot,
     * valor e mensagem), evitando duplicar alertas para a mesma não conformidade.
     */
    public void registrarNovaOcorrencia(SnapshotIndicadorId snapshotId, TipoViolacao tipoViolacao,
                                        double valorEncontrado, String mensagem) {
        if (this.status != StatusAlerta.ATIVO) {
            throw new IllegalStateException("Só é possível reatualizar alertas com status ATIVO.");
        }
        if (snapshotId == null) {
            throw new IllegalArgumentException("Id do snapshot não pode ser nulo.");
        }
        if (tipoViolacao == null) {
            throw new IllegalArgumentException("Tipo de violação não pode ser nulo.");
        }
        this.snapshotId = snapshotId;
        this.tipoViolacao = tipoViolacao;
        this.valorEncontrado = valorEncontrado;
        this.mensagem = mensagem;
    }

    /** Encerra o alerta, registrando quem resolveu e a observação. ATIVO → RESOLVIDO. */
    public void resolver(String resolvidoPor, String observacao) {
        if (this.status != StatusAlerta.ATIVO) {
            throw new IllegalStateException("Só é possível resolver alertas com status ATIVO.");
        }
        this.status = StatusAlerta.RESOLVIDO;
        this.resolvidoPor = resolvidoPor;
        this.observacao = observacao;
        this.dataResolucao = LocalDateTime.now();
    }

    public AlertaId getId() { return id; }
    public IndicadorId getIndicadorId() { return indicadorId; }
    public SnapshotIndicadorId getSnapshotId() { return snapshotId; }
    public TipoViolacao getTipoViolacao() { return tipoViolacao; }
    public double getValorEsperado() { return valorEsperado; }
    public double getValorEncontrado() { return valorEncontrado; }
    public String getMensagem() { return mensagem; }
    public StatusAlerta getStatus() { return status; }
    public String getResolvidoPor() { return resolvidoPor; }
    public String getObservacao() { return observacao; }
    public LocalDateTime getDataAlerta() { return dataAlerta; }
    public LocalDateTime getDataResolucao() { return dataResolucao; }
}
