package com.studiomuda.estoque.strategy;

public class ContextoDesconto {
    private final EstrategiaDesconto estrategia;

    public ContextoDesconto(String tipoDesconto) {
        if ("PERCENTUAL".equalsIgnoreCase(tipoDesconto)) {
            this.estrategia = new DescontoPercentualStrategy();
        } else {
            this.estrategia = new DescontoFixoStrategy();
        }
    }

    public double calcular(double valorTotalPedido, double valorCupom) {
        return estrategia.calcularDesconto(valorTotalPedido, valorCupom);
    }

    public String descricao() {
        return estrategia.descricao();
    }
}