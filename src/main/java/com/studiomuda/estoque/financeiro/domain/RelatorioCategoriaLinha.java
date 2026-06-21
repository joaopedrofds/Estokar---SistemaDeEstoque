package com.studiomuda.estoque.financeiro.domain;

/**
 * Linha de categoria de um {@link RelatorioGerado} (E-12) — Value Object imutável
 * dentro do agregado. Snapshot denormalizado: guarda nome/tipo da categoria no
 * momento da geração, além do {@link CategoriaId} de referência.
 */
public class RelatorioCategoriaLinha {

    private final CategoriaId categoriaId;
    private final String categoriaNome;
    private final String tipoCategoria;
    private final double valorPeriodo;
    private final double valorPeriodoAnterior;
    private final Double variacaoPercentual;
    private final String origemRastreio;
    private final boolean ajusteManual;

    public RelatorioCategoriaLinha(CategoriaId categoriaId, String categoriaNome, String tipoCategoria,
                                   double valorPeriodo, double valorPeriodoAnterior, Double variacaoPercentual,
                                   String origemRastreio, boolean ajusteManual) {
        this.categoriaId = categoriaId;
        this.categoriaNome = categoriaNome;
        this.tipoCategoria = tipoCategoria;
        this.valorPeriodo = valorPeriodo;
        this.valorPeriodoAnterior = valorPeriodoAnterior;
        this.variacaoPercentual = variacaoPercentual;
        this.origemRastreio = origemRastreio;
        this.ajusteManual = ajusteManual;
    }

    public CategoriaId getCategoriaId() { return categoriaId; }
    public String getCategoriaNome() { return categoriaNome; }
    public String getTipoCategoria() { return tipoCategoria; }
    public double getValorPeriodo() { return valorPeriodo; }
    public double getValorPeriodoAnterior() { return valorPeriodoAnterior; }
    public Double getVariacaoPercentual() { return variacaoPercentual; }
    public String getOrigemRastreio() { return origemRastreio; }
    public boolean isAjusteManual() { return ajusteManual; }
}
