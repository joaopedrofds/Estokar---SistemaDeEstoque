package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public void inserir(Produto p) throws SQLException {
        String sql = "INSERT INTO produto (nome, descricao, tipo, quantidade, valor) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getDescricao());
            stmt.setString(3, p.getTipo());
            stmt.setInt(4, p.getQuantidade());
            stmt.setDouble(5, p.getValor());
            stmt.executeUpdate();
        }
    }

    public List<Produto> listar() throws SQLException {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produto";
        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Produto p = new Produto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getString("tipo"),
                        rs.getInt("quantidade"),
                        rs.getDouble("valor")
                );
                lista.add(p);
            }
        }
        return lista;
    }

    public Produto buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM produto WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Produto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getString("tipo"),
                        rs.getInt("quantidade"),
                        rs.getDouble("valor")
                );
            }
        }
        return null;
    }

    public void atualizar(Produto p) throws SQLException {
        String sql = "UPDATE produto SET nome = ?, descricao = ?, tipo = ?, quantidade = ?, valor = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getDescricao());
            stmt.setString(3, p.getTipo());
            stmt.setInt(4, p.getQuantidade());
            stmt.setDouble(5, p.getValor());
            stmt.setInt(6, p.getId());
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM produto WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Produto> buscarComFiltros(String nome, String tipo, String estoque) throws SQLException {
        List<Produto> lista = new ArrayList<>();
        String sql = "CALL sp_buscar_produtos(?, ?, ?)";

        // Normalização dos filtros
        String tipoFiltro = null;
        if (tipo != null && !tipo.trim().isEmpty()) {
            tipoFiltro = tipo.trim().toUpperCase();
            if (!tipoFiltro.equals("PRODUTO") && !tipoFiltro.equals("SERVICO")) {
                tipoFiltro = null; // valor inválido, ignora filtro
            }
        }
        String estoqueFiltro = null;
        if (estoque != null && !estoque.trim().isEmpty()) {
            estoqueFiltro = estoque.trim().toLowerCase();
            if (!estoqueFiltro.equals("disponivel") && !estoqueFiltro.equals("baixo") && !estoqueFiltro.equals("zerado")) {
                estoqueFiltro = null;
            }
        }
        String nomeFiltro = (nome != null && !nome.trim().isEmpty()) ? nome.trim() : null;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeFiltro);
            stmt.setString(2, tipoFiltro);
            stmt.setString(3, estoqueFiltro);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Produto p = new Produto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getString("tipo"),
                        rs.getInt("quantidade"),
                        rs.getDouble("valor")
                );
                lista.add(p);
            }
        }
        return lista;
    }
}
