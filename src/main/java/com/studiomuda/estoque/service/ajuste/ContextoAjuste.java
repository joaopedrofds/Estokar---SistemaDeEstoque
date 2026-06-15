package com.studiomuda.estoque.service.ajuste;

import com.studiomuda.estoque.jpa.entity.ProdutoJpaEntity;

public class ContextoAjuste {
    private final ProdutoJpaEntity produto;
    private final String tipo;
    private final int quantidade;
    private final String justificativa;
    private final int usuarioId;
    private final String usuarioNome;
    private final int limiteQuantidadeSemAprovacao;
    private final int percentualRiscoAlto;

    public ContextoAjuste(ProdutoJpaEntity produto,
                          String tipo,
                          int quantidade,
                          String justificativa,
                          int usuarioId,
                          String usuarioNome,
                          int limiteQuantidadeSemAprovacao,
                          int percentualRiscoAlto) {
        this.produto = produto;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.justificativa = justificativa;
        this.usuarioId = usuarioId;
        this.usuarioNome = usuarioNome;
        this.limiteQuantidadeSemAprovacao = limiteQuantidadeSemAprovacao;
        this.percentualRiscoAlto = percentualRiscoAlto;
    }

    public ProdutoJpaEntity getProduto() { return produto; }
    public String getTipo() { return tipo; }
    public int getQuantidade() { return quantidade; }
    public String getJustificativa() { return justificativa; }
    public int getUsuarioId() { return usuarioId; }
    public String getUsuarioNome() { return usuarioNome; }
    public int getLimiteQuantidadeSemAprovacao() { return limiteQuantidadeSemAprovacao; }
    public int getPercentualRiscoAlto() { return percentualRiscoAlto; }
}
