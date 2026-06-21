package com.studiomuda.estoque.bdd;

import com.studiomuda.estoque.jpa.entity.LogCotacaoJpaEntity;
import com.studiomuda.estoque.jpa.entity.TabelaContingenciaJpaEntity;
import com.studiomuda.estoque.jpa.entity.TransportadoraJpaEntity;
import com.studiomuda.estoque.jpa.repository.ClienteJpaRepository;
import com.studiomuda.estoque.jpa.repository.LogCotacaoJpaRepository;
import com.studiomuda.estoque.jpa.repository.PedidoJpaRepository;
import com.studiomuda.estoque.jpa.repository.TabelaContingenciaJpaRepository;
import com.studiomuda.estoque.proxy.CotacaoFreteProxy;
import com.studiomuda.estoque.proxy.CotacaoResultado;
import com.studiomuda.estoque.proxy.LimiteCotacaoExcedidoException;
import com.studiomuda.estoque.proxy.ParametrosCotacao;
import com.studiomuda.estoque.proxy.TransportadoraApiClient;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CotacaoFreteStepDefinitions {

    private final TransportadoraApiClient api = mock(TransportadoraApiClient.class);
    private final LogCotacaoJpaRepository logs = mock(LogCotacaoJpaRepository.class);
    private final TabelaContingenciaJpaRepository contingencias = mock(TabelaContingenciaJpaRepository.class);
    private final CotacaoFreteProxy proxy = new CotacaoFreteProxy(
            api, logs, contingencias, mock(ClienteJpaRepository.class), mock(PedidoJpaRepository.class));

    private ParametrosCotacao parametros;
    private CotacaoResultado resultado;
    private RuntimeException erro;

    @Dado("uma cotacao de frete valida")
    public void umaCotacaoDeFreteValida() {
        parametros = new ParametrosCotacao();
        parametros.setCepDestino("60000-000");
        parametros.setPeso(new BigDecimal("2"));
        parametros.setComprimento(new BigDecimal("20"));
        parametros.setLargura(new BigDecimal("15"));
        parametros.setAltura(new BigDecimal("10"));
        when(logs.contarCotacoesExternas(eq(7), any())).thenReturn(0L);
        when(logs.buscarCacheRecente(anyString(), any())).thenReturn(Collections.emptyList());
        when(logs.save(any())).thenAnswer(invocacao -> {
            LogCotacaoJpaEntity log = invocacao.getArgument(0);
            log.setId(1);
            return log;
        });
    }

    @E("a integracao externa retorna o valor {double}")
    public void aIntegracaoExternaRetornaOValor(double valor) {
        TransportadoraJpaEntity transportadora = new TransportadoraJpaEntity();
        transportadora.setNome("Transportadora BDD");
        when(api.cotar(any(), eq(7))).thenReturn(new CotacaoResultado(
                BigDecimal.valueOf(valor).setScale(2), "API", transportadora, null));
    }

    @E("a integracao externa esta indisponivel")
    public void aIntegracaoExternaEstaIndisponivel() {
        when(api.cotar(any(), anyInt())).thenThrow(new IllegalStateException("Integração indisponível"));
    }

    @E("existe uma faixa de contingencia no valor {double}")
    public void existeUmaFaixaDeContingenciaNoValor(double valor) {
        TabelaContingenciaJpaEntity faixa = new TabelaContingenciaJpaEntity();
        faixa.setValorFrete(BigDecimal.valueOf(valor).setScale(2));
        when(contingencias.buscarAplicaveis(eq("60000000"), any()))
                .thenReturn(Collections.singletonList(faixa));
    }

    @E("o usuario ja realizou {int} cotacoes externas na ultima hora")
    public void oUsuarioJaRealizouCotacoesExternasNaUltimaHora(int quantidade) {
        when(logs.contarCotacoesExternas(eq(7), any())).thenReturn((long) quantidade);
    }

    @Quando("solicito a cotacao de frete")
    public void solicitoACotacaoDeFrete() {
        try {
            resultado = proxy.cotar(parametros, 7);
        } catch (RuntimeException e) {
            erro = e;
        }
    }

    @Entao("a origem da cotacao deve ser {string}")
    public void aOrigemDaCotacaoDeveSer(String origem) {
        assertEquals(origem, resultado.getOrigem());
    }

    @E("o valor cotado deve ser {double}")
    public void oValorCotadoDeveSer(double valor) {
        assertEquals(0, BigDecimal.valueOf(valor).compareTo(resultado.getValor()));
    }

    @Entao("a cotacao deve ser bloqueada por limite")
    public void aCotacaoDeveSerBloqueadaPorLimite() {
        assertTrue(erro instanceof LimiteCotacaoExcedidoException);
    }
}
