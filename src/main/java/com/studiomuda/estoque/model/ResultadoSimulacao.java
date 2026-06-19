package com.studiomuda.estoque.model;

/**
 * Value Object — resultado de uma simulação de preço (não persiste no banco).
 * Nível Tático DDD: Value Object
 */
public class ResultadoSimulacao {

    private final int produtoId;
    private final String produtoNome;
    private final double precoAtual;
    private final double custoProduto;
    private final double precoSugerido;
    private final double margemCalculada;
    private final double margemMinima;
    private final String tipoEstrategia;
    private final String descricaoEstrategia;
    private final String status;
    private final String justificativa;
    private final double precoMinimo;

    public ResultadoSimulacao(int produtoId, String produtoNome,
                               double precoAtual, double custoProduto,
                               double precoSugerido, double margemMinima,
                               String tipoEstrategia, String descricaoEstrategia) {
        this.produtoId = produtoId;
        this.produtoNome = produtoNome;
        this.precoAtual = precoAtual;
        this.custoProduto = custoProduto;
        this.tipoEstrategia = tipoEstrategia;
        this.descricaoEstrategia = descricaoEstrategia;
        this.margemMinima = margemMinima;
        this.precoMinimo = custoProduto / (1 - margemMinima / 100.0);

        // Calcula margem real
        this.margemCalculada = precoSugerido > 0
                ? ((precoSugerido - custoProduto) / precoSugerido) * 100.0
                : 0;

        // Valida margem mínima
        if (this.margemCalculada >= margemMinima) {
            this.precoSugerido = precoSugerido;
            this.status = "APROVADO";
            this.justificativa = String.format(
                "Estratégia '%s' aplicada. Margem calculada: %.1f%% ≥ mínimo %.1f%%.",
                descricaoEstrategia, margemCalculada, margemMinima);
        } else {
            this.precoSugerido = precoSugerido;
            this.status = "BLOQUEADO";
            this.justificativa = String.format(
                "Margem calculada %.1f%% está abaixo do mínimo permitido de %.1f%%. " +
                "Preço mínimo para esta margem: R$ %.2f.",
                margemCalculada, margemMinima, precoMinimo);
        }
    }

    public int getProdutoId()              { return produtoId; }
    public String getProdutoNome()         { return produtoNome; }
    public double getPrecoAtual()          { return precoAtual; }
    public double getCustoProduto()        { return custoProduto; }
    public double getPrecoSugerido()       { return precoSugerido; }
    public double getMargemCalculada()     { return margemCalculada; }
    public double getMargemMinima()        { return margemMinima; }
    public double getPrecoMinimo()         { return precoMinimo; }
    public String getTipoEstrategia()      { return tipoEstrategia; }
    public String getDescricaoEstrategia() { return descricaoEstrategia; }
    public String getStatus()              { return status; }
    public String getJustificativa()       { return justificativa; }
    public boolean isAprovado()            { return "APROVADO".equals(status); }
    public boolean getAprovado()           { return isAprovado(); }
    public boolean isBloqueado()           { return "BLOQUEADO".equals(status); }

    public double getVariacaoPercent() {
        if (precoAtual == 0) return 0;
        return ((precoSugerido - precoAtual) / precoAtual) * 100.0;
    }
}