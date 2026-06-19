package com.studiomuda.estoque.observer;

import com.studiomuda.estoque.strategy.ContextoRestituicao;
import java.sql.SQLException;

/**
 * ConcreteObserver — executa a Strategy de restituição ao aprovar devolução.
 * Padrão de Design: Observer (GoF) — ConcreteObserver
 */
public class CreditoClienteObserver implements ObservadorDeDevolucao {

    @Override
    public void aoAprovarDevolucao(DevolucaoDomainEvent evento) {
        try {
            ContextoRestituicao ctx = new ContextoRestituicao(
                    evento.getDevolucao().getTipoRestituicao());
            ctx.executar(evento.getDevolucao());
        } catch (SQLException e) {
            System.err.println("[CreditoClienteObserver] Erro: " + e.getMessage());
        }
    }
}