package com.studiomuda.estoque.calculo;

import com.studiomuda.estoque.indicadores.domain.IndicadorOperacional;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Decorator concreto (ConcreteDecorator).
 *
 * Valida o período informado ANTES de delegar o cálculo. Garante que as datas
 * existam e que o início não seja posterior ao fim, evitando snapshots
 * calculados sobre intervalos inválidos.
 */
public class ValidacaoPeriodoDecorator extends CalculadoraDecorator {

    public ValidacaoPeriodoDecorator(CalculadoraIndicador interno) {
        super(interno);
    }

    @Override
    public double calcular(IndicadorOperacional indicador, LocalDate inicio, LocalDate fim) throws SQLException {
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("Período inválido: início e fim são obrigatórios.");
        }
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("Período inválido: a data de início (" + inicio + ") é posterior ao fim (" + fim + ").");
        }
        return delegar(indicador, inicio, fim);
    }
}
