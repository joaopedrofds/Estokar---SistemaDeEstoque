package com.studiomuda.estoque.bdd;

import com.studiomuda.estoque.model.AlertaReposicao;
import com.studiomuda.estoque.model.ParametroEstoque;
import com.studiomuda.estoque.repository.AlertaReposicaoRepository;
import com.studiomuda.estoque.service.AlertaReposicaoService;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class AlertaReposicaoStepDefinitions {

    @Autowired
    private AlertaReposicaoService alertaReposicaoService;

    @Autowired
    private AlertaReposicaoRepository alertaReposicaoRepository;

    private ParametroEstoque parametro;
    private List<AlertaReposicao> alertasAtivos;
    private List<AlertaReposicao> alertasResolvidos;

    @io.cucumber.java.Before
    public void limparAlertas() {
        alertaReposicaoRepository.deleteAll();
        parametro = new ParametroEstoque();
        parametro.setProdutoId(1);
    }

    @Dado("um parametro de reposicao para o produto {string} com fornecedor {string}")
    public void umParametroDeReposicaoParaOProdutoComFornecedor(String produto, String fornecedor) {
        parametro.setProdutoNome(produto);
        parametro.setFornecedorNome(fornecedor);
    }

    @E("estoque atual de {int} unidades para reposicao")
    public void estoqueAtualDeUnidadesParaReposicao(Integer estoqueAtual) {
        parametro.setEstoqueAtual(estoqueAtual);
    }

    @E("ponto de pedido de {int} unidades para reposicao")
    public void pontoDePedidoDeUnidadesParaReposicao(Integer pontoPedido) {
        parametro.setPontoPedido(pontoPedido);
    }

    @E("quantidade sugerida de {int} unidades")
    public void quantidadeSugeridaDeUnidades(Integer quantidadeSugerida) {
        assertEquals(quantidadeSugerida, parametro.calcularQuantidadeSugerida());
    }

    @E("ja existe um alerta ativo para esse produto")
    public void jaExisteUmAlertaAtivoParaEsseProduto() {
        AlertaReposicao alerta = new AlertaReposicao();
        alerta.setProdutoId(parametro.getProdutoId());
        alerta.setProdutoNome(parametro.getProdutoNome());
        alerta.setFornecedorNome(parametro.getFornecedorNome());
        alerta.setEstoqueAtual(99);
        alerta.setPontoPedido(parametro.getPontoPedido());
        alerta.setQuantidadeSugerida(1);
        alerta.setStatus(AlertaReposicao.STATUS_ATIVO);
        alertaReposicaoRepository.save(alerta);
    }

    @Quando("sincronizo os alertas de reposicao")
    public void sincronizoOsAlertasDeReposicao() {
        alertaReposicaoService.sincronizarAlertas(List.of(parametro));
        alertasAtivos = alertaReposicaoService.listarAtivos();
        alertasResolvidos = alertaReposicaoService.listarResolvidos();
    }

    @Entao("deve existir {int} alerta ativo de reposicao")
    public void deveExistirAlertaAtivoDeReposicao(Integer quantidade) {
        assertEquals(quantidade, alertasAtivos.size());
    }

    @E("o alerta de reposicao deve referenciar o produto {string}")
    public void oAlertaDeReposicaoDeveReferenciarOProduto(String produto) {
        assertEquals(produto, alertasAtivos.get(0).getProdutoNome());
    }

    @E("o alerta de reposicao deve ter estoque atual {int}")
    public void oAlertaDeReposicaoDeveTerEstoqueAtual(Integer estoqueAtual) {
        assertEquals(estoqueAtual, alertasAtivos.get(0).getEstoqueAtual());
    }

    @Entao("nao deve existir alerta ativo para o produto")
    public void naoDeveExistirAlertaAtivoParaOProduto() {
        assertFalse(alertasAtivos.stream().anyMatch(alerta -> alerta.getProdutoId().equals(parametro.getProdutoId())));
    }

    @E("deve existir {int} alerta resolvido de reposicao")
    public void deveExistirAlertaResolvidoDeReposicao(Integer quantidade) {
        assertEquals(quantidade, alertasResolvidos.size());
    }
}
