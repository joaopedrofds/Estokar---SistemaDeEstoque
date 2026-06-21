package com.studiomuda.estoque.security.dominio;

/**
 * Permissão de um perfil sobre um recurso/operação (E-11) — entrada da matriz RBAC.
 * Agregado de domínio puro; id {@code int} legado (RBAC/login crítico, ver memória).
 * {@code recurso}/{@code operacao} ficam como {@code String} para tolerar valores
 * legados de seed fora dos enums (ex.: {@code INVENTARIO}).
 */
public class PermissaoPerfil {

    private final int id;
    private final int perfilId;
    private final String recurso;
    private final String operacao;
    private final boolean permitido;

    public PermissaoPerfil(int id, int perfilId, String recurso, String operacao, boolean permitido) {
        if (recurso == null || recurso.isBlank()) {
            throw new IllegalArgumentException("Recurso da permissão não pode ser vazio.");
        }
        if (operacao == null || operacao.isBlank()) {
            throw new IllegalArgumentException("Operação da permissão não pode ser vazia.");
        }
        this.id = id;
        this.perfilId = perfilId;
        this.recurso = recurso;
        this.operacao = operacao;
        this.permitido = permitido;
    }

    public int getId() { return id; }
    public int getPerfilId() { return perfilId; }
    public String getRecurso() { return recurso; }
    public String getOperacao() { return operacao; }
    public boolean isPermitido() { return permitido; }
}
