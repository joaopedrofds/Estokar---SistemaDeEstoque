package com.studiomuda.estoque.strategy;

import com.studiomuda.estoque.model.Devolucao;
import java.sql.SQLException;

/**
 * Context do padrão Strategy — seleciona e executa a estratégia correta.
 * Padrão de Design: Strategy (GoF) — Context
 */
public class ContextoRestituicao {

    private final RestituicaoStrategy estrategia;

    public ContextoRestituicao(String tipoRestituicao) {
        switch (tipoRestituicao != null ? tipoRestituicao : "CREDITO_LOJA") {
            case "TROCA":   this.estrategia = new TrocaProdutoStrategy(); break;
            case "ESTORNO": this.estrategia = new EstornoFinanceiroStrategy(); break;
            default:        this.estrategia = new CreditoLojaStrategy(); break;
        }
    }

    public void executar(Devolucao devolucao) throws SQLException {
        estrategia.executar(devolucao);
    }

    public String descricao() { return estrategia.descricao(); }
}