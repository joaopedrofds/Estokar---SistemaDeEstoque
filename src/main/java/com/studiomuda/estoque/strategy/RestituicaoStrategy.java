package com.studiomuda.estoque.strategy;

import com.studiomuda.estoque.model.Devolucao;
import java.sql.SQLException;

/**
 * Interface Strategy para tipos de restituição em devoluções.
 * Padrão de Design: Strategy (GoF) — Strategy interface
 * Nível Tático DDD: Domain Service contract
 */
public interface RestituicaoStrategy {
    void executar(Devolucao devolucao) throws SQLException;
    String descricao();
}