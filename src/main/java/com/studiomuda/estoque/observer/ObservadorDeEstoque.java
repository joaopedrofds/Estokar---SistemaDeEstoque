package com.studiomuda.estoque.observer;

public interface ObservadorDeEstoque {
    void aoAlterarEstoque(EstoqueAlteradoDomainEvent evento);
}
