package com.studiomuda.estoque.indicadores.infrastructure.persistence;

import com.studiomuda.estoque.indicadores.domain.AlertaId;
import com.studiomuda.estoque.indicadores.domain.AlertaIndicador;
import com.studiomuda.estoque.indicadores.domain.IndicadorId;
import com.studiomuda.estoque.indicadores.domain.SnapshotIndicadorId;
import com.studiomuda.estoque.indicadores.domain.StatusAlerta;
import com.studiomuda.estoque.indicadores.domain.TipoViolacao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Entidade JPA do agregado {@link AlertaIndicador}. Id como {@code String}
 * do {@link AlertaId}; {@code data_alerta} é gerada pelo banco
 * ({@code insertable=false, updatable=false}). Enums e VOs persistidos como
 * {@code String}. Mapeamento manual via {@code fromDomain}/{@code toDomain} —
 * padrão PetCollar.
 */
@Entity
@Table(name = "alerta_indicador")
public class AlertaIndicadorJpa {

    @Id
    private String id;

    @Column(name = "indicador_id", nullable = false)
    private String indicadorId;

    @Column(name = "snapshot_id", nullable = false)
    private String snapshotId;

    @Column(name = "tipo_violacao", nullable = false)
    private String tipoViolacao;

    @Column(name = "valor_esperado")
    private double valorEsperado;

    @Column(name = "valor_encontrado")
    private double valorEncontrado;

    @Column(name = "mensagem", nullable = false)
    private String mensagem;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "resolvido_por")
    private String resolvidoPor;

    @Column(name = "observacao")
    private String observacao;

    @Column(name = "data_alerta", insertable = false, updatable = false)
    private LocalDateTime dataAlerta;

    @Column(name = "data_resolucao")
    private LocalDateTime dataResolucao;

    protected AlertaIndicadorJpa() {}

    public static AlertaIndicadorJpa fromDomain(AlertaIndicador a) {
        AlertaIndicadorJpa jpa = new AlertaIndicadorJpa();
        jpa.id = a.getId().getValor();
        jpa.indicadorId = a.getIndicadorId().getValor();
        jpa.snapshotId = a.getSnapshotId().getValor();
        jpa.tipoViolacao = a.getTipoViolacao().name();
        jpa.valorEsperado = a.getValorEsperado();
        jpa.valorEncontrado = a.getValorEncontrado();
        jpa.mensagem = a.getMensagem();
        jpa.status = a.getStatus().name();
        jpa.resolvidoPor = a.getResolvidoPor();
        jpa.observacao = a.getObservacao();
        jpa.dataResolucao = a.getDataResolucao();
        return jpa;
    }

    public AlertaIndicador toDomain() {
        return new AlertaIndicador(
                AlertaId.de(id),
                IndicadorId.de(indicadorId),
                SnapshotIndicadorId.de(snapshotId),
                TipoViolacao.de(tipoViolacao),
                valorEsperado,
                valorEncontrado,
                mensagem,
                StatusAlerta.de(status),
                resolvidoPor,
                observacao,
                dataAlerta,
                dataResolucao);
    }
}
