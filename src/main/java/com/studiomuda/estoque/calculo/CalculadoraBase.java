package com.studiomuda.estoque.calculo;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.IndicadorOperacional;

import java.sql.*;
import java.time.LocalDate;

/**
 * Componente concreto (ConcreteComponent) do padrão Decorator.
 *
 * Executa o cálculo "cru" do indicador a partir das transações reais
 * (pedido, item_pedido, produto) via JDBC, sem nenhum comportamento extra.
 * Os decorators ({@link CalculadoraDecorator}) embrulham este objeto para
 * adicionar log, validação, arredondamento etc.
 */
public class CalculadoraBase implements CalculadoraIndicador {

    @Override
    public double calcular(IndicadorOperacional indicador, LocalDate inicio, LocalDate fim) throws SQLException {
        String tipoCalculo = indicador.getTipoCalculo();
        String sql;
        boolean usaDatas;

        switch (tipoCalculo) {
            case "TICKET_MEDIO":
                sql = "SELECT COALESCE(AVG(sub.total), 0) AS valor FROM (" +
                      "  SELECT p.id, COALESCE(SUM(ip.quantidade * pr.valor), 0) - COALESCE(p.valor_desconto, 0) AS total " +
                      "  FROM pedido p " +
                      "  LEFT JOIN item_pedido ip ON p.id = ip.id_pedido " +
                      "  LEFT JOIN produto pr ON ip.id_produto = pr.id " +
                      "  WHERE p.data_requisicao >= ? AND p.data_requisicao <= ? " +
                      "  GROUP BY p.id" +
                      ") sub";
                usaDatas = true;
                break;

            case "ESTOQUE_CRITICO":
                sql = "SELECT COUNT(*) AS valor FROM produto WHERE quantidade <= 5";
                usaDatas = false;
                break;

            case "TAXA_CANCELAMENTO":
                sql = "SELECT COALESCE(" +
                      "  (SELECT COUNT(*) FROM pedido WHERE (status_pagamento = 'CANCELADO' OR status = 'CANCELADO') AND data_requisicao >= ? AND data_requisicao <= ?) * 100.0 / " +
                      "  NULLIF((SELECT COUNT(*) FROM pedido WHERE data_requisicao >= ? AND data_requisicao <= ?), 0), " +
                      "  0.0" +
                      ") AS valor";
                usaDatas = true;
                break;

            case "SEM_ESTOQUE":
                sql = "SELECT COUNT(*) AS valor FROM produto WHERE quantidade = 0";
                usaDatas = false;
                break;

            default:
                return 0.0;
        }

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (usaDatas) {
                stmt.setDate(1, Date.valueOf(inicio));
                stmt.setDate(2, Date.valueOf(fim));
                if (tipoCalculo.equals("TAXA_CANCELAMENTO")) {
                    stmt.setDate(3, Date.valueOf(inicio));
                    stmt.setDate(4, Date.valueOf(fim));
                }
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("valor");
                }
            }
        }
        return 0.0;
    }
}
