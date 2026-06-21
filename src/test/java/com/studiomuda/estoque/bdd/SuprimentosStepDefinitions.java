package com.studiomuda.estoque.bdd;

import com.studiomuda.estoque.model.ParametroEstoque;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SuprimentosStepDefinitions {
    private final ParametroEstoque parametro = new ParametroEstoque();
    private double consumoMedioDiario;

    @Dado("que um produto possui estoque atual de {int} unidades")
    public void produtoPossuiEstoqueAtual(Integer estoqueAtual) {
        parametro.setEstoqueAtual(estoqueAtual);
    }

    @E("consumo medio diario de {int} unidades")
    public void consumoMedioDiario(Integer consumo) {
        this.consumoMedioDiario = consumo;
    }

    @E("lead time do fornecedor de {int} dias")
    public void leadTimeDoFornecedor(Integer leadTimeDias) {
        parametro.setLeadTimeDias(leadTimeDias);
    }

    @E("margem de seguranca de {int} unidades")
    public void margemDeSeguranca(Integer margemSeguranca) {
        parametro.setMargemSeguranca(margemSeguranca);
    }

    @Quando("o sistema calcula a necessidade de reposicao")
    public void calculaNecessidadeReposicao() {
        parametro.calcularPontoPedido(consumoMedioDiario);
    }

    @Então("o ponto de pedido deve ser {int} unidades")
    public void pontoPedidoDeveSer(Integer pontoPedido) {
        assertEquals(pontoPedido, parametro.getPontoPedido());
    }

    @E("uma ordem de compra em rascunho deve ser sugerida com {int} unidades")
    public void ordemRascunhoDeveSerSugerida(Integer quantidadeSugerida) {
        assertTrue(parametro.isReposicaoNecessaria());
        assertEquals(quantidadeSugerida, parametro.calcularQuantidadeSugerida());
    }

    @Então("nenhuma ordem de compra deve ser sugerida")
    public void nenhumaOrdemDeCompraDeveSerSugerida() {
        assertTrue(!parametro.isReposicaoNecessaria());
    }

    @Então("uma reposicao deve ser considerada necessaria")
    public void umaReposicaoDeveSerConsideradaNecessaria() {
        assertTrue(parametro.isReposicaoNecessaria());
    }
}
