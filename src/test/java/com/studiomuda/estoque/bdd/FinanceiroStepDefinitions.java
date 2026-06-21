package com.studiomuda.estoque.bdd;

import com.studiomuda.estoque.financeiro.domain.CalculadoraRelatorioFinanceiro;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Steps do relatorio financeiro (E-12). Exercitam a {@link CalculadoraRelatorioFinanceiro}
 * de dominio puro, sem banco. Os numeros sao capturados como texto e convertidos
 * com {@link Double#parseDouble} para evitar a dependencia de locale do conversor
 * {@code {double}} do Cucumber (em pt-BR o "." vira separador de milhar).
 */
public class FinanceiroStepDefinitions {

    private final CalculadoraRelatorioFinanceiro calculadora = new CalculadoraRelatorioFinanceiro();

    private double receita;
    private double custo;
    private double ajustesReceita;
    private double ajustesDespesa;
    private int quantidadePedidos;

    private double resultadoOperacional;
    private double resultadoConsolidado;
    private double margemBruta;
    private double ticketMedio;
    private double variacao;

    @Dado("^uma receita operacional de (\\S+) e custo operacional de (\\S+)$")
    public void umaReceitaECusto(String receita, String custo) {
        this.receita = Double.parseDouble(receita);
        this.custo = Double.parseDouble(custo);
    }

    @E("^ajustes de receita de (\\S+) e ajustes de despesa de (\\S+)$")
    public void ajustes(String ajustesReceita, String ajustesDespesa) {
        this.ajustesReceita = Double.parseDouble(ajustesReceita);
        this.ajustesDespesa = Double.parseDouble(ajustesDespesa);
    }

    @E("^foram pagos (\\d+) pedidos no periodo$")
    public void foramPagosPedidos(int quantidade) {
        this.quantidadePedidos = quantidade;
    }

    @Quando("consolido o relatorio financeiro")
    public void consolidoORelatorio() {
        resultadoOperacional = calculadora.calcularResultadoOperacional(receita, custo);
        resultadoConsolidado = calculadora.calcularResultadoConsolidado(
                resultadoOperacional, ajustesReceita, ajustesDespesa);
    }

    @Quando("calculo os indicadores financeiros")
    public void calculoOsIndicadores() {
        margemBruta = calculadora.calcularMargemBruta(receita, custo);
        ticketMedio = calculadora.calcularTicketMedio(receita, quantidadePedidos);
    }

    @Quando("^comparo o valor atual (\\S+) com o anterior (\\S+)$")
    public void comparoOValor(String atual, String anterior) {
        variacao = calculadora.calcularVariacao(Double.parseDouble(atual), Double.parseDouble(anterior));
    }

    @Então("^o resultado operacional deve ser (\\S+)$")
    public void resultadoOperacionalDeveSer(String esperado) {
        assertEquals(Double.parseDouble(esperado), resultadoOperacional, 0.0001);
    }

    @E("^o resultado consolidado deve ser (\\S+)$")
    public void resultadoConsolidadoDeveSer(String esperado) {
        assertEquals(Double.parseDouble(esperado), resultadoConsolidado, 0.0001);
    }

    @Então("^a margem bruta deve ser (\\S+)$")
    public void margemBrutaDeveSer(String esperado) {
        assertEquals(Double.parseDouble(esperado), margemBruta, 0.0001);
    }

    @E("^o ticket medio deve ser (\\S+)$")
    public void ticketMedioDeveSer(String esperado) {
        assertEquals(Double.parseDouble(esperado), ticketMedio, 0.0001);
    }

    @Então("^a variacao percentual deve ser (\\S+)$")
    public void variacaoPercentualDeveSer(String esperado) {
        assertEquals(Double.parseDouble(esperado), variacao, 0.0001);
    }
}
