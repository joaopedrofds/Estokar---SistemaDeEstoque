package com.studiomuda.estoque.indicadores.domain;

import java.util.List;
import java.util.Optional;

/**
 * Porta de domínio do agregado {@link SnapshotIndicador} (E-13). Definida no
 * domínio (Java puro), implementada na infraestrutura por
 * {@code SnapshotIndicadorRepositorioJpa}.
 */
public interface ISnapshotIndicadorRepositorio {

    void salvar(SnapshotIndicador snapshot);

    /** Último snapshot apurado de um indicador (mais recente por data de execução). */
    Optional<SnapshotIndicador> buscarUltimoPorIndicador(IndicadorId indicadorId);

    /** Todos os snapshots, mais recente primeiro. */
    List<SnapshotIndicador> listarTodosOrdenadoPorExecucao();
}
