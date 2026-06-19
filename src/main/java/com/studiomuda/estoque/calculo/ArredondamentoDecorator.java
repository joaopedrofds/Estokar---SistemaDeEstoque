package com.studiomuda.estoque.calculo;

import com.studiomuda.estoque.model.IndicadorOperacional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Decorator concreto (ConcreteDecorator).
 *
 * Transforma o valor retornado pelo componente embrulhado, arredondando para
 * 2 casas decimais. Demonstra que um decorator também pode alterar o
 * resultado (e não só adicionar efeitos colaterais), mantendo a mesma
 * interface.
 */
public class ArredondamentoDecorator extends CalculadoraDecorator {

    private final int casasDecimais;

    public ArredondamentoDecorator(CalculadoraIndicador interno) {
        this(interno, 2);
    }

    public ArredondamentoDecorator(CalculadoraIndicador interno, int casasDecimais) {
        super(interno);
        this.casasDecimais = casasDecimais;
    }

    @Override
    public double calcular(IndicadorOperacional indicador, LocalDate inicio, LocalDate fim) throws SQLException {
        double valor = delegar(indicador, inicio, fim);
        return BigDecimal.valueOf(valor)
                .setScale(casasDecimais, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
