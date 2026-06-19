package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.ParametroCancelamentoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParametroCancelamentoJpaRepository extends JpaRepository<ParametroCancelamentoJpaEntity, Integer> {
    Optional<ParametroCancelamentoJpaEntity> findFirstByOrderByIdAsc();
}
