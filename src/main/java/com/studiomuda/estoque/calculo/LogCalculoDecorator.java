package com.studiomuda.estoque.calculo;

import com.studiomuda.estoque.indicadores.domain.IndicadorOperacional;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Decorator concreto (ConcreteDecorator).
 *
 * Registra no console o início e o resultado do cálculo de cada indicador,
 * sem alterar o valor retornado. Útil para rastrear no log qual indicador foi
 * recalculado, em qual período e com qual resultado.
 */
public class LogCalculoDecorator extends CalculadoraDecorator {

    public LogCalculoDecorator(CalculadoraIndicador interno) {
        super(interno);
    }

    @Override
    public double calcular(IndicadorOperacional indicador, LocalDate inicio, LocalDate fim) throws SQLException {
        System.out.println("[LogCalculoDecorator] Calculando indicador '" + indicador.getNome() +
                "' (" + indicador.getTipoCalculo() + ") no período " + inicio + " a " + fim);
        double valor = delegar(indicador, inicio, fim);
        System.out.println("[LogCalculoDecorator] Indicador '" + indicador.getNome() + "' resultou em " + valor);
        return valor;
    }
}
