package com.studiomuda.estoque.strategy;

import com.studiomuda.estoque.model.Devolucao;
import org.springframework.stereotype.Component;

/**
 * ConcreteStrategy — registra estorno financeiro manual (ex: estorno em cartão).
 * Padrão de Design: Strategy (GoF) — ConcreteStrategy
 */
@Component
public class EstornoFinanceiroStrategy implements RestituicaoStrategy {

    @Override
    public void executar(Devolucao devolucao) {
        // Estorno é processado externamente — registra para controle
        System.out.println("[EstornoFinanceiroStrategy] Estorno financeiro registrado para devolução #" +
                devolucao.getId());
    }

    @Override
    public String descricao() { return "Estorno financeiro (cartão/boleto)"; }
}
