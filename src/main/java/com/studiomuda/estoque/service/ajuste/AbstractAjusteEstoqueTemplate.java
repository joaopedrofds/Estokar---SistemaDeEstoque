package com.studiomuda.estoque.service.ajuste;

import com.studiomuda.estoque.jpa.entity.MovimentacaoEstoqueJpaEntity;
import com.studiomuda.estoque.jpa.entity.ProdutoJpaEntity;
import com.studiomuda.estoque.jpa.entity.SolicitacaoAjusteEstoqueJpaEntity;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class AbstractAjusteEstoqueTemplate {
    public final ResultadoAjuste processarSolicitacao(ContextoAjuste contexto) {
        validarDados(contexto);
        ProdutoJpaEntity produto = contexto.getProduto();
        int saldoAntes = valorOuZero(produto.getQuantidade());
        int saldoDepois = calcularSaldoDepois(saldoAntes, contexto.getQuantidade());

        validarSaldo(saldoDepois);
        String risco = classificarRisco(saldoAntes, contexto);
        boolean exigeAprovacao = exigeAprovacao(contexto, risco);

        SolicitacaoAjusteEstoqueJpaEntity solicitacao = montarSolicitacao(contexto, saldoAntes, saldoDepois, risco, exigeAprovacao);
        if (!exigeAprovacao) {
            aplicarSaldo(produto, saldoDepois);
            solicitacao.setStatus(StatusAjuste.APLICADO_AUTOMATICAMENTE.name());
            solicitacao.setDataDecisao(Timestamp.valueOf(LocalDateTime.now()));
        }
        return new ResultadoAjuste(solicitacao, exigeAprovacao, null);
    }

    public final MovimentacaoEstoqueJpaEntity aplicarSolicitacaoAprovada(SolicitacaoAjusteEstoqueJpaEntity solicitacao,
                                                                         ProdutoJpaEntity produto) {
        if (!StatusAjuste.PENDENTE_APROVACAO.name().equals(solicitacao.getStatus())) {
            throw new IllegalStateException("Somente solicitacoes pendentes podem ser aprovadas.");
        }
        aplicarSaldo(produto, solicitacao.getSaldoDepois());
        solicitacao.setStatus(StatusAjuste.APROVADO.name());
        solicitacao.setDataDecisao(Timestamp.valueOf(LocalDateTime.now()));
        return criarMovimentacaoAplicada(solicitacao);
    }

    public final MovimentacaoEstoqueJpaEntity criarMovimentacaoAplicada(SolicitacaoAjusteEstoqueJpaEntity solicitacao) {
        if (StatusAjuste.PENDENTE_APROVACAO.name().equals(solicitacao.getStatus())
                || StatusAjuste.REPROVADO.name().equals(solicitacao.getStatus())) {
            return null;
        }
        return new MovimentacaoEstoqueJpaEntity(
                solicitacao.getProdutoId(),
                tipoMovimentacao(),
                solicitacao.getQuantidade(),
                motivoMovimentacao(solicitacao),
                Date.valueOf(LocalDate.now())
        );
    }

    protected abstract int calcularSaldoDepois(int saldoAtual, int quantidade);

    protected abstract String tipoMovimentacao();

    protected abstract String motivoMovimentacao(SolicitacaoAjusteEstoqueJpaEntity solicitacao);

    protected void validarSaldo(int saldoDepois) {
        if (saldoDepois < 0) {
            throw new IllegalArgumentException("Ajuste nao pode deixar o estoque negativo.");
        }
    }

    protected void validarDados(ContextoAjuste contexto) {
        if (contexto == null) {
            throw new IllegalArgumentException("Informe os dados do ajuste.");
        }
        if (contexto.getProduto() == null || contexto.getProduto().getId() == null) {
            throw new IllegalArgumentException("Produto do ajuste nao encontrado.");
        }
        if (contexto.getQuantidade() <= 0) {
            throw new IllegalArgumentException("A quantidade do ajuste deve ser maior que zero.");
        }
        if (contexto.getJustificativa() == null
                || contexto.getJustificativa().trim().length() < 10
                || contexto.getJustificativa().trim().length() > 300) {
            throw new IllegalArgumentException("A justificativa deve ter entre 10 e 300 caracteres.");
        }
        if (contexto.getUsuarioId() <= 0 || contexto.getUsuarioNome() == null || contexto.getUsuarioNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o usuario responsavel pela solicitacao.");
        }
    }

    private SolicitacaoAjusteEstoqueJpaEntity montarSolicitacao(ContextoAjuste contexto,
                                                                int saldoAntes,
                                                                int saldoDepois,
                                                                String risco,
                                                                boolean exigeAprovacao) {
        SolicitacaoAjusteEstoqueJpaEntity solicitacao = new SolicitacaoAjusteEstoqueJpaEntity();
        solicitacao.setProdutoId(contexto.getProduto().getId());
        solicitacao.setTipo(contexto.getTipo());
        solicitacao.setQuantidade(contexto.getQuantidade());
        solicitacao.setJustificativa(contexto.getJustificativa().trim());
        solicitacao.setSolicitanteId(contexto.getUsuarioId());
        solicitacao.setSolicitanteNome(contexto.getUsuarioNome());
        solicitacao.setSaldoAntes(saldoAntes);
        solicitacao.setSaldoDepois(saldoDepois);
        solicitacao.setRisco(risco);
        solicitacao.setExigeAprovacao(exigeAprovacao);
        solicitacao.setStatus(exigeAprovacao
                ? StatusAjuste.PENDENTE_APROVACAO.name()
                : StatusAjuste.APLICADO_AUTOMATICAMENTE.name());
        solicitacao.setDataSolicitacao(Timestamp.valueOf(LocalDateTime.now()));
        return solicitacao;
    }

    private String classificarRisco(int saldoAntes, ContextoAjuste contexto) {
        if (contexto.getQuantidade() > contexto.getLimiteQuantidadeSemAprovacao()) {
            return "ALTO";
        }
        if (saldoAntes == 0) {
            return contexto.getQuantidade() > 0 ? "MEDIO" : "BAIXO";
        }
        int percentual = Math.abs(contexto.getQuantidade() * 100 / saldoAntes);
        if (percentual >= contexto.getPercentualRiscoAlto()) {
            return "ALTO";
        }
        return percentual >= 10 ? "MEDIO" : "BAIXO";
    }

    private boolean exigeAprovacao(ContextoAjuste contexto, String risco) {
        return "ALTO".equals(risco) || contexto.getQuantidade() > contexto.getLimiteQuantidadeSemAprovacao();
    }

    private void aplicarSaldo(ProdutoJpaEntity produto, int saldoDepois) {
        produto.setQuantidade(saldoDepois);
    }

    private int valorOuZero(Integer valor) {
        return valor != null ? valor : 0;
    }
}
