package com.studiomuda.estoque.observer;

import com.studiomuda.estoque.strategy.CreditoLojaStrategy;
import com.studiomuda.estoque.strategy.ContextoRestituicao;
import com.studiomuda.estoque.strategy.EstornoFinanceiroStrategy;
import com.studiomuda.estoque.strategy.RestituicaoStrategy;
import com.studiomuda.estoque.strategy.TrocaProdutoStrategy;
import org.springframework.stereotype.Component;

/**
 * ConcreteObserver — executa a Strategy de restituição ao aprovar devolução.
 * Padrão de Design: Observer (GoF) — ConcreteObserver
 */
@Component
public class CreditoClienteObserver implements ObservadorDeDevolucao {

    private final CreditoLojaStrategy creditoLoja;
    private final TrocaProdutoStrategy trocaProduto;
    private final EstornoFinanceiroStrategy estornoFinanceiro;

    public CreditoClienteObserver(CreditoLojaStrategy creditoLoja,
                                  TrocaProdutoStrategy trocaProduto,
                                  EstornoFinanceiroStrategy estornoFinanceiro) {
        this.creditoLoja = creditoLoja;
        this.trocaProduto = trocaProduto;
        this.estornoFinanceiro = estornoFinanceiro;
    }

    @Override
    public void aoAprovarDevolucao(DevolucaoDomainEvent evento) {
        String tipo = evento.getDevolucao().getTipoRestituicao();
        ContextoRestituicao contexto = new ContextoRestituicao(creditoLoja, trocaProduto, estornoFinanceiro);
        RestituicaoStrategy estrategia = contexto.selecionar(tipo);
        try {
            estrategia.executar(evento.getDevolucao());
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao processar restituição da devolução.", e);
        }
    }
}
