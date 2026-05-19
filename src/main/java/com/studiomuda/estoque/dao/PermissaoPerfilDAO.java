package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.PermissaoPerfil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PermissaoPerfilDAO {

    public List<PermissaoPerfil> listarPorPerfil(int perfilId) throws SQLException {
        List<PermissaoPerfil> permissoes = new ArrayList<>();
        String sql = "SELECT * FROM permissao_perfil WHERE perfil_id = ? ORDER BY recurso, operacao";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, perfilId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permissoes.add(mapear(rs));
                }
            }
        }
        return permissoes;
    }

    public Map<String, Set<String>> carregarMapaPermissoes(int perfilId) throws SQLException {
        Map<String, Set<String>> mapa = new HashMap<>();
        for (PermissaoPerfil permissao : listarPorPerfil(perfilId)) {
            if (!permissao.isPermitido()) {
                continue;
            }
            mapa.computeIfAbsent(permissao.getRecurso(), chave -> new HashSet<>())
                    .add(permissao.getOperacao());
        }
        return mapa;
    }

    public void substituirPermissoesPerfil(int perfilId, List<PermissaoPerfil> permissoes) throws SQLException {
        String sqlDelete = "DELETE FROM permissao_perfil WHERE perfil_id = ?";
        String sqlInsert = "INSERT INTO permissao_perfil (perfil_id, recurso, operacao, permitido) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete);
                 PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {

                stmtDelete.setInt(1, perfilId);
                stmtDelete.executeUpdate();

                for (PermissaoPerfil permissao : permissoes) {
                    stmtInsert.setInt(1, perfilId);
                    stmtInsert.setString(2, permissao.getRecurso());
                    stmtInsert.setString(3, permissao.getOperacao());
                    stmtInsert.setBoolean(4, permissao.isPermitido());
                    stmtInsert.addBatch();
                }
                stmtInsert.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private PermissaoPerfil mapear(ResultSet rs) throws SQLException {
        return new PermissaoPerfil(
                rs.getInt("id"),
                rs.getInt("perfil_id"),
                rs.getString("recurso"),
                rs.getString("operacao"),
                rs.getBoolean("permitido")
        );
    }
}
