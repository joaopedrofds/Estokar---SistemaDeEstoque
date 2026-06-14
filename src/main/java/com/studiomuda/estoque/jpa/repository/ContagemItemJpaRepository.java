package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.ContagemItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContagemItemJpaRepository extends JpaRepository<ContagemItemJpaEntity, Integer> {
    List<ContagemItemJpaEntity> findBySessaoIdOrderByDataContagemDescIdDesc(Integer sessaoId);

    Optional<ContagemItemJpaEntity> findTopBySessaoIdAndProdutoIdOrderByDataContagemDescIdDesc(Integer sessaoId, Integer produtoId);

    boolean existsBySessaoIdAndProdutoId(Integer sessaoId, Integer produtoId);
}
