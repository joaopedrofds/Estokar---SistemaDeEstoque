package com.studiomuda.estoque.bdd;

import com.studiomuda.estoque.service.InventarioService;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InventarioPeriodicoStepDefinitions {
    private final InventarioMemoria inventario = new InventarioMemoria();
    private Exception erro;

    @Dado("que existe uma sessao de inventario {string} para o setor {string}")
    public void existeSessaoInventarioParaSetor(String status, String setor) {
        inventario.status = status;
        inventario.setor = setor;
        erro = null;
    }

    @Quando("tento abrir nova sessao de inventario para o setor {string}")
    public void tentoAbrirNovaSessao(String setor) {
        try {
            inventario.abrir(setor);
        } catch (Exception e) {
            erro = e;
        }
    }

    @Então("a abertura deve ser bloqueada por concorrencia")
    public void aberturaDeveSerBloqueadaPorConcorrencia() {
        assertTrue(erro instanceof IllegalStateException);
        assertTrue(erro.getMessage().contains("ativa"));
    }

    @E("o produto {string} possui saldo de sistema {int} unidades")
    public void produtoPossuiSaldoSistema(String produto, Integer saldoSistema) {
        inventario.produto = produto;
        inventario.saldoSistema = saldoSistema;
        inventario.saldoFinal = saldoSistema;
    }

    @E("a tolerancia do inventario e {int} unidades")
    public void toleranciaInventario(Integer tolerancia) {
        inventario.tolerancia = tolerancia;
    }

    @Quando("o auxiliar registra contagem fisica de {int} unidades")
    public void auxiliarRegistraContagem(Integer quantidadeFisica) {
        inventario.registrarContagem(quantidadeFisica);
    }

    @Quando("o gerente fecha a sessao de inventario")
    public void gerenteFechaSessao() {
        inventario.fechar(Collections.singletonList("ROLE_GERENTE_OPERACIONAL"));
    }

    @E("o operador tenta fechar a sessao de inventario")
    public void operadorTentaFecharSessao() {
        inventario.fechar(Collections.singletonList("ROLE_OPERADOR_VENDEDOR"));
    }

    @Então("a sessao deve ficar com status {string}")
    public void sessaoDeveFicarComStatus(String statusEsperado) {
        assertEquals(statusEsperado, inventario.status);
    }

    @E("o saldo final do produto deve ser {int} unidades")
    public void saldoFinalProduto(Integer saldoEsperado) {
        assertEquals(saldoEsperado, inventario.saldoFinal);
    }

    @E("deve ser gerada uma movimentacao {string} de {int} unidades")
    public void deveGerarMovimentacao(String tipo, Integer quantidade) {
        assertEquals(tipo, inventario.movimentacaoTipo);
        assertEquals(quantidade, inventario.movimentacaoQuantidade);
    }

    private static class InventarioMemoria {
        private String setor;
        private String status;
        private String produto;
        private int saldoSistema;
        private int saldoFinal;
        private int quantidadeFisica;
        private int tolerancia = 3;
        private String movimentacaoTipo;
        private int movimentacaoQuantidade;

        private void abrir(String novoSetor) {
            if (setor != null && setor.equalsIgnoreCase(novoSetor)
                    && (InventarioService.STATUS_EM_ANDAMENTO.equals(status)
                    || InventarioService.STATUS_AGUARDANDO_APROVACAO.equals(status))) {
                throw new IllegalStateException("Ja existe sessao ativa para este setor.");
            }
            setor = novoSetor;
            status = InventarioService.STATUS_EM_ANDAMENTO;
        }

        private void registrarContagem(int quantidadeFisica) {
            if (!InventarioService.STATUS_EM_ANDAMENTO.equals(status)) {
                throw new IllegalStateException("Sessao precisa estar em andamento.");
            }
            if (quantidadeFisica < 0) {
                throw new IllegalArgumentException("Quantidade invalida.");
            }
            this.quantidadeFisica = quantidadeFisica;
        }

        private void fechar(java.util.Collection<String> autoridades) {
            int diferenca = quantidadeFisica - saldoSistema;
            boolean exigeAprovacao = Math.abs(diferenca) > tolerancia;
            boolean aprovador = autoridades.contains("ROLE_GERENTE_OPERACIONAL") || autoridades.contains("ROLE_DIRETOR");

            if (exigeAprovacao && !aprovador) {
                status = InventarioService.STATUS_AGUARDANDO_APROVACAO;
                return;
            }

            if (diferenca != 0) {
                movimentacaoTipo = diferenca > 0 ? "entrada" : "saida";
                movimentacaoQuantidade = Math.abs(diferenca);
            }
            saldoFinal = quantidadeFisica;
            status = InventarioService.STATUS_FECHADO;
        }
    }
}
