package com.studiomuda.estoque.precificacao.infrastructure.persistence.repository;

import com.studiomuda.estoque.precificacao.infrastructure.persistence.entity.ComponentePrecificacaoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComponentePrecificacaoJpaRepository extends JpaRepository<ComponentePrecificacaoJpaEntity, Long> {
    List<ComponentePrecificacaoJpaEntity> findBySimulacaoIdOrderByOrdemAsc(Long simulacaoId);
}
