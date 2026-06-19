package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.ParametroEstoqueJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParametroEstoqueJpaRepository extends JpaRepository<ParametroEstoqueJpaEntity, Integer> {
    @Query("select pe from ParametroEstoqueJpaEntity pe " +
            "join fetch pe.produto p " +
            "join fetch pe.fornecedor f " +
            "order by p.nome asc")
    List<ParametroEstoqueJpaEntity> listarComRelacoes();

    @Query("select pe from ParametroEstoqueJpaEntity pe " +
            "join fetch pe.produto p " +
            "join fetch pe.fornecedor f " +
            "where p.id = :produtoId")
    Optional<ParametroEstoqueJpaEntity> buscarPorProduto(@Param("produtoId") Integer produtoId);
}
