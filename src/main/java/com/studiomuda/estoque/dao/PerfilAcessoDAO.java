package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.PerfilAcesso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PerfilAcessoDAO {

    public void inserir(PerfilAcesso perfil) throws SQLException {
        String sql = "INSERT INTO perfil_acesso (nome, descricao, ativo) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, perfil.getNome());
            stmt.setString(2, perfil.getDescricao());
            stmt.setBoolean(3, perfil.isAtivo());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    perfil.setId(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(PerfilAcesso perfil) throws SQLException {
        String sql = "UPDATE perfil_acesso SET nome = ?, descricao = ?, ativo = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, perfil.getNome());
            stmt.setString(2, perfil.getDescricao());
            stmt.setBoolean(3, perfil.isAtivo());
            stmt.setInt(4, perfil.getId());
            stmt.executeUpdate();
        }
    }

    public PerfilAcesso buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM perfil_acesso WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public PerfilAcesso buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM perfil_acesso WHERE nome = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public List<PerfilAcesso> listarTodos() throws SQLException {
        List<PerfilAcesso> perfis = new ArrayList<>();
        String sql = "SELECT * FROM perfil_acesso ORDER BY ativo DESC, nome ASC";
        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                perfis.add(mapear(rs));
            }
        }
        return perfis;
    }

    public List<PerfilAcesso> listarAtivos() throws SQLException {
        List<PerfilAcesso> perfis = new ArrayList<>();
        String sql = "SELECT * FROM perfil_acesso WHERE ativo = TRUE ORDER BY nome ASC";
        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                perfis.add(mapear(rs));
            }
        }
        return perfis;
    }

    public void inativar(int id) throws SQLException {
        String sql = "UPDATE perfil_acesso SET ativo = FALSE WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private PerfilAcesso mapear(ResultSet rs) throws SQLException {
        return new PerfilAcesso(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("descricao"),
                rs.getBoolean("ativo")
        );
    }
}
