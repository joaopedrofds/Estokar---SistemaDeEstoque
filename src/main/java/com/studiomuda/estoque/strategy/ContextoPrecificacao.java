package com.studiomuda.estoque.strategy;

/**
 * Context do padrão Strategy — seleciona a estratégia correta pelo tipo.
 * Padrão de Design: Strategy (GoF) — Context
 */
public class ContextoPrecificacao {

    private final EstrategiaPrecificacao estrategia;

    public ContextoPrecificacao(String tipoEstrategia) {
        switch (tipoEstrategia != null ? tipoEstrategia : "MARGEM_FIXA") {
            case "DESCONTO_VOLUME": this.estrategia = new DescontoVolumeStrategy(); break;
            case "SAZONALIDADE":    this.estrategia = new SazonalidadeStrategy();   break;
            default:                this.estrategia = new MargemFixaStrategy();     break;
        }
    }

    public double calcular(double precoAtual, double custo,
                           int estoque, double valorParametro) {
        return estrategia.calcularPreco(precoAtual, custo, estoque, valorParametro);
    }

    public String descricao() { return estrategia.descricao(); }
    public String tipo()      { return estrategia.tipo(); }
}