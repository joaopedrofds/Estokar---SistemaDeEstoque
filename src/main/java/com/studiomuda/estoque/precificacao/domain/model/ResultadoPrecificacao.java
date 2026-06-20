package com.studiomuda.estoque.precificacao.domain.model;

import com.studiomuda.estoque.precificacao.domain.iterator.ComponentesCusto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;


public class ResultadoPrecificacao {
    private final int produtoId;
    private final String produtoNome;
    private final BigDecimal precoAtual;
    private final BigDecimal custoCompra;
    private final BigDecimal valorImpostos;
    private final BigDecimal valorDespesasOperacionais;
    private final BigDecimal custoTotal;
    private final BigDecimal precoSugerido;
    private final BigDecimal precoMinimoPermitido;
    private final BigDecimal margemLucroDesejada;
    private final BigDecimal margemMinimaGlobal;
    private final BigDecimal margemReal;
    private final BigDecimal descontoMaximoSolicitado;
    private final BigDecimal descontoMaximoEfetivo;
    private final StatusPrecificacao status;
    private final String justificativa;
    private final List<ComponenteCusto> componentes;

    public ResultadoPrecificacao(int produtoId,
                                 String produtoNome,
                                 BigDecimal precoAtual,
                                 BigDecimal custoCompra,
                                 BigDecimal valorImpostos,
                                 BigDecimal valorDespesasOperacionais,
                                 BigDecimal custoTotal,
                                 BigDecimal precoSugerido,
                                 BigDecimal precoMinimoPermitido,
                                 BigDecimal margemLucroDesejada,
                                 BigDecimal margemMinimaGlobal,
                                 BigDecimal margemReal,
                                 BigDecimal descontoMaximoSolicitado,
                                 BigDecimal descontoMaximoEfetivo,
                                 StatusPrecificacao status,
                                 String justificativa,
                                 ComponentesCusto componentes) {
        this.produtoId = produtoId;
        this.produtoNome = produtoNome;
        this.precoAtual = dinheiro(precoAtual);
        this.custoCompra = dinheiro(custoCompra);
        this.valorImpostos = dinheiro(valorImpostos);
        this.valorDespesasOperacionais = dinheiro(valorDespesasOperacionais);
        this.custoTotal = dinheiro(custoTotal);
        this.precoSugerido = dinheiro(precoSugerido);
        this.precoMinimoPermitido = dinheiro(precoMinimoPermitido);
        this.margemLucroDesejada = percentual(margemLucroDesejada);
        this.margemMinimaGlobal = percentual(margemMinimaGlobal);
        this.margemReal = percentual(margemReal);
        this.descontoMaximoSolicitado = percentual(descontoMaximoSolicitado);
        this.descontoMaximoEfetivo = percentual(descontoMaximoEfetivo);
        this.status = status;
        this.justificativa = justificativa;
        this.componentes = componentes == null ? Collections.emptyList() : componentes.comoLista();
    }

    private BigDecimal dinheiro(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal percentual(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor.setScale(2, RoundingMode.HALF_UP);
    }

    public int getProdutoId() { return produtoId; }
    public String getProdutoNome() { return produtoNome; }
    public BigDecimal getPrecoAtual() { return precoAtual; }
    public BigDecimal getCustoCompra() { return custoCompra; }
    public BigDecimal getValorImpostos() { return valorImpostos; }
    public BigDecimal getValorDespesasOperacionais() { return valorDespesasOperacionais; }
    public BigDecimal getCustoTotal() { return custoTotal; }
    public BigDecimal getPrecoSugerido() { return precoSugerido; }
    public BigDecimal getPrecoMinimoPermitido() { return precoMinimoPermitido; }
    public BigDecimal getMargemLucroDesejada() { return margemLucroDesejada; }
    public BigDecimal getMargemMinimaGlobal() { return margemMinimaGlobal; }
    public BigDecimal getMargemReal() { return margemReal; }
    public BigDecimal getDescontoMaximoSolicitado() { return descontoMaximoSolicitado; }
    public BigDecimal getDescontoMaximoEfetivo() { return descontoMaximoEfetivo; }
    public StatusPrecificacao getStatus() { return status; }
    public String getJustificativa() { return justificativa; }
    public List<ComponenteCusto> getComponentes() { return componentes; }

    public boolean isAprovado() { return status == StatusPrecificacao.APROVADO; }

    public BigDecimal getVariacaoPercentual() {
        if (precoAtual.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return precoSugerido.subtract(precoAtual)
                .multiply(BigDecimal.valueOf(100))
                .divide(precoAtual, 2, RoundingMode.HALF_UP);
    }
}
