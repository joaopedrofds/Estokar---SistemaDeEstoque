package com.studiomuda.estoque.calculo;

import com.studiomuda.estoque.indicadores.domain.IndicadorOperacional;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Decorator abstrato (Decorator) do padrão.
 *
 * Implementa a mesma interface {@link CalculadoraIndicador} e mantém uma
 * referência para outro {@code CalculadoraIndicador} (o objeto embrulhado).
 * Por padrão apenas delega a chamada; as subclasses concretas sobrescrevem
 * {@link #calcular} para adicionar comportamento antes e/ou depois de chamar
 * {@link #delegar}.
 */
public abstract class CalculadoraDecorator implements CalculadoraIndicador {

    /** Componente embrulhado (pode ser a base ou outro decorator). */
    protected final CalculadoraIndicador interno;

    protected CalculadoraDecorator(CalculadoraIndicador interno) {
        this.interno = interno;
    }

    /** Delega o cálculo para o componente embrulhado. */
    protected double delegar(IndicadorOperacional indicador, LocalDate inicio, LocalDate fim) throws SQLException {
        return interno.calcular(indicador, inicio, fim);
    }
}
