package com.studiomuda.estoque.precificacao.infrastructure.persistence.repository;

import com.studiomuda.estoque.precificacao.domain.model.StatusPrecificacao;
import com.studiomuda.estoque.precificacao.infrastructure.persistence.entity.SimulacaoPrecificacaoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SimulacaoPrecificacaoJpaRepository extends JpaRepository<SimulacaoPrecificacaoJpaEntity, Long> {
    List<SimulacaoPrecificacaoJpaEntity> findAllByOrderByDataSimulacaoDesc();
    List<SimulacaoPrecificacaoJpaEntity> findByStatusOrderByDataSimulacaoDesc(StatusPrecificacao status);
    List<SimulacaoPrecificacaoJpaEntity> findByProdutoIdOrderByDataSimulacaoDesc(int produtoId);
    Optional<SimulacaoPrecificacaoJpaEntity> findFirstByProdutoIdOrderByDataSimulacaoDesc(int produtoId);
    List<SimulacaoPrecificacaoJpaEntity> findTop8ByOrderByDataSimulacaoDesc();
    long countByStatus(StatusPrecificacao status);
}
