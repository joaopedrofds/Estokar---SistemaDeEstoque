package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.InventarioEscopoProdutoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventarioEscopoProdutoJpaRepository extends JpaRepository<InventarioEscopoProdutoJpaEntity, Integer> {
    List<InventarioEscopoProdutoJpaEntity> findBySessaoIdOrderByProduto_NomeAsc(Integer sessaoId);

    long countBySessaoId(Integer sessaoId);

    Optional<InventarioEscopoProdutoJpaEntity> findBySessaoIdAndProdutoId(Integer sessaoId, Integer produtoId);
}
