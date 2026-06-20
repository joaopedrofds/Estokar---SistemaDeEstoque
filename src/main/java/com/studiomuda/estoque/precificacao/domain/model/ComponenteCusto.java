package com.studiomuda.estoque.precificacao.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class ComponenteCusto {
    private final String nome;
    private final TipoComponenteCusto tipo;
    private final BigDecimal percentual;
    private final BigDecimal valor;
    private final BigDecimal baseCalculo;
    private final int ordem;

    public ComponenteCusto(String nome,
                           TipoComponenteCusto tipo,
                           BigDecimal percentual,
                           BigDecimal valor,
                           BigDecimal baseCalculo,
                           int ordem) {
        this.nome = nome;
        this.tipo = tipo;
        this.percentual = escala(percentual);
        this.valor = escala(valor);
        this.baseCalculo = escala(baseCalculo);
        this.ordem = ordem;
    }

    private BigDecimal escala(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor.setScale(2, RoundingMode.HALF_UP);
    }

    public String getNome() { return nome; }
    public TipoComponenteCusto getTipo() { return tipo; }
    public BigDecimal getPercentual() { return percentual; }
    public BigDecimal getValor() { return valor; }
    public BigDecimal getBaseCalculo() { return baseCalculo; }
    public int getOrdem() { return ordem; }
}
