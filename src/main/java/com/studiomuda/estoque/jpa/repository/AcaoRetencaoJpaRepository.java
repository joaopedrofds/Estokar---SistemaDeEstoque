package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.AcaoRetencaoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AcaoRetencaoJpaRepository extends JpaRepository<AcaoRetencaoJpaEntity, Integer> {
    List<AcaoRetencaoJpaEntity> findAllByOrderByDataCriacaoDesc();
    Optional<AcaoRetencaoJpaEntity> findFirstByClienteIdAndAtivaTrue(Integer clienteId);
    long countByFaixaId(Integer faixaId);
}
