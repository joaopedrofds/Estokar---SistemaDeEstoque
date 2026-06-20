package com.studiomuda.estoque.precificacao.application.dto;

import java.util.List;

public class PainelPrecificacaoView {
    private final List<ProdutoPrecificavelView> produtos;
    private final List<PoliticaPrecificacaoView> politicas;
    private final List<ResultadoPrecificacaoView> simulacoesRecentes;
    private final KpiPrecificacaoView kpis;
    private final ParametroPrecificacaoView parametros;

    public PainelPrecificacaoView(List<ProdutoPrecificavelView> produtos,
                                  List<PoliticaPrecificacaoView> politicas,
                                  List<ResultadoPrecificacaoView> simulacoesRecentes,
                                  KpiPrecificacaoView kpis,
                                  ParametroPrecificacaoView parametros) {
        this.produtos = produtos;
        this.politicas = politicas;
        this.simulacoesRecentes = simulacoesRecentes;
        this.kpis = kpis;
        this.parametros = parametros;
    }

    public List<ProdutoPrecificavelView> getProdutos() { return produtos; }
    public List<PoliticaPrecificacaoView> getPoliticas() { return politicas; }
    public List<ResultadoPrecificacaoView> getSimulacoesRecentes() { return simulacoesRecentes; }
    public KpiPrecificacaoView getKpis() { return kpis; }
    public ParametroPrecificacaoView getParametros() { return parametros; }
}
