package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.HistoricoAjusteEstoqueJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoAjusteEstoqueJpaRepository extends JpaRepository<HistoricoAjusteEstoqueJpaEntity, Integer> {
    List<HistoricoAjusteEstoqueJpaEntity> findBySolicitacaoIdOrderByDataEventoDescIdDesc(Integer solicitacaoId);
}
