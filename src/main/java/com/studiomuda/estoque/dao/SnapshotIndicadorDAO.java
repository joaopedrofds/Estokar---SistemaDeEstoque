package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.SnapshotIndicador;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SnapshotIndicadorDAO {

    public List<SnapshotIndicador> listarTodos() throws SQLException {
        List<SnapshotIndicador> lista = new ArrayList<>();
        String sql = "SELECT s.*, i.nome AS indicador_nome, i.codigo AS indicador_codigo " +
                     "FROM snapshot_indicador s " +
                     "JOIN indicador_operacional i ON s.indicador_id = i.id " +
                     "ORDER BY s.data_execucao DESC";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<SnapshotIndicador> listarPorIndicador(int indicadorId) throws SQLException {
        List<SnapshotIndicador> lista = new ArrayList<>();
        String sql = "SELECT s.*, i.nome AS indicador_nome, i.codigo AS indicador_codigo " +
                     "FROM snapshot_indicador s " +
                     "JOIN indicador_operacional i ON s.indicador_id = i.id " +
                     "WHERE s.indicador_id = ? " +
                     "ORDER BY s.data_execucao DESC";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, indicadorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public SnapshotIndicador buscarPorId(int id) throws SQLException {
        String sql = "SELECT s.*, i.nome AS indicador_nome, i.codigo AS indicador_codigo " +
                     "FROM snapshot_indicador s " +
                     "JOIN indicador_operacional i ON s.indicador_id = i.id " +
                     "WHERE s.id = ?";
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

    public SnapshotIndicador buscarUltimoPorIndicador(int indicadorId) throws SQLException {
        String sql = "SELECT s.*, i.nome AS indicador_nome, i.codigo AS indicador_codigo " +
                     "FROM snapshot_indicador s " +
                     "JOIN indicador_operacional i ON s.indicador_id = i.id " +
                     "WHERE s.indicador_id = ? " +
                     "ORDER BY s.data_execucao DESC LIMIT 1";
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

    public void inserir(SnapshotIndicador snap) throws SQLException {
        String sql = "INSERT INTO snapshot_indicador (indicador_id, valor_calculado, periodo_inicio, periodo_fim, " +
                     "executado_por_id, executado_por, detalhe_rastreio) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, snap.getIndicadorId());
            stmt.setDouble(2, snap.getValorCalculado());
            stmt.setDate(3, Date.valueOf(snap.getPeriodoInicio()));
            stmt.setDate(4, Date.valueOf(snap.getPeriodoFim()));
            if (snap.getExecutadoPorId() != null) {
                stmt.setInt(5, snap.getExecutadoPorId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            stmt.setString(6, snap.getExecutadoPor());
            stmt.setString(7, snap.getDetalheRastreio());
            stmt.executeUpdate();
            
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) {
                    snap.setId(gk.getInt(1));
                }
            }
        }
    }

    private SnapshotIndicador mapear(ResultSet rs) throws SQLException {
        SnapshotIndicador snap = new SnapshotIndicador();
        snap.setId(rs.getInt("id"));
        snap.setIndicadorId(rs.getInt("indicador_id"));
        snap.setValorCalculado(rs.getDouble("valor_calculado"));
        snap.setPeriodoInicio(rs.getDate("periodo_inicio").toLocalDate());
        snap.setPeriodoFim(rs.getDate("periodo_fim").toLocalDate());
        
        int executadoPorId = rs.getInt("executado_por_id");
        snap.setExecutadoPorId(rs.wasNull() ? null : executadoPorId);
        snap.setExecutadoPor(rs.getString("executado_por"));
        snap.setDataExecucao(rs.getTimestamp("data_execucao").toLocalDateTime());
        snap.setDetalheRastreio(rs.getString("detalhe_rastreio"));
        
        snap.setIndicadorNome(rs.getString("indicador_nome"));
        snap.setIndicadorCodigo(rs.getString("indicador_codigo"));
        return snap;
    }
}
