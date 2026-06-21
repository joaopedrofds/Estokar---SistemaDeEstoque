package com.studiomuda.estoque.indicadores.infrastructure.persistence;

import com.studiomuda.estoque.indicadores.domain.IndicadorId;
import com.studiomuda.estoque.indicadores.domain.SnapshotIndicador;
import com.studiomuda.estoque.indicadores.domain.SnapshotIndicadorId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade JPA do agregado {@link SnapshotIndicador} (E-13). Id como {@code String}
 * do {@link SnapshotIndicadorId}; {@code data_execucao} é gerada pelo banco
 * ({@code insertable=false, updatable=false}). Mapeamento manual via
 * {@code fromDomain}/{@code toDomain} — padrão PetCollar.
 */
@Entity
@Table(name = "snapshot_indicador")
public class SnapshotIndicadorJpa {

    @Id
    private String id;

    @Column(name = "indicador_id", nullable = false)
    private String indicadorId;

    @Column(name = "valor_calculado", nullable = false)
    private double valorCalculado;

    @Column(name = "periodo_inicio", nullable = false)
    private LocalDate periodoInicio;

    @Column(name = "periodo_fim", nullable = false)
    private LocalDate periodoFim;

    @Column(name = "executado_por_id")
    private Integer executadoPorId;

    @Column(name = "executado_por")
    private String executadoPor;

    @Column(name = "data_execucao", insertable = false, updatable = false)
    private LocalDateTime dataExecucao;

    @Column(name = "detalhe_rastreio")
    private String detalheRastreio;

    protected SnapshotIndicadorJpa() {}

    public static SnapshotIndicadorJpa fromDomain(SnapshotIndicador s) {
        SnapshotIndicadorJpa jpa = new SnapshotIndicadorJpa();
        jpa.id = s.getId().getValor();
        jpa.indicadorId = s.getIndicadorId().getValor();
        jpa.valorCalculado = s.getValorCalculado();
        jpa.periodoInicio = s.getPeriodoInicio();
        jpa.periodoFim = s.getPeriodoFim();
        jpa.executadoPorId = s.getExecutadoPorId();
        jpa.executadoPor = s.getExecutadoPor();
        jpa.detalheRastreio = s.getDetalheRastreio();
        return jpa;
    }

    public SnapshotIndicador toDomain() {
        return new SnapshotIndicador(
                SnapshotIndicadorId.de(id),
                IndicadorId.de(indicadorId),
                valorCalculado,
                periodoInicio,
                periodoFim,
                executadoPorId,
                executadoPor,
                dataExecucao,
                detalheRastreio);
    }
}
