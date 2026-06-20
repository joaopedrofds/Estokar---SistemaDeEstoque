package com.studiomuda.estoque.precificacao.application.dto;

import com.studiomuda.estoque.precificacao.domain.model.TipoComponenteCusto;
import java.math.BigDecimal;

public class ComponenteCalculoView {
    private final String nome;
    private final TipoComponenteCusto tipo;
    private final BigDecimal percentual;
    private final BigDecimal valor;
    private final BigDecimal baseCalculo;
    private final int ordem;

    public ComponenteCalculoView(String nome, TipoComponenteCusto tipo, BigDecimal percentual,
                                 BigDecimal valor, BigDecimal baseCalculo, int ordem) {
        this.nome = nome;
        this.tipo = tipo;
        this.percentual = percentual;
        this.valor = valor;
        this.baseCalculo = baseCalculo;
        this.ordem = ordem;
    }

    public String getNome() { return nome; }
    public TipoComponenteCusto getTipo() { return tipo; }
    public BigDecimal getPercentual() { return percentual; }
    public BigDecimal getValor() { return valor; }
    public BigDecimal getBaseCalculo() { return baseCalculo; }
    public int getOrdem() { return ordem; }
}
