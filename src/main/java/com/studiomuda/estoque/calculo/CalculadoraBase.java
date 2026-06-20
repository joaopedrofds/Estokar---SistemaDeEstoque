package com.studiomuda.estoque.calculo;

import com.studiomuda.estoque.model.IndicadorOperacional;
import com.studiomuda.estoque.repository.CalculoIndicadorRepository;

import java.time.LocalDate;

/**
 * Componente concreto (ConcreteComponent) do padrão Decorator.
 *
 * Executa o cálculo "cru" do indicador a partir das transações reais
 * (pedido, item_pedido, produto) via JPA ({@link CalculoIndicadorRepository}),
 * sem nenhum comportamento extra. Os decorators ({@link CalculadoraDecorator})
 * embrulham este objeto para adicionar log, validação, arredondamento etc.
 */
public class CalculadoraBase implements CalculadoraIndicador {

    private final CalculoIndicadorRepository calculoRepository;

    public CalculadoraBase(CalculoIndicadorRepository calculoRepository) {
        this.calculoRepository = calculoRepository;
    }

    @Override
    public double calcular(IndicadorOperacional indicador, LocalDate inicio, LocalDate fim) {
        String tipoCalculo = indicador.getTipoCalculo();
        Double valor;

        switch (tipoCalculo) {
            case "TICKET_MEDIO":
                valor = calculoRepository.calcularTicketMedio(inicio, fim);
                break;
            case "TAXA_CANCELAMENTO":
                valor = calculoRepository.calcularTaxaCancelamento(inicio, fim);
                break;
            case "ESTOQUE_CRITICO":
                valor = calculoRepository.calcularEstoqueCritico();
                break;
            case "SEM_ESTOQUE":
                valor = calculoRepository.calcularSemEstoque();
                break;
            default:
                return 0.0;
        }

        return valor != null ? valor : 0.0;
    }
}
