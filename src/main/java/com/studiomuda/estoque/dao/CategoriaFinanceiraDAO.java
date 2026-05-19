package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.CategoriaFinanceira;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoriaFinanceiraDAO {

    public void inserir(CategoriaFinanceira categoria) throws SQLException {
        String sql = "INSERT INTO categoria_financeira (nome, tipo, origem_sistema, descricao, ativo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getTipo());
            stmt.setString(3, categoria.getOrigemSistema());
            stmt.setString(4, categoria.getDescricao());
            stmt.setBoolean(5, categoria.isAtivo());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    categoria.setId(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(CategoriaFinanceira categoria) throws SQLException {
        String sql = "UPDATE categoria_financeira SET nome = ?, tipo = ?, origem_sistema = ?, descricao = ?, ativo = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getTipo());
            stmt.setString(3, categoria.getOrigemSistema());
            stmt.setString(4, categoria.getDescricao());
            stmt.setBoolean(5, categoria.isAtivo());
            stmt.setInt(6, categoria.getId());
            stmt.executeUpdate();
        }
    }

    public CategoriaFinanceira buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM categoria_financeira WHERE id = ?";
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

    public List<CategoriaFinanceira> listarTodos() throws SQLException {
        List<CategoriaFinanceira> lista = new ArrayList<>();
        String sql = "SELECT * FROM categoria_financeira ORDER BY ativo DESC, tipo ASC, nome ASC";
        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<CategoriaFinanceira> listarAtivas() throws SQLException {
        List<CategoriaFinanceira> lista = new ArrayList<>();
        String sql = "SELECT * FROM categoria_financeira WHERE ativo = TRUE ORDER BY tipo ASC, nome ASC";
        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public void inativar(int id) throws SQLException {
        String sql = "UPDATE categoria_financeira SET ativo = FALSE WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private CategoriaFinanceira mapear(ResultSet rs) throws SQLException {
        return new CategoriaFinanceira(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("tipo"),
                rs.getString("origem_sistema"),
                rs.getString("descricao"),
                rs.getBoolean("ativo")
        );
    }
}
