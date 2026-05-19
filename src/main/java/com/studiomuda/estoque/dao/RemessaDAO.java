package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.AgendamentoRemessa;
import com.studiomuda.estoque.model.CalendarioExcecao;
import com.studiomuda.estoque.model.Distribuidora;
import com.studiomuda.estoque.model.Doca;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RemessaDAO {

    public List<Doca> listarDocasAtivas() throws SQLException {
        List<Doca> docas = new ArrayList<>();
        String sql = "SELECT * FROM doca WHERE ativa = TRUE ORDER BY nome";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                docas.add(new Doca(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getInt("capacidade_paletes_diaria"),
                        rs.getBoolean("ativa")
                ));
            }
        }
        return docas;
    }

    public List<Distribuidora> listarDistribuidorasAtivas() throws SQLException {
        List<Distribuidora> distribuidoras = new ArrayList<>();
        String sql = "SELECT * FROM distribuidora WHERE ativa = TRUE ORDER BY nome";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                distribuidoras.add(new Distribuidora(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("nivel_prioridade"),
                        rs.getBoolean("ativa")
                ));
            }
        }
        return distribuidoras;
    }

    public Doca buscarDocaPorId(int id) throws SQLException {
        String sql = "SELECT * FROM doca WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Doca(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getInt("capacidade_paletes_diaria"),
                            rs.getBoolean("ativa")
                    );
                }
            }
        }
        return null;
    }

    public Distribuidora buscarDistribuidoraPorId(int id) throws SQLException {
        String sql = "SELECT * FROM distribuidora WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Distribuidora(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("nivel_prioridade"),
                            rs.getBoolean("ativa")
                    );
                }
            }
        }
        return null;
    }

    public void inserirDoca(Doca doca) throws SQLException {
        String sql = "INSERT INTO doca (nome, capacidade_paletes_diaria, ativa) VALUES (?, ?, TRUE)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, doca.getNome());
            stmt.setInt(2, doca.getCapacidadePaletesDiaria());
            stmt.executeUpdate();
        }
    }

    public void inserirDistribuidora(Distribuidora distribuidora) throws SQLException {
        String sql = "INSERT INTO distribuidora (nome, nivel_prioridade, ativa) VALUES (?, ?, TRUE)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, distribuidora.getNome());
            stmt.setString(2, distribuidora.getNivelPrioridade());
            stmt.executeUpdate();
        }
    }

    public void inserirExcecao(CalendarioExcecao excecao) throws SQLException {
        String sql = "INSERT INTO calendario_excecao (data, motivo, ativa) VALUES (?, ?, TRUE)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, excecao.getData());
            stmt.setString(2, excecao.getMotivo());
            stmt.executeUpdate();
        }
    }

    public boolean existeExcecaoAtiva(Date data) throws SQLException {
        String sql = "SELECT COUNT(*) FROM calendario_excecao WHERE data = ? AND ativa = TRUE";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, data);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public int somarVolumeConfirmado(int docaId, Date data) throws SQLException {
        String sql = "SELECT COALESCE(SUM(volume_paletes), 0) FROM agendamento_remessa " +
                "WHERE doca_id = ? AND data = ? AND status = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, docaId);
            stmt.setDate(2, data);
            stmt.setString(3, AgendamentoRemessa.STATUS_CONFIRMADO);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public boolean existeAgendamentoNoHorario(int docaId, Date data, String horario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM agendamento_remessa " +
                "WHERE doca_id = ? AND data = ? AND horario = ? AND status = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, docaId);
            stmt.setDate(2, data);
            stmt.setString(3, horario);
            stmt.setString(4, AgendamentoRemessa.STATUS_CONFIRMADO);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public void inserirAgendamento(AgendamentoRemessa agendamento) throws SQLException {
        String sql = "INSERT INTO agendamento_remessa (doca_id, distribuidora_id, data, horario, volume_paletes, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, agendamento.getDocaId());
            stmt.setInt(2, agendamento.getDistribuidoraId());
            stmt.setDate(3, agendamento.getData());
            stmt.setString(4, agendamento.getHorario());
            stmt.setInt(5, agendamento.getVolumePaletes());
            stmt.setString(6, AgendamentoRemessa.STATUS_CONFIRMADO);
            stmt.executeUpdate();
        }
    }

    public List<AgendamentoRemessa> listarAgendamentos() throws SQLException {
        List<AgendamentoRemessa> agendamentos = new ArrayList<>();
        String sql = "SELECT ar.*, d.nome AS doca_nome, di.nome AS distribuidora_nome, di.nivel_prioridade " +
                "FROM agendamento_remessa ar " +
                "JOIN doca d ON d.id = ar.doca_id " +
                "JOIN distribuidora di ON di.id = ar.distribuidora_id " +
                "ORDER BY ar.data DESC, ar.horario DESC, ar.id DESC";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                agendamentos.add(mapearAgendamento(rs));
            }
        }
        return agendamentos;
    }

    private AgendamentoRemessa mapearAgendamento(ResultSet rs) throws SQLException {
        AgendamentoRemessa agendamento = new AgendamentoRemessa();
        agendamento.setId(rs.getInt("id"));
        agendamento.setDocaId(rs.getInt("doca_id"));
        agendamento.setDistribuidoraId(rs.getInt("distribuidora_id"));
        agendamento.setData(rs.getDate("data"));
        agendamento.setHorario(rs.getString("horario"));
        agendamento.setVolumePaletes(rs.getInt("volume_paletes"));
        agendamento.setStatus(rs.getString("status"));
        agendamento.setDocaNome(rs.getString("doca_nome"));
        agendamento.setDistribuidoraNome(rs.getString("distribuidora_nome"));
        agendamento.setPrioridadeDistribuidora(rs.getString("nivel_prioridade"));
        return agendamento;
    }
}
