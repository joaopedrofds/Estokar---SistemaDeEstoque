package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.PoliticaCreditoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PoliticaCreditoJpaRepository extends JpaRepository<PoliticaCreditoJpaEntity, Integer> {
    Optional<PoliticaCreditoJpaEntity> findFirstByAtivaTrueAndDataFimIsNullOrderByDataInicioDesc();
    List<PoliticaCreditoJpaEntity> findByAtivaTrueAndDataFimIsNull();
    List<PoliticaCreditoJpaEntity> findAllByOrderByDataInicioDesc();
}
