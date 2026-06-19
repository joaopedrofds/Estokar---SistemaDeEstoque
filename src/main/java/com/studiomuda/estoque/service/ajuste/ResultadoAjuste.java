package com.studiomuda.estoque.service.ajuste;

import com.studiomuda.estoque.jpa.entity.MovimentacaoEstoqueJpaEntity;
import com.studiomuda.estoque.jpa.entity.SolicitacaoAjusteEstoqueJpaEntity;

public class ResultadoAjuste {
    private final SolicitacaoAjusteEstoqueJpaEntity solicitacao;
    private final boolean exigeAprovacao;
    private final MovimentacaoEstoqueJpaEntity movimentacao;

    public ResultadoAjuste(SolicitacaoAjusteEstoqueJpaEntity solicitacao,
                           boolean exigeAprovacao,
                           MovimentacaoEstoqueJpaEntity movimentacao) {
        this.solicitacao = solicitacao;
        this.exigeAprovacao = exigeAprovacao;
        this.movimentacao = movimentacao;
    }

    public SolicitacaoAjusteEstoqueJpaEntity getSolicitacao() { return solicitacao; }
    public boolean isExigeAprovacao() { return exigeAprovacao; }
    public MovimentacaoEstoqueJpaEntity getMovimentacao() { return movimentacao; }
}
