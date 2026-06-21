package com.studiomuda.estoque.indicadores.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositório Spring Data do {@link SnapshotIndicadorJpa} (chave {@code String}).
 */
public interface SnapshotIndicadorJpaRepository extends JpaRepository<SnapshotIndicadorJpa, String> {

    List<SnapshotIndicadorJpa> findAllByOrderByDataExecucaoDesc();

    SnapshotIndicadorJpa findFirstByIndicadorIdOrderByDataExecucaoDesc(String indicadorId);
}
