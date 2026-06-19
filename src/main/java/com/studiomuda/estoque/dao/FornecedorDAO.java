package com.studiomuda.estoque.dao;
import org.springframework.stereotype.Repository;


import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.Fornecedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FornecedorDAO {

    public void inserir(Fornecedor f) throws SQLException {
        String sql = "INSERT INTO fornecedor (nome, lead_time_dias, ativo) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, f.getNome());
            stmt.setInt(2, f.getLeadTimeDias());
            stmt.setBoolean(3, f.isAtivo());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) f.setId(rs.getInt(1));
            }
        }
    }

    public List<Fornecedor> listarTodos() throws SQLException {
        List<Fornecedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM fornecedor WHERE ativo = true ORDER BY nome";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Fornecedor f = new Fornecedor();
                f.setId(rs.getInt("id"));
                f.setNome(rs.getString("nome"));
                f.setLeadTimeDias(rs.getInt("lead_time_dias"));
                f.setAtivo(rs.getBoolean("ativo"));
                lista.add(f);
            }
        }
        return lista;
    }

    public Fornecedor buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM fornecedor WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Fornecedor f = new Fornecedor();
                    f.setId(rs.getInt("id"));
                    f.setNome(rs.getString("nome"));
                    f.setLeadTimeDias(rs.getInt("lead_time_dias"));
                    f.setAtivo(rs.getBoolean("ativo"));
                    return f;
                }
            }
        }
        return null;
    }

    public void atualizar(Fornecedor f) throws SQLException {
        String sql = "UPDATE fornecedor SET nome = ?, lead_time_dias = ?, ativo = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, f.getNome());
            stmt.setInt(2, f.getLeadTimeDias());
            stmt.setBoolean(3, f.isAtivo());
            stmt.setInt(4, f.getId());
            stmt.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM fornecedor WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
