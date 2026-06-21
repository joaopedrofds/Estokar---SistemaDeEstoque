package com.studiomuda.estoque.security.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Usuário de acesso (E-11) — agregado de domínio puro. A identidade
 * {@code int} legada é preservada porque o usuário é o hub do login/RBAC e de
 * várias FKs entre contextos.
 *
 * <p>Os nomes dos perfis são dados de apresentação e não pertencem ao
 * agregado; somente os respectivos IDs são mantidos aqui.</p>
 */
public class UsuarioAcesso {

    private final int id;
    private final String username;
    private final String nome;
    private final String senha;
    private boolean ativo;
    private final List<Integer> perfilIds;

    public UsuarioAcesso(int id, String username, String nome, String senha,
                         boolean ativo, List<Integer> perfilIds) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Login do usuário não pode ser vazio.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do usuário não pode ser vazio.");
        }
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Senha do usuário não pode ser vazia.");
        }
        this.id = id;
        this.username = username;
        this.nome = nome;
        this.senha = senha;
        this.ativo = ativo;

        LinkedHashSet<Integer> idsUnicos = new LinkedHashSet<>();
        if (perfilIds != null) {
            for (Integer perfilId : perfilIds) {
                if (perfilId != null) {
                    idsUnicos.add(perfilId);
                }
            }
        }
        this.perfilIds = Collections.unmodifiableList(new ArrayList<>(idsUnicos));
    }

    public void inativar() {
        this.ativo = false;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getNome() { return nome; }
    public String getSenha() { return senha; }
    public boolean isAtivo() { return ativo; }
    public List<Integer> getPerfilIds() { return perfilIds; }
}
