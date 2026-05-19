package com.studiomuda.estoque.model;

public class PermissaoPerfil {
    private int id;
    private int perfilId;
    private String recurso;
    private String operacao;
    private boolean permitido;

    public PermissaoPerfil() {
    }

    public PermissaoPerfil(int id, int perfilId, String recurso, String operacao, boolean permitido) {
        this.id = id;
        this.perfilId = perfilId;
        this.recurso = recurso;
        this.operacao = operacao;
        this.permitido = permitido;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPerfilId() {
        return perfilId;
    }

    public void setPerfilId(int perfilId) {
        this.perfilId = perfilId;
    }

    public String getRecurso() {
        return recurso;
    }

    public void setRecurso(String recurso) {
        this.recurso = recurso;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public boolean isPermitido() {
        return permitido;
    }

    public void setPermitido(boolean permitido) {
        this.permitido = permitido;
    }
}
