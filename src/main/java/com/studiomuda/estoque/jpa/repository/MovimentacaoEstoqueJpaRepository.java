package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.MovimentacaoEstoqueJpaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
public interface MovimentacaoEstoqueJpaRepository extends JpaRepository<MovimentacaoEstoqueJpaEntity, Integer> {
    @Query("select coalesce(sum(m.quantidade), 0) " +
            "from MovimentacaoEstoqueJpaEntity m " +
            "where m.produtoId = :produtoId " +
            "and lower(m.tipo) = 'saida' " +
            "and m.data >= :dataMinima")
    Long somarSaidasUltimos90Dias(@Param("produtoId") Integer produtoId, @Param("dataMinima") Date dataMinima);

    @Query("select coalesce(function('datediff', max(m.data), min(m.data)) + 1, 1) " +
            "from MovimentacaoEstoqueJpaEntity m " +
            "where m.produtoId = :produtoId " +
            "and lower(m.tipo) = 'saida' " +
            "and m.data >= :dataMinima")
    Long calcularDiasConsumo(@Param("produtoId") Integer produtoId, @Param("dataMinima") Date dataMinima);
}
