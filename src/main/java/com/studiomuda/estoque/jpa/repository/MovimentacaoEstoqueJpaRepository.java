package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.MovimentacaoEstoqueJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoEstoqueJpaRepository extends JpaRepository<MovimentacaoEstoqueJpaEntity, Integer> {
}
