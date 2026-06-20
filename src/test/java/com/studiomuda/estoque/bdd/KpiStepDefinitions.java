package com.studiomuda.estoque.bdd;

import com.studiomuda.estoque.calculo.ArredondamentoDecorator;
import com.studiomuda.estoque.calculo.CalculadoraIndicador;
import com.studiomuda.estoque.calculo.LogCalculoDecorator;
import com.studiomuda.estoque.calculo.ValidacaoPeriodoDecorator;
import com.studiomuda.estoque.model.IndicadorOperacional;
import com.studiomuda.estoque.model.MetaIndicador;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Os números são capturados como texto (regex {@code (\S+)}) e convertidos com
 * {@link Double#parseDouble}, evitando a dependência do locale do JVM que o
 * conversor {double} do Cucumber tem (em pt-BR o "." vira separador de milhar).
 */
public class KpiStepDefinitions {
    // Cenarios de meta/alerta
    private MetaIndicador meta;
    private double valorCalculado;

    // Cenarios da cadeia de decorators
    private IndicadorOperacional indicador;
    private CalculadoraIndicador calculadora;
    private double valorRecalculado;
    private Exception erro;

    @Dado("^um indicador \"([^\"]+)\" com meta de valor alvo (\\S+) e operador \"([^\"]+)\"$")
    public void umIndicadorComMeta(String nome, String valorAlvo, String operador) {
        indicador = new IndicadorOperacional();
        indicador.setNome(nome);

        meta = new MetaIndicador();
        meta.setValorAlvo(Double.parseDouble(valorAlvo));
        meta.setOperador(operador);
        erro = null;
    }

    @E("^o limite critico da meta e (\\S+)$")
    public void limiteCriticoDaMeta(String limite) {
        meta.setLimiteCritico(Double.parseDouble(limite));
    }

    @Quando("^o valor calculado do indicador e (\\S+)$")
    public void valorCalculadoDoIndicador(String valor) {
        valorCalculado = Double.parseDouble(valor);
    }

    @Então("a meta deve estar violada")
    public void aMetaDeveEstarViolada() {
        assertTrue(meta.isViolada(valorCalculado), "Esperava que a meta estivesse violada");
    }

    @Então("a meta nao deve estar violada")
    public void aMetaNaoDeveEstarViolada() {
        assertFalse(meta.isViolada(valorCalculado), "Esperava que a meta NAO estivesse violada");
    }

    @E("a meta deve estar em nivel critico")
    public void aMetaDeveEstarEmNivelCritico() {
        assertTrue(meta.isCritico(valorCalculado), "Esperava que a meta estivesse em nivel critico");
    }

    @E("a meta nao deve estar em nivel critico")
    public void aMetaNaoDeveEstarEmNivelCritico() {
        assertFalse(meta.isCritico(valorCalculado), "Esperava que a meta NAO estivesse em nivel critico");
    }

    // ----- Cadeia de decorators (padrao Decorator) -----

    @Dado("^uma calculadora decorada que produz o valor cru (\\S+)$")
    public void umaCalculadoraDecorada(String valorCru) {
        double cru = Double.parseDouble(valorCru);
        // Componente concreto simulado: ignora o cálculo real e devolve um valor fixo.
        CalculadoraIndicador base = (ind, inicio, fim) -> cru;
        // Mesma ordem de empilhamento usada no IndicadorService.
        calculadora = new LogCalculoDecorator(
                new ValidacaoPeriodoDecorator(
                        new ArredondamentoDecorator(base)));

        indicador = new IndicadorOperacional();
        indicador.setNome("Indicador de teste");
        indicador.setTipoCalculo("TICKET_MEDIO");
        erro = null;
    }

    @Quando("recalculo o indicador com periodo de {string} a {string}")
    public void recalculoOIndicadorComPeriodo(String inicio, String fim) {
        try {
            valorRecalculado = calculadora.calcular(indicador, LocalDate.parse(inicio), LocalDate.parse(fim));
        } catch (Exception e) {
            erro = e;
        }
    }

    @Então("o calculo deve ser bloqueado por periodo invalido")
    public void oCalculoDeveSerBloqueadoPorPeriodoInvalido() {
        assertTrue(erro instanceof IllegalArgumentException, "Esperava IllegalArgumentException");
        assertTrue(erro.getMessage().contains("Período inválido"), "Mensagem inesperada: " + erro.getMessage());
    }

    @Então("^o valor recalculado deve ser (\\S+)$")
    public void oValorRecalculadoDeveSer(String esperado) {
        assertNull(erro, "Nao era esperado erro no recalculo");
        assertEquals(Double.parseDouble(esperado), valorRecalculado, 0.0001);
    }
}
