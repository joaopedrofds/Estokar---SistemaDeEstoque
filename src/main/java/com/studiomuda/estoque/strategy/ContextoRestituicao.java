package com.studiomuda.estoque.strategy;

import com.studiomuda.estoque.model.Devolucao;

public class ContextoRestituicao {

    private final RestituicaoStrategy creditoLojaStrategy;
    private final RestituicaoStrategy trocaProdutoStrategy;
    private final RestituicaoStrategy estornoFinanceiroStrategy;

    public ContextoRestituicao(RestituicaoStrategy creditoLojaStrategy,
                               RestituicaoStrategy trocaProdutoStrategy,
                               RestituicaoStrategy estornoFinanceiroStrategy) {
        this.creditoLojaStrategy = creditoLojaStrategy;
        this.trocaProdutoStrategy = trocaProdutoStrategy;
        this.estornoFinanceiroStrategy = estornoFinanceiroStrategy;
    }

    public RestituicaoStrategy selecionar(String tipoRestituicao) {
        if ("TROCA".equalsIgnoreCase(tipoRestituicao)) {
            return trocaProdutoStrategy;
        }
        if ("ESTORNO".equalsIgnoreCase(tipoRestituicao)) {
            return estornoFinanceiroStrategy;
        }
        return creditoLojaStrategy;
    }

    public void executar(Devolucao devolucao) throws java.sql.SQLException {
        selecionar(devolucao.getTipoRestituicao()).executar(devolucao);
    }
}
