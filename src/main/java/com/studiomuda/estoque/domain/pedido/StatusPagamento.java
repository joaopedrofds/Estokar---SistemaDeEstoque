package com.studiomuda.estoque.domain.pedido;

public enum StatusPagamento {
    PENDENTE,
    PAGO;

    public static StatusPagamento fromCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) return PENDENTE;
        if (PAGO.name().equalsIgnoreCase(codigo.trim())) return PAGO;
        return PENDENTE;
    }
}
