package com.studiomuda.estoque.observer;

/**
 * Interface Observer — contrato para ouvintes de eventos de preço.
 * Padrão de Design: Observer (GoF) — Observer interface
 */
public interface ObservadorDePreco {
    void aoAlterarPreco(PrecoDomainEvent evento);
}