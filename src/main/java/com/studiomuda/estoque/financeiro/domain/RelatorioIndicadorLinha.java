package com.studiomuda.estoque.financeiro.domain;

/**
 * Linha de indicador de um {@link RelatorioGerado} (E-12) — Value Object imutável
 * dentro do agregado (ex.: MARGEM_BRUTA, TICKET_MEDIO, RESULTADO_LIQUIDO).
 */
public class RelatorioIndicadorLinha {

    private final String indicador;
    private final double valor;
    private final Double valorAnterior;
    private final Double variacaoPercentual;
    private final String formulaDescricao;

    public RelatorioIndicadorLinha(String indicador, double valor, Double valorAnterior,
                                   Double variacaoPercentual, String formulaDescricao) {
        this.indicador = indicador;
        this.valor = valor;
        this.valorAnterior = valorAnterior;
        this.variacaoPercentual = variacaoPercentual;
        this.formulaDescricao = formulaDescricao;
    }

    public String getIndicador() { return indicador; }
    public double getValor() { return valor; }
    public Double getValorAnterior() { return valorAnterior; }
    public Double getVariacaoPercentual() { return variacaoPercentual; }
    public String getFormulaDescricao() { return formulaDescricao; }
}
