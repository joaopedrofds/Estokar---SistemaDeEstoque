package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.IndicadorOperacional;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IndicadorOperacionalDAO {

    public List<IndicadorOperacional> listarTodos() throws SQLException {
        List<IndicadorOperacional> lista = new ArrayList<>();
        String sql = "SELECT * FROM indicador_operacional ORDER BY nome";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<IndicadorOperacional> listarAtivos() throws SQLException {
        List<IndicadorOperacional> lista = new ArrayList<>();
        String sql = "SELECT * FROM indicador_operacional WHERE ativo = TRUE ORDER BY nome";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public IndicadorOperacional buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM indicador_operacional WHERE id = ?";
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

    public IndicadorOperacional buscarPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT * FROM indicador_operacional WHERE codigo = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public void inserir(IndicadorOperacional ind) throws SQLException {
        String sql = "INSERT INTO indicador_operacional (codigo, nome, descricao, tipo_calculo, periodo_padrao, ativo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, ind.getCodigo().trim().toUpperCase());
            stmt.setString(2, ind.getNome().trim());
            stmt.setString(3, ind.getDescricao() != null ? ind.getDescricao().trim() : null);
            stmt.setString(4, ind.getTipoCalculo().trim().toUpperCase());
            stmt.setString(5, ind.getPeriodoPadrao().trim().toUpperCase());
            stmt.setBoolean(6, ind.isAtivo());
            stmt.executeUpdate();
            
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) {
                    ind.setId(gk.getInt(1));
                }
            }
        }
    }

    public void atualizar(IndicadorOperacional ind) throws SQLException {
        String sql = "UPDATE indicador_operacional SET codigo = ?, nome = ?, descricao = ?, tipo_calculo = ?, " +
                     "periodo_padrao = ?, ativo = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ind.getCodigo().trim().toUpperCase());
            stmt.setString(2, ind.getNome().trim());
            stmt.setString(3, ind.getDescricao() != null ? ind.getDescricao().trim() : null);
            stmt.setString(4, ind.getTipoCalculo().trim().toUpperCase());
            stmt.setString(5, ind.getPeriodoPadrao().trim().toUpperCase());
            stmt.setBoolean(6, ind.isAtivo());
            stmt.setInt(7, ind.getId());
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM indicador_operacional WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private IndicadorOperacional mapear(ResultSet rs) throws SQLException {
        return new IndicadorOperacional(
            rs.getInt("id"),
            rs.getString("codigo"),
            rs.getString("nome"),
            rs.getString("descricao"),
            rs.getString("tipo_calculo"),
            rs.getString("periodo_padrao"),
            rs.getBoolean("ativo")
        );
    }
}
