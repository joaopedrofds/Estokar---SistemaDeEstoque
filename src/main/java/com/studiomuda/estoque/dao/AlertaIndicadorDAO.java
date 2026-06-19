package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.AlertaIndicador;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AlertaIndicadorDAO {

    public List<AlertaIndicador> listarTodos() throws SQLException {
        List<AlertaIndicador> lista = new ArrayList<>();
        String sql = "SELECT a.*, i.nome AS indicador_nome, i.codigo AS indicador_codigo " +
                     "FROM alerta_indicador a " +
                     "JOIN indicador_operacional i ON a.indicador_id = i.id " +
                     "ORDER BY a.data_alerta DESC";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<AlertaIndicador> listarPorStatus(String status) throws SQLException {
        List<AlertaIndicador> lista = new ArrayList<>();
        String sql = "SELECT a.*, i.nome AS indicador_nome, i.codigo AS indicador_codigo " +
                     "FROM alerta_indicador a " +
                     "JOIN indicador_operacional i ON a.indicador_id = i.id " +
                     "WHERE a.status = ? " +
                     "ORDER BY a.data_alerta DESC";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.trim().toUpperCase());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public AlertaIndicador buscarPorId(int id) throws SQLException {
        String sql = "SELECT a.*, i.nome AS indicador_nome, i.codigo AS indicador_codigo " +
                     "FROM alerta_indicador a " +
                     "JOIN indicador_operacional i ON a.indicador_id = i.id " +
                     "WHERE a.id = ?";
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

    public AlertaIndicador buscarAtivoPorIndicador(int indicadorId) throws SQLException {
        String sql = "SELECT a.*, i.nome AS indicador_nome, i.codigo AS indicador_codigo " +
                     "FROM alerta_indicador a " +
                     "JOIN indicador_operacional i ON a.indicador_id = i.id " +
                     "WHERE a.indicador_id = ? AND a.status = 'ATIVO' " +
                     "ORDER BY a.data_alerta DESC LIMIT 1";
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

    public void inserir(AlertaIndicador alerta) throws SQLException {
        String sql = "INSERT INTO alerta_indicador (indicador_id, snapshot_id, tipo_violacao, valor_esperado, " +
                     "valor_encontrado, mensagem, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, alerta.getIndicadorId());
            stmt.setInt(2, alerta.getSnapshotId());
            stmt.setString(3, alerta.getTipoViolacao());
            stmt.setDouble(4, alerta.getValorEsperado());
            stmt.setDouble(5, alerta.getValorEncontrado());
            stmt.setString(6, alerta.getMensagem());
            stmt.setString(7, alerta.getStatus());
            stmt.executeUpdate();
            
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) {
                    alerta.setId(gk.getInt(1));
                }
            }
        }
    }

    public void atualizar(AlertaIndicador alerta) throws SQLException {
        String sql = "UPDATE alerta_indicador SET snapshot_id = ?, valor_encontrado = ?, mensagem = ?, " +
                     "status = ?, resolvido_por = ?, observacao = ?, data_resolucao = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, alerta.getSnapshotId());
            stmt.setDouble(2, alerta.getValorEncontrado());
            stmt.setString(3, alerta.getMensagem());
            stmt.setString(4, alerta.getStatus());
            stmt.setString(5, alerta.getResolvidoPor());
            stmt.setString(6, alerta.getObservacao());
            stmt.setTimestamp(7, alerta.getDataResolucao() != null ? Timestamp.valueOf(alerta.getDataResolucao()) : null);
            stmt.setInt(8, alerta.getId());
            stmt.executeUpdate();
        }
    }

    public void resolverAlerta(int id, String resolvidoPor, String observacao) throws SQLException {
        String sql = "UPDATE alerta_indicador SET status = 'RESOLVIDO', resolvido_por = ?, observacao = ?, " +
                     "data_resolucao = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, resolvidoPor);
            stmt.setString(2, observacao);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(4, id);
            stmt.executeUpdate();
        }
    }

    private AlertaIndicador mapear(ResultSet rs) throws SQLException {
        AlertaIndicador alerta = new AlertaIndicador();
        alerta.setId(rs.getInt("id"));
        alerta.setIndicadorId(rs.getInt("indicador_id"));
        alerta.setSnapshotId(rs.getInt("snapshot_id"));
        alerta.setTipoViolacao(rs.getString("tipo_violacao"));
        alerta.setValorEsperado(rs.getDouble("valor_esperado"));
        alerta.setValorEncontrado(rs.getDouble("valor_encontrado"));
        alerta.setMensagem(rs.getString("mensagem"));
        alerta.setStatus(rs.getString("status"));
        alerta.setResolvidoPor(rs.getString("resolvido_por"));
        alerta.setObservacao(rs.getString("observacao"));
        alerta.setDataAlerta(rs.getTimestamp("data_alerta").toLocalDateTime());
        
        Timestamp dr = rs.getTimestamp("data_resolucao");
        alerta.setDataResolucao(dr != null ? dr.toLocalDateTime() : null);
        
        alerta.setIndicadorNome(rs.getString("indicador_nome"));
        alerta.setIndicadorCodigo(rs.getString("indicador_codigo"));
        return alerta;
    }
}
