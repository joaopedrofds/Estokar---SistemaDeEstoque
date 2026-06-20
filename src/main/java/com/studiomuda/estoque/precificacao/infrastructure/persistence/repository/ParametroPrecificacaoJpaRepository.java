package com.studiomuda.estoque.precificacao.infrastructure.persistence.repository;

import com.studiomuda.estoque.precificacao.infrastructure.persistence.entity.ParametroPrecificacaoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ParametroPrecificacaoJpaRepository extends JpaRepository<ParametroPrecificacaoJpaEntity, Long> {
    Optional<ParametroPrecificacaoJpaEntity> findFirstByOrderByIdDesc();
}
