package com.studiomuda.estoque.strategy;

import java.time.Month;
import java.time.LocalDate;

/**
 * ConcreteStrategy — ajusta preço conforme a época do ano.
 * Primavera (set-nov): +valorParametro%
 * Inverno (jun-ago): -(valorParametro/2)%
 * Demais meses: preço atual
 *
 * Padrão de Design: Strategy (GoF) — ConcreteStrategy
 */
public class SazonalidadeStrategy implements EstrategiaPrecificacao {

    @Override
    public double calcularPreco(double precoAtual, double custo,
                                int estoque, double valorParametro) {
        Month mesAtual = LocalDate.now().getMonth();

        // Alta temporada — primavera (set, out, nov)
        if (mesAtual == Month.SEPTEMBER || mesAtual == Month.OCTOBER
                || mesAtual == Month.NOVEMBER) {
            return precoAtual * (1 + valorParametro / 100.0);
        }

        // Baixa temporada — inverno (jun, jul, ago)
        if (mesAtual == Month.JUNE || mesAtual == Month.JULY
                || mesAtual == Month.AUGUST) {
            return precoAtual * (1 - (valorParametro / 2.0) / 100.0);
        }

        // Demais meses — mantém preço atual
        return precoAtual;
    }

    @Override
    public String descricao() {
        return "Ajuste sazonal: alta na primavera, desconto no inverno";
    }

    @Override
    public String tipo() { return "SAZONALIDADE"; }
}