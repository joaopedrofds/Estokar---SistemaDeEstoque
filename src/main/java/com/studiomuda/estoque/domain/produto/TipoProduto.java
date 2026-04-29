package com.studiomuda.estoque.domain.produto;

public enum TipoProduto {
    PRODUTO,
    SERVICO;

    public static TipoProduto fromCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("O tipo é obrigatório.");
        }
        return TipoProduto.valueOf(codigo.trim().toUpperCase());
    }
}
