package com.studiomuda.estoque.security.dominio;

/**
 * Perfil de acesso (E-11) — agregado de domínio puro. Identidade {@code int}
 * legado (auth/RBAC é caminho crítico de login com JDBC raw; migração p/ UUID
 * adiada deliberadamente para não quebrar o caminho crítico de autenticação.
 */
public class PerfilAcesso {

    private final int id;
    private final String nome;
    private final String descricao;
    private boolean ativo;

    public PerfilAcesso(int id, String nome, String descricao, boolean ativo) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do perfil não pode ser vazio.");
        }
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.ativo = ativo;
    }

    public void inativar() {
        this.ativo = false;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public boolean isAtivo() { return ativo; }
}
