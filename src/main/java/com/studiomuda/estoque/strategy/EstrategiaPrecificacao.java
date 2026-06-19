package com.studiomuda.estoque.strategy;

/**
 * Interface Strategy para algoritmos de precificação.
 * Padrão de Design: Strategy (GoF) — Strategy interface
 * Nível Tático DDD: Domain Service contract
 */
public interface EstrategiaPrecificacao {
    /**
     * Calcula o preço sugerido com base nos dados do produto.
     * @param precoAtual    preço atual do produto
     * @param custo         custo do produto
     * @param estoque       quantidade em estoque
     * @param valorParametro valor configurado na regra (margem%, desconto%, ajuste%)
     * @return preço sugerido calculado
     */
    double calcularPreco(double precoAtual, double custo,
                         int estoque, double valorParametro);

    String descricao();
    String tipo();
}