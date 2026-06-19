package com.studiomuda.estoque.strategy;

/**
 * ConcreteStrategy — aplica desconto quando estoque está acima do threshold.
 * Exemplo: estoque 85 un. (threshold 70) → desconto 15% sobre preço atual
 *
 * Padrão de Design: Strategy (GoF) — ConcreteStrategy
 */
public class DescontoVolumeStrategy implements EstrategiaPrecificacao {

    private static final int THRESHOLD_PADRAO = 70;

    @Override
    public double calcularPreco(double precoAtual, double custo,
                                int estoque, double valorParametro) {
        if (estoque > THRESHOLD_PADRAO) {
            // aplica desconto para girar estoque
            return precoAtual * (1 - valorParametro / 100.0);
        }
        // estoque normal — mantém preço atual
        return precoAtual;
    }

    @Override
    public String descricao() {
        return "Desconto automático para girar estoque alto (> " + THRESHOLD_PADRAO + " un.)";
    }

    @Override
    public String tipo() { return "DESCONTO_VOLUME"; }
}