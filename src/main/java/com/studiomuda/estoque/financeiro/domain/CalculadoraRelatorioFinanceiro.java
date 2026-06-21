package com.studiomuda.estoque.financeiro.domain;

/**
 * Regras de cálculo do relatório financeiro consolidado (E-12), em Java puro.
 *
 * <p>Concentra a aritmética que antes vivia embutida no {@code RelatorioDAO}
 * (consolidação de resultado, variação percentual entre períodos e indicadores
 * financeiros), tornando-a testável sem banco — conforme o padrão do contexto
 * {@code Precificacao}. Não conhece JDBC nem Spring.</p>
 */
public class CalculadoraRelatorioFinanceiro {

    /**
     * Variação percentual entre o valor de um período e o do período anterior.
     *
     * <p>Quando não há base anterior (zero), considera-se {@code 0%} se o atual
     * também é zero e {@code 100%} caso contrário. Usa o módulo do anterior para
     * preservar o sinal real da variação.</p>
     */
    public double calcularVariacao(double atual, double anterior) {
        if (anterior == 0) {
            return atual == 0 ? 0.0 : 100.0;
        }
        return ((atual - anterior) / Math.abs(anterior)) * 100;
    }

    /** Resultado operacional: receita operacional menos custo operacional. */
    public double calcularResultadoOperacional(double receita, double custo) {
        return receita - custo;
    }

    /** Resultado consolidado: operacional + ajustes de receita − ajustes de despesa. */
    public double calcularResultadoConsolidado(double resultadoOperacional,
                                               double ajustesReceita,
                                               double ajustesDespesa) {
        return resultadoOperacional + ajustesReceita - ajustesDespesa;
    }

    /** Margem bruta percentual: (receita − custo) / receita × 100 (0 se sem receita). */
    public double calcularMargemBruta(double receita, double custo) {
        return receita > 0 ? ((receita - custo) / receita) * 100 : 0;
    }

    /** Ticket médio: receita / quantidade de pedidos pagos (0 se sem pedidos). */
    public double calcularTicketMedio(double receita, int quantidadePedidos) {
        return quantidadePedidos > 0 ? receita / quantidadePedidos : 0;
    }
}
