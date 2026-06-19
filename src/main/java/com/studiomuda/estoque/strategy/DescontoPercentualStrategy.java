package com.studiomuda.estoque.strategy;

public class DescontoPercentualStrategy implements EstrategiaDesconto {
    @Override
    public double calcularDesconto(double valorTotalPedido, double valorCupom) {
        if (valorCupom <= 0 || valorCupom > 100) return 0;
        double desconto = valorTotalPedido * (valorCupom / 100.0);
        return Math.min(desconto, valorTotalPedido);
    }

    @Override
    public String descricao() {
        return "Desconto percentual sobre o total";
    }
}