package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.OrdemCompraJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrdemCompraJpaRepository extends JpaRepository<OrdemCompraJpaEntity, Integer> {
    @Query("select distinct oc from OrdemCompraJpaEntity oc " +
            "join fetch oc.fornecedor f " +
            "left join fetch oc.itens i " +
            "left join fetch i.produto p " +
            "order by oc.dataCriacao desc, oc.id desc")
    List<OrdemCompraJpaEntity> listarComItens();

    @Query("select distinct oc from OrdemCompraJpaEntity oc " +
            "join fetch oc.fornecedor f " +
            "left join fetch oc.itens i " +
            "left join fetch i.produto p " +
            "where oc.id = :id")
    Optional<OrdemCompraJpaEntity> buscarCompletaPorId(@Param("id") Integer id);

    @Query("select case when count(oc) > 0 then true else false end from OrdemCompraJpaEntity oc " +
            "join oc.itens i " +
            "where oc.status = :status and i.produto.id = :produtoId")
    boolean existeRascunhoParaProduto(@Param("status") String status, @Param("produtoId") Integer produtoId);
}
