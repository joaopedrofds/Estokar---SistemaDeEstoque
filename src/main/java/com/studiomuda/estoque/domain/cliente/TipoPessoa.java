package com.studiomuda.estoque.domain.cliente;

public enum TipoPessoa {
    PF(11, "CPF"),
    PJ(14, "CNPJ");

    private final int tamanhoDocumento;
    private final String rotulo;

    TipoPessoa(int tamanhoDocumento, String rotulo) {
        this.tamanhoDocumento = tamanhoDocumento;
        this.rotulo = rotulo;
    }

    public int tamanhoDocumento() {
        return tamanhoDocumento;
    }

    public String rotulo() {
        return rotulo;
    }

    public static TipoPessoa fromCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return PF;
        }
        return TipoPessoa.valueOf(codigo.trim().toUpperCase());
    }
}
