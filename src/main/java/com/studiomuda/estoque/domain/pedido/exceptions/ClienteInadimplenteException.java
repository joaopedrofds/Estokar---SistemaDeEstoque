package com.studiomuda.estoque.domain.pedido.exceptions;

import com.studiomuda.estoque.domain.pedido.AnaliseInadimplencia;

public class ClienteInadimplenteException extends RuntimeException {
    private final AnaliseInadimplencia analise;

    public ClienteInadimplenteException(String message, AnaliseInadimplencia analise) {
        super(message);
        this.analise = analise;
    }

    public AnaliseInadimplencia analise() { return analise; }
}
