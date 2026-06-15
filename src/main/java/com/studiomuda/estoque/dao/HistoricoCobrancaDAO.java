package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.HistoricoCobranca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class HistoricoCobrancaDAO {
    public void inserir(HistoricoCobranca historico) throws SQLException {
        String sql = "INSERT INTO historico_cobranca " +
                "(cliente_id, fatura_id, acordo_id, registro_original_id, tipo, descricao, usuario) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(stmt, historico);
            stmt.executeUpdate();
            try (java.sql.ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    historico.setId(rs.getInt(1));
                }
            }
        }
    }

    public void corrigirRegistro(int registroOriginalId, HistoricoCobranca correcao) throws SQLException {
        correcao.setRegistroOriginalId(registroOriginalId);
        correcao.setTipo("CORRECAO_DE_REGISTRO");
        inserir(correcao);
    }

    public void atualizar(HistoricoCobranca historico) {
        throw new UnsupportedOperationException("Historico de cobranca e imutavel; crie uma correcao de registro.");
    }

    public void deletar(int id) {
        throw new UnsupportedOperationException("Historico de cobranca e imutavel; exclusao nao permitida.");
    }

    private void preencher(PreparedStatement stmt, HistoricoCobranca historico) throws SQLException {
        stmt.setInt(1, historico.getClienteId());
        setInteger(stmt, 2, historico.getFaturaId());
        setInteger(stmt, 3, historico.getAcordoId());
        setInteger(stmt, 4, historico.getRegistroOriginalId());
        stmt.setString(5, historico.getTipo());
        stmt.setString(6, historico.getDescricao());
        stmt.setString(7, historico.getUsuario());
    }

    private void setInteger(PreparedStatement stmt, int index, Integer value) throws SQLException {
        if (value != null && value > 0) {
            stmt.setInt(index, value);
        } else {
            stmt.setNull(index, Types.INTEGER);
        }
    }
}
