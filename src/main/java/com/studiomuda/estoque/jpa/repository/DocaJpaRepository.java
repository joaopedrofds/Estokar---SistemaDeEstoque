package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.DocaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocaJpaRepository extends JpaRepository<DocaJpaEntity, Integer> {
    List<DocaJpaEntity> findByAtivaTrueOrderByNomeAsc();
}
