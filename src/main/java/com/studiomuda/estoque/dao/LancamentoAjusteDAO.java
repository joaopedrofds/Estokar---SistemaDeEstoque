package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.LancamentoAjuste;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LancamentoAjusteDAO {

    public void inserir(LancamentoAjuste ajuste) throws SQLException {
        String sql = "INSERT INTO lancamento_ajuste (categoria_id, data_lancamento, valor, descricao, usuario_id, username) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, ajuste.getCategoriaId());
            stmt.setDate(2, new Date(ajuste.getDataLancamento().getTime()));
            stmt.setDouble(3, ajuste.getValor());
            stmt.setString(4, ajuste.getDescricao());
            if (ajuste.getUsuarioId() != null && ajuste.getUsuarioId() > 0) {
                stmt.setInt(5, ajuste.getUsuarioId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            stmt.setString(6, ajuste.getUsername());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    ajuste.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<LancamentoAjuste> listarTodos() throws SQLException {
        List<LancamentoAjuste> lista = new ArrayList<>();
        String sql = "SELECT la.*, cf.nome AS categoria_nome, cf.tipo AS categoria_tipo " +
                "FROM lancamento_ajuste la " +
                "JOIN categoria_financeira cf ON cf.id = la.categoria_id " +
                "ORDER BY la.data_lancamento DESC, la.id DESC";
        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public double somarPorCategoria(int categoriaId, Date inicio, Date fim) throws SQLException {
        String sql = "SELECT COALESCE(SUM(valor), 0) FROM lancamento_ajuste WHERE categoria_id = ? AND data_lancamento BETWEEN ? AND ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoriaId);
            stmt.setDate(2, inicio);
            stmt.setDate(3, fim);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0;
    }

    public double somarAjustesPorTipo(String tipoCategoria, Date inicio, Date fim) throws SQLException {
        String sql = "SELECT COALESCE(SUM(la.valor), 0) " +
                "FROM lancamento_ajuste la " +
                "JOIN categoria_financeira cf ON cf.id = la.categoria_id " +
                "WHERE cf.tipo = ? AND la.data_lancamento BETWEEN ? AND ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tipoCategoria);
            stmt.setDate(2, inicio);
            stmt.setDate(3, fim);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0;
    }

    private LancamentoAjuste mapear(ResultSet rs) throws SQLException {
        LancamentoAjuste ajuste = new LancamentoAjuste();
        ajuste.setId(rs.getInt("id"));
        ajuste.setCategoriaId(rs.getInt("categoria_id"));
        ajuste.setCategoriaNome(rs.getString("categoria_nome"));
        ajuste.setCategoriaTipo(rs.getString("categoria_tipo"));
        ajuste.setDataLancamento(rs.getDate("data_lancamento"));
        ajuste.setValor(rs.getDouble("valor"));
        ajuste.setDescricao(rs.getString("descricao"));
        int usuarioId = rs.getInt("usuario_id");
        if (!rs.wasNull()) {
            ajuste.setUsuarioId(usuarioId);
        }
        ajuste.setUsername(rs.getString("username"));
        ajuste.setCriadoEm(rs.getTimestamp("criado_em"));
        return ajuste;
    }
}
