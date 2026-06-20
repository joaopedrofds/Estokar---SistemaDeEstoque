package com.studiomuda.estoque.bdd;

import com.studiomuda.estoque.precificacao.domain.model.MotorPrecificacaoDinamica;
import com.studiomuda.estoque.precificacao.domain.model.PoliticaPrecificacao;
import com.studiomuda.estoque.precificacao.domain.model.ResultadoPrecificacao;
import com.studiomuda.estoque.precificacao.domain.model.StatusPrecificacao;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

import java.math.BigDecimal;
import java.text.Normalizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrecificacaoStepDefinitions {
    private final MotorPrecificacaoDinamica motor = new MotorPrecificacaoDinamica();
    private int produtoId;
    private String produtoNome = "Produto teste";
    private BigDecimal precoAtual;
    private BigDecimal custoCompra;
    private PoliticaPrecificacao politica;
    private BigDecimal margemMinimaGlobal;
    private BigDecimal descontoMaximoGlobal;
    private ResultadoPrecificacao resultado;

    @Dado("^um produto de id (\\d+) com preco atual de (\\S+) e custo de compra de (\\S+)$")
    public void umProdutoComPrecoECusto(int produtoId, String precoAtual, String custoCompra) {
        this.produtoId = produtoId;
        this.produtoNome = "Produto " + produtoId;
        this.precoAtual = new BigDecimal(precoAtual);
        this.custoCompra = new BigDecimal(custoCompra);
    }

    @Dado("^uma politica com margem desejada de (\\S+), impostos de (\\S+), despesas operacionais de (\\S+) e desconto maximo de (\\S+)$")
    public void umaPolitica(String margem, String impostos, String despesas, String desconto) {
        this.politica = new PoliticaPrecificacao(
                null,
                produtoId,
                new BigDecimal(margem),
                new BigDecimal(impostos),
                new BigDecimal(despesas),
                new BigDecimal(desconto),
                true,
                "politica de teste");
    }

    @Dado("^uma margem minima global de (\\S+) e desconto maximo global de (\\S+)$")
    public void parametrosGlobais(String margemMinima, String descontoMaximo) {
        this.margemMinimaGlobal = new BigDecimal(margemMinima);
        this.descontoMaximoGlobal = new BigDecimal(descontoMaximo);
    }

    @Quando("o sistema calcula a precificacao dinamica")
    public void oSistemaCalculaAPrecificacaoDinamica() {
        resultado = motor.calcular(
                produtoId,
                produtoNome,
                precoAtual,
                custoCompra,
                politica,
                margemMinimaGlobal,
                descontoMaximoGlobal
        );
    }

    @Entao("^o custo total deve ser (\\S+)$")
    public void oCustoTotalDeveSer(String esperado) {
        assertNotNull(resultado);
        assertEquals(0, resultado.getCustoTotal().compareTo(new BigDecimal(esperado)));
    }

    @Entao("^o preco sugerido deve ser (\\S+)$")
    public void oPrecoSugeridoDeveSer(String esperado) {
        assertNotNull(resultado);
        assertEquals(0, resultado.getPrecoSugerido().compareTo(new BigDecimal(esperado)));
    }

    @Entao("^o status deve ser \"([^\"]+)\"$")
    public void oStatusDeveSer(String statusEsperado) {
        assertNotNull(resultado);
        assertEquals(StatusPrecificacao.valueOf(statusEsperado), resultado.getStatus());
    }

    @Entao("^a simulacao deve conter (\\d+) componentes de custo$")
    public void aSimulacaoDeveConterComponentes(int quantidade) {
        assertNotNull(resultado);
        assertEquals(quantidade, resultado.getComponentes().size());
        assertEquals("Custo de compra do produto", resultado.getComponentes().get(0).getNome());
        assertEquals("Impostos sobre compra/venda", resultado.getComponentes().get(1).getNome());
        assertEquals("Rateio de despesas operacionais", resultado.getComponentes().get(2).getNome());
    }

    @Entao("^a justificativa deve conter \"([^\"]+)\"$")
    public void aJustificativaDeveConter(String trecho) {
        assertNotNull(resultado);
        String justificativaNormalizada = normalizar(resultado.getJustificativa());
        String trechoNormalizado = normalizar(trecho);
        assertTrue(justificativaNormalizada.contains(trechoNormalizado), "Justificativa inesperada: " + resultado.getJustificativa());
    }

    private String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
    }
}
