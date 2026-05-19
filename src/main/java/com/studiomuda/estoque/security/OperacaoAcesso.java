package com.studiomuda.estoque.security;

public enum OperacaoAcesso {
    LEITURA("Leitura"),
    ESCRITA("Escrita"),
    APROVACAO("Aprovação");

    private final String descricao;

    OperacaoAcesso(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
