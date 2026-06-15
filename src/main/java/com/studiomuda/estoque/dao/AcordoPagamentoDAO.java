package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.AcordoPagamento;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AcordoPagamentoDAO {
    public AcordoPagamento buscarAtivoValidoPorCliente(int clienteId, LocalDate dataReferencia) throws SQLException {
        String sql = "SELECT id, cliente_id, status, data_inicio, data_fim FROM acordo_pagamento " +
                "WHERE cliente_id = ? AND UPPER(status) = 'EM_ACORDO' " +
                "AND data_inicio <= ? AND (data_fim IS NULL OR data_fim >= ?) ORDER BY id DESC LIMIT 1";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            stmt.setDate(2, Date.valueOf(dataReferencia));
            stmt.setDate(3, Date.valueOf(dataReferencia));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            if (!isTabelaInexistente(e)) {
                throw e;
            }
        }
        return null;
    }

    public boolean possuiParcelaAtrasada(int acordoId, LocalDate dataReferencia) throws SQLException {
        String sql = "SELECT 1 FROM parcela_acordo WHERE acordo_id = ? AND data_vencimento < ? " +
                "AND data_pagamento IS NULL AND UPPER(status) <> 'PAGA' LIMIT 1";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, acordoId);
            stmt.setDate(2, Date.valueOf(dataReferencia));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            if (!isTabelaInexistente(e)) {
                throw e;
            }
        }
        return false;
    }

    public void registrarPerdaProtecao(int acordoId) throws SQLException {
        String sql = "UPDATE acordo_pagamento SET status = 'PERDEU_PROTECAO', data_fim = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setInt(2, acordoId);
            stmt.executeUpdate();
        }
    }

    private AcordoPagamento mapear(ResultSet rs) throws SQLException {
        AcordoPagamento acordo = new AcordoPagamento();
        acordo.setId(rs.getInt("id"));
        acordo.setClienteId(rs.getInt("cliente_id"));
        acordo.setStatus(rs.getString("status"));
        Date dataInicio = rs.getDate("data_inicio");
        Date dataFim = rs.getDate("data_fim");
        acordo.setDataInicio(dataInicio != null ? dataInicio.toLocalDate() : null);
        acordo.setDataFim(dataFim != null ? dataFim.toLocalDate() : null);
        return acordo;
    }

    private boolean isTabelaInexistente(SQLException e) {
        return "42S02".equals(e.getSQLState())
                || (e.getMessage() != null && e.getMessage().toLowerCase().contains("doesn't exist"));
    }
}
