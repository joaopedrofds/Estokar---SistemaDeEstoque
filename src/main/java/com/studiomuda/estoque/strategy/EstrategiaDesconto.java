package com.studiomuda.estoque.strategy;

public interface EstrategiaDesconto {
    double calcularDesconto(double valorTotalPedido, double valorCupom);
    String descricao();
}