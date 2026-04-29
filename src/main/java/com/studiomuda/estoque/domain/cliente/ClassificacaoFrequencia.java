package com.studiomuda.estoque.domain.cliente;

public enum ClassificacaoFrequencia {
    SEM_COMPRAS("Sem Compras"),
    VIP("Cliente VIP"),
    REGULAR("Regular"),
    EM_RISCO("Em Risco/Inativo");

    private final String descricao;

    ClassificacaoFrequencia(String descricao) {
        this.descricao = descricao;
    }

    public String descricao() {
        return descricao;
    }

    public static ClassificacaoFrequencia desdeMediaDias(double mediaDias) {
        if (mediaDias < 15) return VIP;
        if (mediaDias <= 30) return REGULAR;
        return EM_RISCO;
    }
}
