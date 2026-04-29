package com.studiomuda.estoque.domain.estoque;

public enum TipoMovimentacao {
    ENTRADA("entrada"),
    SAIDA("saida");

    private final String codigo;

    TipoMovimentacao(String codigo) {
        this.codigo = codigo;
    }

    public String codigo() {
        return codigo;
    }

    public int sinalEstoque() {
        return this == ENTRADA ? 1 : -1;
    }

    public static TipoMovimentacao fromCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("O tipo da movimentação é obrigatório.");
        }
        for (TipoMovimentacao t : values()) {
            if (t.codigo.equalsIgnoreCase(codigo.trim()) || t.name().equalsIgnoreCase(codigo.trim())) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de movimentação inválido: " + codigo);
    }
}
