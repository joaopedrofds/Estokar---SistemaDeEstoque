package com.studiomuda.estoque.indicadores.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Snapshot imutável do valor apurado de um indicador operacional (E-13) —
 * agregado de domínio puro (padrão PetCollar). Identidade por
 * {@link SnapshotIndicadorId}; {@code indicadorId}/{@code executadoPorId} são
 * referências ({@code int}) a agregados ainda legados.
 *
 * <p>{@code dataExecucao} é atribuída pelo banco ({@code DEFAULT CURRENT_TIMESTAMP})
 * e por isso é nula em um snapshot recém-criado, sendo preenchida apenas na
 * reconstrução a partir da persistência.</p>
 */
public class SnapshotIndicador {

    private final SnapshotIndicadorId id;
    private final IndicadorId indicadorId;
    private final double valorCalculado;
    private final LocalDate periodoInicio;
    private final LocalDate periodoFim;
    private final Integer executadoPorId;
    private final String executadoPor;
    private final LocalDateTime dataExecucao;
    private final String detalheRastreio;

    public SnapshotIndicador(SnapshotIndicadorId id, IndicadorId indicadorId, double valorCalculado,
                             LocalDate periodoInicio, LocalDate periodoFim,
                             Integer executadoPorId, String executadoPor, String detalheRastreio) {
        this(id, indicadorId, valorCalculado, periodoInicio, periodoFim,
                executadoPorId, executadoPor, null, detalheRastreio);
    }

    // Construtor de RECONSTRUÇÃO (infra → domínio); inclui a data de execução do banco.
    public SnapshotIndicador(SnapshotIndicadorId id, IndicadorId indicadorId, double valorCalculado,
                             LocalDate periodoInicio, LocalDate periodoFim,
                             Integer executadoPorId, String executadoPor,
                             LocalDateTime dataExecucao, String detalheRastreio) {
        if (id == null) {
            throw new IllegalArgumentException("Id do snapshot não pode ser nulo.");
        }
        if (indicadorId == null) {
            throw new IllegalArgumentException("Id do indicador do snapshot não pode ser nulo.");
        }
        if (periodoInicio == null || periodoFim == null) {
            throw new IllegalArgumentException("Período do snapshot não pode ser nulo.");
        }
        if (periodoFim.isBefore(periodoInicio)) {
            throw new IllegalArgumentException("Fim do período não pode ser anterior ao início.");
        }
        this.id = id;
        this.indicadorId = indicadorId;
        this.valorCalculado = valorCalculado;
        this.periodoInicio = periodoInicio;
        this.periodoFim = periodoFim;
        this.executadoPorId = executadoPorId;
        this.executadoPor = executadoPor;
        this.dataExecucao = dataExecucao;
        this.detalheRastreio = detalheRastreio;
    }

    public SnapshotIndicadorId getId() { return id; }

    public IndicadorId getIndicadorId() { return indicadorId; }

    public double getValorCalculado() { return valorCalculado; }

    public LocalDate getPeriodoInicio() { return periodoInicio; }

    public LocalDate getPeriodoFim() { return periodoFim; }

    public Integer getExecutadoPorId() { return executadoPorId; }

    public String getExecutadoPor() { return executadoPor; }

    public LocalDateTime getDataExecucao() { return dataExecucao; }

    public String getDetalheRastreio() { return detalheRastreio; }
}
