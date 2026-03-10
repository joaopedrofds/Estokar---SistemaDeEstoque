package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.conexao.Conexao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/kpis")
public class KpiController {

    @GetMapping("/contadores")
    public ResponseEntity<?> obterContadores() {
        try (Connection conn = Conexao.getConnection()) {
            String sql = "SELECT tipo_contador, valor_atual FROM contadores_sistema";
            Map<String, Integer> contadores = new HashMap<>();

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    contadores.put(rs.getString("tipo_contador"), rs.getInt("valor_atual"));
                }
            }

            return ResponseEntity.ok(contadores);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao obter contadores: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> obterDadosDashboard() {
        try (Connection conn = Conexao.getConnection()) {
            Map<String, Object> dados = new HashMap<>();

            // Obter contadores principais
            String sqlContadores = "SELECT tipo_contador, valor_atual FROM contadores_sistema WHERE tipo_contador IN ('total_produtos', 'total_clientes', 'total_pedidos', 'total_movimentacoes')";
            try (PreparedStatement stmt = conn.prepareStatement(sqlContadores);
                    ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    dados.put(rs.getString("tipo_contador"), rs.getInt("valor_atual"));
                }
            }

            // Obter alertas de estoque
            String sqlAlertas = "SELECT COUNT(*) as total FROM vw_estoque_critico";
            try (PreparedStatement stmt = conn.prepareStatement(sqlAlertas);
                    ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    dados.put("alertas_estoque", rs.getInt("total"));
                }
            }

            // Obter vendas do mês (simulado)
            String sqlVendas = "SELECT COUNT(*) as vendas_mes FROM pedidos WHERE MONTH(data_pedido) = MONTH(NOW()) AND YEAR(data_pedido) = YEAR(NOW())";
            try (PreparedStatement stmt = conn.prepareStatement(sqlVendas);
                    ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    dados.put("vendas_mes", rs.getInt("vendas_mes"));
                }
            }

            return ResponseEntity.ok(dados);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao obter dados do dashboard: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/recalcular")
    public ResponseEntity<?> recalcularContadores() {
        try (Connection conn = Conexao.getConnection()) {
            String sql = "CALL sp_recalcular_contadores()";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.executeUpdate();
            }

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("mensagem", "Contadores recalculados com sucesso!");
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao recalcular contadores: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
