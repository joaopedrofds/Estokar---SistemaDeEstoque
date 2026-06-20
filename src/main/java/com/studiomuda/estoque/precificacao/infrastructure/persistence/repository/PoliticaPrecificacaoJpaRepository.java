package com.studiomuda.estoque.precificacao.infrastructure.persistence.repository;

import com.studiomuda.estoque.precificacao.infrastructure.persistence.entity.PoliticaPrecificacaoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PoliticaPrecificacaoJpaRepository extends JpaRepository<PoliticaPrecificacaoJpaEntity, Long> {
    Optional<PoliticaPrecificacaoJpaEntity> findFirstByProdutoIdAndAtivaTrueOrderByAtualizadoEmDesc(int produtoId);
    List<PoliticaPrecificacaoJpaEntity> findAllByOrderByAtualizadoEmDesc();
    List<PoliticaPrecificacaoJpaEntity> findByAtivaTrueOrderByAtualizadoEmDesc();
    long countByAtivaTrue();
}
