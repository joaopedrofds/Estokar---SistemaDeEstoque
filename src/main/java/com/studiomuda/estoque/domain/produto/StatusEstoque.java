package com.studiomuda.estoque.domain.produto;

public enum StatusEstoque {
    ZERADO("zerado"),
    BAIXO("baixo"),
    DISPONIVEL("disponivel");

    private static final int LIMITE_BAIXO = 5;

    private final String codigo;

    StatusEstoque(String codigo) {
        this.codigo = codigo;
    }

    public String codigo() {
        return codigo;
    }

    public static StatusEstoque desdeQuantidade(int quantidade) {
        if (quantidade <= 0) return ZERADO;
        if (quantidade <= LIMITE_BAIXO) return BAIXO;
        return DISPONIVEL;
    }

    public static StatusEstoque fromCodigo(String codigo) {
        if (codigo == null) return null;
        for (StatusEstoque s : values()) {
            if (s.codigo.equalsIgnoreCase(codigo.trim())) return s;
        }
        return null;
    }
}
