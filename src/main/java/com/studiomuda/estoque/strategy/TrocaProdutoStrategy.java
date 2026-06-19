package com.studiomuda.estoque.strategy;

import com.studiomuda.estoque.model.Devolucao;

/**
 * ConcreteStrategy — marca devolução para troca física no balcão.
 * Padrão de Design: Strategy (GoF) — ConcreteStrategy
 */
public class TrocaProdutoStrategy implements RestituicaoStrategy {

    @Override
    public void executar(Devolucao devolucao) {
        // Troca é física — apenas registra a aprovação para o operador
        // O estoque já foi restaurado pelo EstoqueDevolucaoObserver
        System.out.println("[TrocaProdutoStrategy] Devolução #" +
                devolucao.getId() + " aprovada para troca no balcão.");
    }

    @Override
    public String descricao() { return "Troca de produto no balcão"; }
}