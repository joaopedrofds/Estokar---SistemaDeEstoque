package com.studiomuda.estoque.dao;
import org.springframework.stereotype.Repository;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.UsuarioAcesso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;



@Repository
public class UsuarioAcessoDAO {

    public void inserir(UsuarioAcesso usuario) throws SQLException {
        String sql = "INSERT INTO usuario_acesso (username, nome, senha, ativo) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getNome());
            stmt.setString(3, usuario.getSenha());
            stmt.setBoolean(4, usuario.isAtivo());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
            }
        }
        atualizarPerfisUsuario(usuario.getId(), usuario.getPerfilIds());
    }

    public void atualizar(UsuarioAcesso usuario, boolean atualizarSenha) throws SQLException {
        String sqlComSenha = "UPDATE usuario_acesso SET username = ?, nome = ?, senha = ?, ativo = ? WHERE id = ?";
        String sqlSemSenha = "UPDATE usuario_acesso SET username = ?, nome = ?, ativo = ? WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(atualizarSenha ? sqlComSenha : sqlSemSenha)) {
            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getNome());
            if (atualizarSenha) {
                stmt.setString(3, usuario.getSenha());
                stmt.setBoolean(4, usuario.isAtivo());
                stmt.setInt(5, usuario.getId());
            } else {
                stmt.setBoolean(3, usuario.isAtivo());
                stmt.setInt(4, usuario.getId());
            }
            stmt.executeUpdate();
        }
        atualizarPerfisUsuario(usuario.getId(), usuario.getPerfilIds());
    }

    public UsuarioAcesso buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuario_acesso WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UsuarioAcesso usuario = mapear(rs);
                    carregarPerfisUsuario(conn, usuario);
                    return usuario;
                }
            }
        }
        return null;
    }

    public UsuarioAcesso buscarPorUsername(String username) throws SQLException {
        String sql = "SELECT * FROM usuario_acesso WHERE username = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UsuarioAcesso usuario = mapear(rs);
                    carregarPerfisUsuario(conn, usuario);
                    return usuario;
                }
            }
        }
        return null;
    }

    public List<UsuarioAcesso> listarTodos() throws SQLException {
        List<UsuarioAcesso> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario_acesso ORDER BY ativo DESC, username ASC";
        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                UsuarioAcesso usuario = mapear(rs);
                carregarPerfisUsuario(conn, usuario);
                usuarios.add(usuario);
            }
        }
        return usuarios;
    }

    public void atualizarPerfisUsuario(int usuarioId, List<Integer> perfilIds) throws SQLException {
        String sqlDelete = "DELETE FROM usuario_perfil WHERE usuario_id = ?";
        String sqlInsert = "INSERT INTO usuario_perfil (usuario_id, perfil_id) VALUES (?, ?)";

        try (Connection conn = Conexao.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete);
                 PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {
                stmtDelete.setInt(1, usuarioId);
                stmtDelete.executeUpdate();

                if (perfilIds != null) {
                    for (Integer perfilId : perfilIds) {
                        stmtInsert.setInt(1, usuarioId);
                        stmtInsert.setInt(2, perfilId);
                        stmtInsert.addBatch();
                    }
                    stmtInsert.executeBatch();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void inativar(int id) throws SQLException {
        String sql = "UPDATE usuario_acesso SET ativo = FALSE WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private void carregarPerfisUsuario(Connection conn, UsuarioAcesso usuario) throws SQLException {
        String sql = "SELECT pa.id, pa.nome " +
                "FROM usuario_perfil up " +
                "JOIN perfil_acesso pa ON pa.id = up.perfil_id " +
                "WHERE up.usuario_id = ? " +
                "ORDER BY pa.nome";
        List<Integer> perfilIds = new ArrayList<>();
        List<String> perfilNomes = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuario.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    perfilIds.add(rs.getInt("id"));
                    perfilNomes.add(rs.getString("nome"));
                }
            }
        }
        usuario.setPerfilIds(perfilIds);
        usuario.setPerfilNomes(perfilNomes);
    }

    private UsuarioAcesso mapear(ResultSet rs) throws SQLException {
        return new UsuarioAcesso(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("nome"),
                rs.getString("senha"),
                rs.getBoolean("ativo")
        );
    }
}
