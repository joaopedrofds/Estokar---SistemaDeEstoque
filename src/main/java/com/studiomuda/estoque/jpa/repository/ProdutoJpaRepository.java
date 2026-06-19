package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.ProdutoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoJpaRepository extends JpaRepository<ProdutoJpaEntity, Integer> {
    List<ProdutoJpaEntity> findAllByOrderByNomeAsc();
}
