package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.DistribuidoraJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistribuidoraJpaRepository extends JpaRepository<DistribuidoraJpaEntity, Integer> {
    List<DistribuidoraJpaEntity> findByAtivaTrueOrderByNomeAsc();
}
