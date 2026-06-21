package com.studiomuda.estoque.indicadores.infrastructure.persistence;

import com.studiomuda.estoque.indicadores.domain.ISnapshotIndicadorRepositorio;
import com.studiomuda.estoque.indicadores.domain.IndicadorId;
import com.studiomuda.estoque.indicadores.domain.SnapshotIndicador;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter JPA que implementa a porta {@link ISnapshotIndicadorRepositorio},
 * traduzindo domínio ↔ {@link SnapshotIndicadorJpa} via {@code fromDomain}/{@code toDomain}.
 */
@Repository
public class SnapshotIndicadorRepositorioJpa implements ISnapshotIndicadorRepositorio {

    private final SnapshotIndicadorJpaRepository jpa;

    public SnapshotIndicadorRepositorioJpa(SnapshotIndicadorJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void salvar(SnapshotIndicador snapshot) {
        jpa.save(SnapshotIndicadorJpa.fromDomain(snapshot));
    }

    @Override
    public Optional<SnapshotIndicador> buscarUltimoPorIndicador(IndicadorId indicadorId) {
        return Optional.ofNullable(jpa.findFirstByIndicadorIdOrderByDataExecucaoDesc(indicadorId.getValor()))
                .map(SnapshotIndicadorJpa::toDomain);
    }

    @Override
    public List<SnapshotIndicador> listarTodosOrdenadoPorExecucao() {
        return jpa.findAllByOrderByDataExecucaoDesc().stream()
                .map(SnapshotIndicadorJpa::toDomain)
                .toList();
    }
}
