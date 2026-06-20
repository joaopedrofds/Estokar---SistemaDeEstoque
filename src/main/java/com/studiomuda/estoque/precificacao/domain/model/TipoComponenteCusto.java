package com.studiomuda.estoque.precificacao.domain.model;


public enum TipoComponenteCusto {
    CUSTO_COMPRA("Custo de compra"),
    IMPOSTO("Impostos"),
    DESPESA_OPERACIONAL("Despesas operacionais");

    private final String descricao;

    TipoComponenteCusto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
