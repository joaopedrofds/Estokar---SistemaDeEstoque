package com.studiomuda.estoque.security;

import com.studiomuda.estoque.conexao.Conexao;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String sqlUsuario = "SELECT id, username, nome, senha, ativo FROM usuario_acesso WHERE username = ?";
        try (Connection conn = Conexao.getConnectionSemAutorizacao();
             PreparedStatement stmt = conn.prepareStatement(sqlUsuario)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new UsernameNotFoundException("Usuário não encontrado.");
                }

                int id = rs.getInt("id");
                String login = rs.getString("username");
                String nome = rs.getString("nome");
                String senha = rs.getString("senha");
                boolean ativo = rs.getBoolean("ativo");

                PerfilCarregado perfilCarregado = carregarPerfis(conn, id);
                return new UsuarioAutenticado(
                        id,
                        login,
                        nome,
                        senha,
                        ativo,
                        perfilCarregado.perfilIds,
                        perfilCarregado.authorities
                );
            }
        } catch (SQLException e) {
            throw new UsernameNotFoundException("Falha ao carregar usuário.");
        }
    }

    private PerfilCarregado carregarPerfis(Connection conn, int usuarioId) throws SQLException {
        String sqlPerfis = "SELECT pa.id, pa.nome " +
                "FROM usuario_perfil up " +
                "JOIN perfil_acesso pa ON pa.id = up.perfil_id " +
                "WHERE up.usuario_id = ? AND pa.ativo = TRUE";

        List<Integer> perfilIds = new ArrayList<>();
        List<GrantedAuthority> authorities = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sqlPerfis)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int perfilId = rs.getInt("id");
                    String nomePerfil = rs.getString("nome");
                    perfilIds.add(perfilId);
                    authorities.add(new SimpleGrantedAuthority(NormalizadorAutoridade.paraRole(nomePerfil)));
                }
            }
        }

        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_SEM_PERFIL"));
        }

        return new PerfilCarregado(perfilIds, authorities);
    }

    private static class PerfilCarregado {
        private final List<Integer> perfilIds;
        private final List<GrantedAuthority> authorities;

        private PerfilCarregado(List<Integer> perfilIds, List<GrantedAuthority> authorities) {
            this.perfilIds = perfilIds;
            this.authorities = authorities;
        }
    }
}
