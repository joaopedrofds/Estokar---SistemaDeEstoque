package com.studiomuda.estoque.strategy;

public class DescontoFixoStrategy implements EstrategiaDesconto {
    @Override
    public double calcularDesconto(double valorTotalPedido, double valorCupom) {
        return Math.min(valorCupom, valorTotalPedido);
    }

    @Override
    public String descricao() {
        return "Desconto fixo em reais";
    }
}