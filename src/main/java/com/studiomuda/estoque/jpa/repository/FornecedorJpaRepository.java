package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.FornecedorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FornecedorJpaRepository extends JpaRepository<FornecedorJpaEntity, Integer> {
    List<FornecedorJpaEntity> findByAtivoTrueOrderByLeadTimeDiasAscNomeAsc();
}
