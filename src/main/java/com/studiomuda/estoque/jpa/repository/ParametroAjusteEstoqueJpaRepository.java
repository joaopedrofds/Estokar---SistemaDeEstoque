package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.ParametroAjusteEstoqueJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParametroAjusteEstoqueJpaRepository extends JpaRepository<ParametroAjusteEstoqueJpaEntity, Integer> {
    Optional<ParametroAjusteEstoqueJpaEntity> findFirstByOrderByIdAsc();
}
