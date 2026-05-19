package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.LogAcesso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LogAcessoDAO {

    public List<LogAcesso> listarRecentes(String resultado, int limite) throws SQLException {
        List<LogAcesso> logs = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM log_acesso WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (resultado != null && !resultado.trim().isEmpty()) {
            sql.append("AND resultado = ? ");
            params.add(resultado.trim().toUpperCase());
        }

        sql.append("ORDER BY data_hora DESC LIMIT ?");
        params.add(Math.max(1, limite));

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapear(rs));
                }
            }
        }
        return logs;
    }

    private LogAcesso mapear(ResultSet rs) throws SQLException {
        int usuarioId = rs.getInt("usuario_id");
        Integer usuarioIdObj = rs.wasNull() ? null : usuarioId;

        return new LogAcesso(
                rs.getInt("id"),
                usuarioIdObj,
                rs.getString("username"),
                rs.getString("recurso"),
                rs.getString("operacao"),
                rs.getString("resultado"),
                rs.getString("detalhe"),
                rs.getTimestamp("data_hora")
        );
    }
}
