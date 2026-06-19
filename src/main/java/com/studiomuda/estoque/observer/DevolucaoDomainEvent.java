package com.studiomuda.estoque.observer;

import com.studiomuda.estoque.model.Devolucao;

/**
 * Value Object imutável — evento de aprovação de devolução.
 * Nível Tático DDD: Domain Event
 */
public class DevolucaoDomainEvent {
    private final Devolucao devolucao;

    public DevolucaoDomainEvent(Devolucao devolucao) {
        this.devolucao = devolucao;
    }

    public Devolucao getDevolucao() { return devolucao; }
}