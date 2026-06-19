package com.studiomuda.estoque.observer;

/**
 * Interface Observer para eventos de aprovação de devolução.
 * Padrão de Design: Observer (GoF) — Observer interface
 */
public interface ObservadorDeDevolucao {
    void aoAprovarDevolucao(DevolucaoDomainEvent evento);
}