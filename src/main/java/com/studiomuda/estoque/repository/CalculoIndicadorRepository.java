package com.studiomuda.estoque.repository;

import com.studiomuda.estoque.model.IndicadorOperacional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * Repositório de leitura para o cálculo dos indicadores a partir das transações reais
 * (pedido, item_pedido, produto). Usa consultas nativas porque os indicadores agregam
 * dados de várias tabelas; está ancorado em {@link IndicadorOperacional} apenas por
 * exigência do Spring Data (a entidade não é usada nas consultas).
 */
@Repository
public interface CalculoIndicadorRepository extends JpaRepository<IndicadorOperacional, Integer> {

    @Query(nativeQuery = true, value =
            "SELECT COALESCE(AVG(sub.total), 0) FROM (" +
            "  SELECT p.id, COALESCE(SUM(ip.quantidade * pr.valor), 0) - COALESCE(p.valor_desconto, 0) AS total " +
            "  FROM pedido p " +
            "  LEFT JOIN item_pedido ip ON p.id = ip.id_pedido " +
            "  LEFT JOIN produto pr ON ip.id_produto = pr.id " +
            "  WHERE p.data_requisicao >= :inicio AND p.data_requisicao <= :fim " +
            "  GROUP BY p.id" +
            ") sub")
    Double calcularTicketMedio(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query(nativeQuery = true, value =
            "SELECT COALESCE(" +
            "  (SELECT COUNT(*) FROM pedido WHERE (status_pagamento = 'CANCELADO' OR status = 'CANCELADO') " +
            "     AND data_requisicao >= :inicio AND data_requisicao <= :fim) * 100.0 / " +
            "  NULLIF((SELECT COUNT(*) FROM pedido WHERE data_requisicao >= :inicio AND data_requisicao <= :fim), 0), " +
            "  0.0)")
    Double calcularTaxaCancelamento(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) * 1.0 FROM produto WHERE quantidade <= 5")
    Double calcularEstoqueCritico();

    @Query(nativeQuery = true, value = "SELECT COUNT(*) * 1.0 FROM produto WHERE quantidade = 0")
    Double calcularSemEstoque();
}
