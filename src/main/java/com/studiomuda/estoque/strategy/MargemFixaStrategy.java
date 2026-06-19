package com.studiomuda.estoque.strategy;

/**
 * ConcreteStrategy — calcula preço aplicando margem percentual sobre o custo.
 * Exemplo: custo R$15,00 + margem 92% = R$28,80
 *
 * Padrão de Design: Strategy (GoF) — ConcreteStrategy
 */
public class MargemFixaStrategy implements EstrategiaPrecificacao {

    @Override
    public double calcularPreco(double precoAtual, double custo,
                                int estoque, double valorParametro) {
        // preço = custo × (1 + margem/100)
        return custo * (1 + valorParametro / 100.0);
    }

    @Override
    public String descricao() {
        return "Margem fixa sobre o custo do produto";
    }

    @Override
    public String tipo() { return "MARGEM_FIXA"; }
}