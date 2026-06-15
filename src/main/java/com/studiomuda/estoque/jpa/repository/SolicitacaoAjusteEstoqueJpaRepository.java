package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.SolicitacaoAjusteEstoqueJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitacaoAjusteEstoqueJpaRepository extends JpaRepository<SolicitacaoAjusteEstoqueJpaEntity, Integer> {
    List<SolicitacaoAjusteEstoqueJpaEntity> findAllByOrderByDataSolicitacaoDescIdDesc();
    List<SolicitacaoAjusteEstoqueJpaEntity> findByStatusOrderByDataSolicitacaoDescIdDesc(String status);
}
