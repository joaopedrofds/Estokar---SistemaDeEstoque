package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.FornecedorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FornecedorJpaRepository extends JpaRepository<FornecedorJpaEntity, Integer> {
    List<FornecedorJpaEntity> findByAtivoTrueOrderByLeadTimeDiasAscNomeAsc();

    Optional<FornecedorJpaEntity> findFirstByNomeIgnoreCaseAndLeadTimeDias(String nome, Integer leadTimeDias);
}
