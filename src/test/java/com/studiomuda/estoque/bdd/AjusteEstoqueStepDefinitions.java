package com.studiomuda.estoque.bdd;

import com.studiomuda.estoque.jpa.entity.ProdutoJpaEntity;
import com.studiomuda.estoque.service.ajuste.AbstractAjusteEstoqueTemplate;
import com.studiomuda.estoque.service.ajuste.AjustePorAvariaTemplate;
import com.studiomuda.estoque.service.ajuste.AjustePorPerdaTemplate;
import com.studiomuda.estoque.service.ajuste.AjustePorSobraTemplate;
import com.studiomuda.estoque.service.ajuste.ContextoAjuste;
import com.studiomuda.estoque.service.ajuste.ResultadoAjuste;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AjusteEstoqueStepDefinitions {
    private ProdutoJpaEntity produto;
    private int limiteAutomatico;
    private ResultadoAjuste resultado;
    private Exception erro;

    @Dado("que o produto possui saldo atual de {int} unidades")
    public void produtoPossuiSaldoAtual(Integer saldo) throws Exception {
        produto = new ProdutoJpaEntity();
        definirCampo(produto, "id", 1);
        definirCampo(produto, "nome", "Produto de teste");
        produto.setQuantidade(saldo);
        erro = null;
        resultado = null;
    }

    @E("o limite de ajuste automatico e {int} unidades")
    public void limiteDeAjusteAutomatico(Integer limite) {
        limiteAutomatico = limite;
    }

    @Quando("solicito um ajuste {string} de {int} unidades com justificativa {string}")
    public void solicitoAjuste(String tipo, Integer quantidade, String justificativa) {
        try {
            AbstractAjusteEstoqueTemplate template = selecionarTemplate(tipo);
            resultado = template.processarSolicitacao(new ContextoAjuste(
                    produto,
                    tipo,
                    quantidade,
                    justificativa,
                    1,
                    "Operador",
                    limiteAutomatico,
                    30
            ));
        } catch (Exception e) {
            erro = e;
        }
    }

    @Então("a solicitacao deve ficar com status {string}")
    public void solicitacaoDeveFicarComStatus(String status) {
        assertEquals(status, resultado.getSolicitacao().getStatus());
    }

    @E("o saldo projetado deve ser {int} unidades")
    public void saldoProjetado(Integer saldoProjetado) {
        assertEquals(saldoProjetado, resultado.getSolicitacao().getSaldoDepois());
    }

    @Então("o ajuste deve ser bloqueado por saldo insuficiente")
    public void ajusteDeveSerBloqueadoPorSaldoInsuficiente() {
        assertTrue(erro instanceof IllegalArgumentException);
        assertTrue(erro.getMessage().contains("estoque negativo"));
    }

    private AbstractAjusteEstoqueTemplate selecionarTemplate(String tipo) {
        if ("SOBRA".equals(tipo)) {
            return new AjustePorSobraTemplate();
        }
        if ("AVARIA".equals(tipo)) {
            return new AjustePorAvariaTemplate();
        }
        return new AjustePorPerdaTemplate();
    }

    private void definirCampo(Object alvo, String nomeCampo, Object valor) throws Exception {
        Field field = alvo.getClass().getDeclaredField(nomeCampo);
        field.setAccessible(true);
        field.set(alvo, valor);
    }
}
