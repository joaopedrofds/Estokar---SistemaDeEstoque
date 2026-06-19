package com.studiomuda.estoque.service.ajuste;

import com.studiomuda.estoque.jpa.entity.SolicitacaoAjusteEstoqueJpaEntity;

public class AjustePorPerdaTemplate extends AbstractAjusteEstoqueTemplate {
    @Override
    protected int calcularSaldoDepois(int saldoAtual, int quantidade) {
        return saldoAtual - quantidade;
    }

    @Override
    protected String tipoMovimentacao() {
        return "saida";
    }

    @Override
    protected String motivoMovimentacao(SolicitacaoAjusteEstoqueJpaEntity solicitacao) {
        return "SaidaPorPerda - Ajuste #" + solicitacao.getId();
    }
}
