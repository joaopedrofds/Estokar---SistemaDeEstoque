package com.studiomuda.estoque.bdd;

import com.studiomuda.estoque.model.Devolucao;
import com.studiomuda.estoque.strategy.ContextoRestituicao;
import com.studiomuda.estoque.strategy.ContextoDesconto;
import com.studiomuda.estoque.strategy.CreditoLojaStrategy;
import com.studiomuda.estoque.strategy.EstornoFinanceiroStrategy;
import com.studiomuda.estoque.strategy.RestituicaoStrategy;
import com.studiomuda.estoque.strategy.TrocaProdutoStrategy;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class EstrategiasDescontoStepDefinitions {

    private double valorPedido;
    private double valorCupom;
    private String tipoCupom;
    private double desconto;
    private Devolucao devolucao;
    private RestituicaoStrategy estrategiaRestituicao;

    @Dado("um pedido no valor de {string}")
    public void umPedidoNoValorDe(String valorPedido) {
        this.valorPedido = Double.parseDouble(valorPedido);
    }

    @E("um cupom {string} no valor de {string}")
    public void umCupomNoValorDe(String tipoCupom, String valorCupom) {
        this.tipoCupom = tipoCupom;
        this.valorCupom = Double.parseDouble(valorCupom);
    }

    @Quando("calculo o desconto pela estrategia")
    public void calculoODescontoPelaEstrategia() {
        desconto = new ContextoDesconto(tipoCupom).calcular(valorPedido, valorCupom);
    }

    @Entao("o desconto calculado deve ser {string}")
    public void oDescontoCalculadoDeveSer(String esperado) {
        assertEquals(Double.parseDouble(esperado), desconto, 0.001);
    }

    @Dado("uma devolucao com tipo de restituicao {string}")
    public void umaDevolucaoComTipoDeRestituicao(String tipoRestituicao) {
        devolucao = new Devolucao();
        devolucao.setTipoRestituicao(tipoRestituicao);
    }

    @Quando("seleciono a estrategia de restituicao")
    public void selecionoAEstrategiaDeRestituicao() {
        CreditoLojaStrategy credito = new CreditoLojaStrategy(mock(com.studiomuda.estoque.repository.CreditoClienteRepository.class));
        TrocaProdutoStrategy troca = new TrocaProdutoStrategy();
        EstornoFinanceiroStrategy estorno = new EstornoFinanceiroStrategy();
        estrategiaRestituicao = new ContextoRestituicao(credito, troca, estorno)
                .selecionar(devolucao.getTipoRestituicao());
    }

    @Entao("a descricao da estrategia de restituicao deve ser {string}")
    public void aDescricaoDaEstrategiaDeRestituicaoDeveSer(String descricao) {
        assertEquals(descricao, estrategiaRestituicao.descricao());
    }
}
