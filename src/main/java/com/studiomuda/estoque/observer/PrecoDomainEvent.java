package com.studiomuda.estoque.observer;

/**
 * Value Object imutável — representa o evento de alteração de preço.
 * Nível Tático DDD: Domain Event
 */
public class PrecoDomainEvent {
    private final int produtoId;
    private final String produtoNome;
    private final double precoAnterior;
    private final double precoNovo;
    private final String usuarioResponsavel;

    public PrecoDomainEvent(int produtoId, String produtoNome,
                            double precoAnterior, double precoNovo,
                            String usuarioResponsavel) {
        this.produtoId = produtoId;
        this.produtoNome = produtoNome;
        this.precoAnterior = precoAnterior;
        this.precoNovo = precoNovo;
        this.usuarioResponsavel = usuarioResponsavel;
    }

    public int getProdutoId()             { return produtoId; }
    public String getProdutoNome()        { return produtoNome; }
    public double getPrecoAnterior()      { return precoAnterior; }
    public double getPrecoNovo()          { return precoNovo; }
    public String getUsuarioResponsavel() { return usuarioResponsavel; }

    public boolean precoMudou() {
        return Double.compare(precoAnterior, precoNovo) != 0;
    }
}