package com.studiomuda.estoque.calculo;

import com.studiomuda.estoque.model.IndicadorOperacional;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Component do padrão Decorator.
 *
 * Define a operação comum (calcular o valor de um indicador a partir das
 * transações reais) que tanto o componente concreto ({@link CalculadoraBase})
 * quanto os decorators ({@link CalculadoraDecorator} e filhos) implementam.
 * Como todos compartilham esta interface, os decorators podem ser empilhados
 * de forma transparente sobre a calculadora base.
 */
public interface CalculadoraIndicador {
    double calcular(IndicadorOperacional indicador, LocalDate inicio, LocalDate fim) throws SQLException;
}
