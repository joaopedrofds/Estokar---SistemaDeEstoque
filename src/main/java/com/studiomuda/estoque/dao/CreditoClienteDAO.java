package com.studiomuda.estoque.dao;
import org.springframework.stereotype.Repository;




import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.CreditoCliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Repository
public class CreditoClienteDAO {

    public List<CreditoCliente> listarPorCliente(int clienteId) throws SQLException {
        List<CreditoCliente> lista = new ArrayList<>();
        String sql = "SELECT cc.*, c.nome as cliente_nome FROM credito_cliente cc " +
                     "JOIN cliente c ON cc.cliente_id = c.id " +
                     "WHERE cc.cliente_id = ? ORDER BY cc.data_geracao DESC";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CreditoCliente cc = new CreditoCliente();
                    cc.setId(rs.getInt("id"));
                    cc.setClienteId(rs.getInt("cliente_id"));
                    cc.setDevolucaoId(rs.getInt("devolucao_id"));
                    cc.setValor(rs.getDouble("valor"));
                    cc.setStatus(rs.getString("status"));
                    cc.setDataGeracao(rs.getTimestamp("data_geracao") != null ?
                            rs.getTimestamp("data_geracao").toLocalDateTime() : null);
                    cc.setDataUtilizacao(rs.getTimestamp("data_utilizacao") != null ?
                            rs.getTimestamp("data_utilizacao").toLocalDateTime() : null);
                    lista.add(cc);
                }
            }
        }
        return lista;
    }

    public void inserir(CreditoCliente cc) throws SQLException {
        String sql = "INSERT INTO credito_cliente (cliente_id, devolucao_id, valor, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, cc.getClienteId());
            stmt.setInt(2, cc.getDevolucaoId());
            stmt.setDouble(3, cc.getValor());
            stmt.setString(4, cc.getStatus());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) cc.setId(rs.getInt(1));
            }
        }
    }

    public void atualizarStatus(int id, String status) throws SQLException {
        String sql = "UPDATE credito_cliente SET status = ?, data_utilizacao = NOW() WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }
}
