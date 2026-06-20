package com.studiomuda.estoque.precificacao.domain.model;


public enum StatusPrecificacao {
    APROVADO("Aprovado"),
    BLOQUEADO_MARGEM("Bloqueado por margem"),
    BLOQUEADO_DESCONTO("Bloqueado por desconto"),
    APLICADO("Aplicado");

    private final String descricao;

    StatusPrecificacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean permiteAplicacao() {
        return this == APROVADO || this == APLICADO;
    }

    public boolean isBloqueado() {
        return this == BLOQUEADO_MARGEM || this == BLOQUEADO_DESCONTO;
    }
}
