package com.studiomuda.estoque.infrastructure.persistence.kpi;

import com.studiomuda.estoque.application.kpi.ports.KpiQueryPort;
import com.studiomuda.estoque.conexao.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class KpiQueryJdbc implements KpiQueryPort {

    @Override
    public Map<String, Integer> obterContadores() {
        Map<String, Integer> contadores = new HashMap<>();
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT tipo_contador, valor_atual FROM contadores_sistema");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                contadores.put(rs.getString("tipo_contador"), rs.getInt("valor_atual"));
            }
            return contadores;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter contadores: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> obterDadosDashboard() {
        Map<String, Object> dados = new HashMap<>();
        try (Connection conn = Conexao.getConnection()) {
            String sqlContadores = "SELECT tipo_contador, valor_atual FROM contadores_sistema WHERE tipo_contador IN ('total_produtos', 'total_clientes', 'total_pedidos', 'total_movimentacoes')";
            try (PreparedStatement stmt = conn.prepareStatement(sqlContadores);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dados.put(rs.getString("tipo_contador"), rs.getInt("valor_atual"));
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) as total FROM vw_estoque_critico");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dados.put("alertas_estoque", rs.getInt("total"));
                }
            }

            String sqlVendas = "SELECT COUNT(*) as vendas_mes FROM pedidos WHERE MONTH(data_pedido) = MONTH(NOW()) AND YEAR(data_pedido) = YEAR(NOW())";
            try (PreparedStatement stmt = conn.prepareStatement(sqlVendas);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dados.put("vendas_mes", rs.getInt("vendas_mes"));
                }
            }
            return dados;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter dados do dashboard: " + e.getMessage(), e);
        }
    }

    @Override
    public void recalcularContadores() {
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement("CALL sp_recalcular_contadores()")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao recalcular contadores: " + e.getMessage(), e);
        }
    }
}
