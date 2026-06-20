package com.studiomuda.estoque.precificacao.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class PoliticaPrecificacao {
    private Long id;
    private int produtoId;
    private BigDecimal margemLucroDesejada;
    private BigDecimal aliquotaImpostos;
    private BigDecimal percentualDespesasOperacionais;
    private BigDecimal descontoMaximoPermitido;
    private boolean ativa;
    private String observacao;

    public PoliticaPrecificacao(Long id,
                                int produtoId,
                                BigDecimal margemLucroDesejada,
                                BigDecimal aliquotaImpostos,
                                BigDecimal percentualDespesasOperacionais,
                                BigDecimal descontoMaximoPermitido,
                                boolean ativa,
                                String observacao) {
        this.id = id;
        this.produtoId = produtoId;
        this.margemLucroDesejada = percentual(margemLucroDesejada);
        this.aliquotaImpostos = percentual(aliquotaImpostos);
        this.percentualDespesasOperacionais = percentual(percentualDespesasOperacionais);
        this.descontoMaximoPermitido = percentual(descontoMaximoPermitido);
        this.ativa = ativa;
        this.observacao = observacao;
        validar();
    }

    public static PoliticaPrecificacao padrao(int produtoId,
                                              BigDecimal margemPadrao,
                                              BigDecimal impostoPadrao,
                                              BigDecimal despesaPadrao,
                                              BigDecimal descontoPadrao) {
        return new PoliticaPrecificacao(null, produtoId, margemPadrao, impostoPadrao,
                despesaPadrao, descontoPadrao, true, "Política padrão gerada pelos parâmetros globais");
    }

    private BigDecimal percentual(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor.setScale(2, RoundingMode.HALF_UP);
    }

    private void validar() {
        if (produtoId <= 0) {
            throw new IllegalArgumentException("Produto inválido para política de precificação.");
        }
        validarPercentual(margemLucroDesejada, "margem de lucro desejada", false);
        validarPercentual(aliquotaImpostos, "alíquota de impostos", true);
        validarPercentual(percentualDespesasOperacionais, "despesas operacionais", true);
        validarPercentual(descontoMaximoPermitido, "desconto máximo permitido", true);
    }

    private void validarPercentual(BigDecimal valor, String campo, boolean permiteCem) {
        BigDecimal limite = permiteCem ? BigDecimal.valueOf(100) : BigDecimal.valueOf(99.99);
        if (valor.compareTo(BigDecimal.ZERO) < 0 || valor.compareTo(limite) > 0) {
            throw new IllegalArgumentException("O percentual de " + campo + " deve ficar entre 0 e " + limite + ".");
        }
    }

    public Long getId() { return id; }
    public int getProdutoId() { return produtoId; }
    public BigDecimal getMargemLucroDesejada() { return margemLucroDesejada; }
    public BigDecimal getAliquotaImpostos() { return aliquotaImpostos; }
    public BigDecimal getPercentualDespesasOperacionais() { return percentualDespesasOperacionais; }
    public BigDecimal getDescontoMaximoPermitido() { return descontoMaximoPermitido; }
    public boolean isAtiva() { return ativa; }
    public String getObservacao() { return observacao; }
}
