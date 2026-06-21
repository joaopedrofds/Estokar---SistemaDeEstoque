package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.PedidoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface PedidoJpaRepository extends JpaRepository<PedidoJpaEntity, Integer> {
    List<PedidoJpaEntity> findByStatusOrderByDataCancelamentoDesc(String status);

    @Query("select p.dataRequisicao from PedidoJpaEntity p where p.cliente.id = :clienteId and upper(p.status) = 'CONCLUIDO' and p.dataRequisicao is not null order by p.dataRequisicao desc")
    List<Date> listarDatasComprasConfirmadas(@Param("clienteId") Integer clienteId);

    long countByBeneficioAplicadoId(Integer beneficioId);

    @Query("select distinct p.status from PedidoJpaEntity p where p.status is not null and p.status <> ''")
    List<String> findDistinctStatus();

    /**
     * Retorna [receita_total, quantidade_pedidos] para pedidos com status_pagamento = 'PAGO'
     * no intervalo de datas (data_pagamento ou data_requisicao).
     * Replica a lógica de PedidoDAO.resumirReceitaPaga().
     */
    @Query(value =
        "SELECT COALESCE(SUM(sub.subtotal), 0), COUNT(*) " +
        "FROM (" +
        "  SELECT p.id, COALESCE(SUM(ip.quantidade * pr.valor), 0) - COALESCE(p.valor_desconto, 0) AS subtotal " +
        "  FROM pedido p " +
        "  JOIN item_pedido ip ON ip.id_pedido = p.id " +
        "  JOIN produto pr ON pr.id = ip.id_produto " +
        "  WHERE p.status_pagamento = 'PAGO' " +
        "  AND COALESCE(p.data_pagamento, p.data_requisicao) BETWEEN :inicio AND :fim " +
        "  GROUP BY p.id" +
        ") sub",
        nativeQuery = true)
    List<Object[]> resumirReceitaPagaNativo(@Param("inicio") Date inicio, @Param("fim") Date fim);
}
