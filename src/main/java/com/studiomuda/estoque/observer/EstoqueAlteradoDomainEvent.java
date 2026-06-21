package com.studiomuda.estoque.observer;

import com.studiomuda.estoque.model.ParametroEstoque;

public class EstoqueAlteradoDomainEvent {

    private final ParametroEstoque parametro;

    public EstoqueAlteradoDomainEvent(ParametroEstoque parametro) {
        this.parametro = parametro;
    }

    public ParametroEstoque getParametro() {
        return parametro;
    }
}
