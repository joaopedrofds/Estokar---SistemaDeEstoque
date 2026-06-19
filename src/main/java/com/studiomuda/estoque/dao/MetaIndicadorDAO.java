package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.MetaIndicador;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MetaIndicadorDAO {

    public List<MetaIndicador> listarTodas() throws SQLException {
        List<MetaIndicador> lista = new ArrayList<>();
        String sql = "SELECT m.*, i.nome AS indicador_nome, i.codigo AS indicador_codigo " +
                     "FROM meta_indicador m " +
                     "JOIN indicador_operacional i ON m.indicador_id = i.id " +
                     "ORDER BY i.nome, m.vigencia_inicio DESC";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public MetaIndicador buscarPorId(int id) throws SQLException {
        String sql = "SELECT m.*, i.nome AS indicador_nome, i.codigo AS indicador_codigo " +
                     "FROM meta_indicador m " +
                     "JOIN indicador_operacional i ON m.indicador_id = i.id " +
                     "WHERE m.id = ?";
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

    public MetaIndicador buscarAtivaPorIndicador(int indicadorId) throws SQLException {
        String sql = "SELECT m.*, i.nome AS indicador_nome, i.codigo AS indicador_codigo " +
                     "FROM meta_indicador m " +
                     "JOIN indicador_operacional i ON m.indicador_id = i.id " +
                     "WHERE m.indicador_id = ? AND m.ativo = TRUE " +
                     "AND m.vigencia_inicio <= CURDATE() AND (m.vigencia_fim IS NULL OR m.vigencia_fim >= CURDATE()) " +
                     "ORDER BY m.vigencia_inicio DESC LIMIT 1";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, indicadorId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public void inserir(MetaIndicador meta) throws SQLException {
        String sql = "INSERT INTO meta_indicador (indicador_id, valor_alvo, limite_critico, operador, vigencia_inicio, vigencia_fim, ativo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, meta.getIndicadorId());
            stmt.setDouble(2, meta.getValorAlvo());
            stmt.setDouble(3, meta.getLimiteCritico());
            stmt.setString(4, meta.getOperador().trim().toUpperCase());
            stmt.setDate(5, Date.valueOf(meta.getVigenciaInicio()));
            stmt.setDate(6, meta.getVigenciaFim() != null ? Date.valueOf(meta.getVigenciaFim()) : null);
            stmt.setBoolean(7, meta.isAtivo());
            stmt.executeUpdate();
            
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) {
                    meta.setId(gk.getInt(1));
                }
            }
        }
    }

    public void atualizar(MetaIndicador meta) throws SQLException {
        String sql = "UPDATE meta_indicador SET indicador_id = ?, valor_alvo = ?, limite_critico = ?, operador = ?, " +
                     "vigencia_inicio = ?, vigencia_fim = ?, ativo = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, meta.getIndicadorId());
            stmt.setDouble(2, meta.getValorAlvo());
            stmt.setDouble(3, meta.getLimiteCritico());
            stmt.setString(4, meta.getOperador().trim().toUpperCase());
            stmt.setDate(5, Date.valueOf(meta.getVigenciaInicio()));
            stmt.setDate(6, meta.getVigenciaFim() != null ? Date.valueOf(meta.getVigenciaFim()) : null);
            stmt.setBoolean(7, meta.isAtivo());
            stmt.setInt(8, meta.getId());
            stmt.executeUpdate();
        }
    }

    public void desativarOutrasMetas(int indicadorId, int metaAtivaId) throws SQLException {
        String sql = "UPDATE meta_indicador SET ativo = FALSE WHERE indicador_id = ? AND id <> ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, indicadorId);
            stmt.setInt(2, metaAtivaId);
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM meta_indicador WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private MetaIndicador mapear(ResultSet rs) throws SQLException {
        Date vf = rs.getDate("vigencia_fim");
        LocalDate vigenciaFim = vf != null ? vf.toLocalDate() : null;
        
        MetaIndicador meta = new MetaIndicador(
            rs.getInt("id"),
            rs.getInt("indicador_id"),
            rs.getDouble("valor_alvo"),
            rs.getDouble("limite_critico"),
            rs.getString("operador"),
            rs.getDate("vigencia_inicio").toLocalDate(),
            vigenciaFim,
            rs.getBoolean("ativo")
        );
        meta.setIndicadorNome(rs.getString("indicador_nome"));
        meta.setIndicadorCodigo(rs.getString("indicador_codigo"));
        return meta;
    }
}
