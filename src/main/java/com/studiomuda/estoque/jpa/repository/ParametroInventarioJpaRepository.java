package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.ParametroInventarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParametroInventarioJpaRepository extends JpaRepository<ParametroInventarioJpaEntity, Integer> {
    Optional<ParametroInventarioJpaEntity> findFirstByOrderByIdAsc();
}
