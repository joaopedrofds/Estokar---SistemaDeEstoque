package com.studiomuda.estoque.repository;

import com.studiomuda.estoque.model.SnapshotIndicador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SnapshotIndicadorRepository extends JpaRepository<SnapshotIndicador, Integer> {

    List<SnapshotIndicador> findAllByOrderByDataExecucaoDesc();

    List<SnapshotIndicador> findByIndicadorIdOrderByDataExecucaoDesc(int indicadorId);

    SnapshotIndicador findFirstByIndicadorIdOrderByDataExecucaoDesc(int indicadorId);
}
