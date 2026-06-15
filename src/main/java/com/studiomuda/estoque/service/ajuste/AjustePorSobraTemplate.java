package com.studiomuda.estoque.service.ajuste;

import com.studiomuda.estoque.jpa.entity.SolicitacaoAjusteEstoqueJpaEntity;

public class AjustePorSobraTemplate extends AbstractAjusteEstoqueTemplate {
    @Override
    protected int calcularSaldoDepois(int saldoAtual, int quantidade) {
        return saldoAtual + quantidade;
    }

    @Override
    protected String tipoMovimentacao() {
        return "entrada";
    }

    @Override
    protected String motivoMovimentacao(SolicitacaoAjusteEstoqueJpaEntity solicitacao) {
        return "EntradaPorSobraManual - Ajuste #" + solicitacao.getId();
    }
}
