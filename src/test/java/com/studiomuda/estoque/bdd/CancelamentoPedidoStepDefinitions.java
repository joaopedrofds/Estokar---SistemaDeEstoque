package com.studiomuda.estoque.bdd;

import com.studiomuda.estoque.dao.ItemPedidoDAO;
import com.studiomuda.estoque.dao.PedidoDAO;
import com.studiomuda.estoque.model.ItemPedido;
import com.studiomuda.estoque.model.Pedido;
import com.studiomuda.estoque.service.PedidoService;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CancelamentoPedidoStepDefinitions {
    private final FakePedidoDAO pedidoDAO = new FakePedidoDAO();
    private final FakeItemPedidoDAO itemPedidoDAO = new FakeItemPedidoDAO();
    private final PedidoService pedidoService = new PedidoService(pedidoDAO, itemPedidoDAO);
    private PedidoService.ResultadoCancelamento resultado;
    private Exception erro;

    @Dado("que existe um pedido {string} com {int} itens e {int} unidades no total")
    public void existePedidoComItens(String status, Integer quantidadeItens, Integer quantidadeTotal) {
        Pedido pedido = new Pedido();
        pedido.setId(1);
        pedido.setStatus(status);
        pedidoDAO.pedido = pedido;
        itemPedidoDAO.itens = distribuirItens(quantidadeItens, quantidadeTotal);
        pedidoDAO.quantidadeEstornada = 0;
        pedidoDAO.statusAtualizado = status;
        resultado = null;
        erro = null;
    }

    @E("o limite de cancelamento sem aprovacao e {int} unidades")
    public void limiteCancelamentoSemAprovacao(Integer limite) {
        pedidoDAO.limiteQuantidade = limite;
    }

    @Quando("o solicitante {string} cancela o pedido com justificativa {string}")
    public void solicitanteCancelaPedido(String nomeSolicitante, String justificativa) {
        executarCancelamento(nomeSolicitante, justificativa, Collections.singletonList("ROLE_OPERADOR_VENDEDOR"));
    }

    @Quando("o gerente {string} aprova o cancelamento com justificativa {string}")
    public void gerenteAprovaCancelamento(String nomeGerente, String justificativa) {
        executarCancelamento(nomeGerente, justificativa, Collections.singletonList("ROLE_GERENTE_OPERACIONAL"));
    }

    private void executarCancelamento(String nomeSolicitante, String justificativa, Collection<String> autoridades) {
        try {
            resultado = pedidoService.cancelarPedido(
                    1,
                    new PedidoService.UsuarioOperacao(3, nomeSolicitante),
                    justificativa,
                    autoridades
            );
        } catch (Exception e) {
            erro = e;
        }
    }

    @Então("o pedido deve ficar com status {string}")
    public void pedidoDeveFicarComStatus(String statusEsperado) {
        assertEquals(statusEsperado, pedidoDAO.statusAtualizado);
        assertEquals(statusEsperado, resultado.getStatus());
    }

    @E("o estoque deve receber estorno de {int} unidades")
    public void estoqueDeveReceberEstorno(Integer quantidadeEsperada) {
        assertEquals(quantidadeEsperada, pedidoDAO.quantidadeEstornada);
    }

    @Então("a tentativa deve ser bloqueada por idempotencia")
    public void tentativaDeveSerBloqueadaPorIdempotencia() {
        assertTrue(erro instanceof IllegalStateException);
        assertTrue(erro.getMessage().contains("idempotencia"));
    }

    private List<ItemPedido> distribuirItens(int quantidadeItens, int quantidadeTotal) {
        List<ItemPedido> itens = new ArrayList<>();
        int restante = quantidadeTotal;
        for (int i = 1; i <= quantidadeItens; i++) {
            int quantidade = i == quantidadeItens ? restante : Math.max(1, quantidadeTotal / quantidadeItens);
            restante -= quantidade;
            ItemPedido item = new ItemPedido(i, 1, i, quantidade);
            itens.add(item);
        }
        return itens;
    }

    private static class FakePedidoDAO extends PedidoDAO {
        private Pedido pedido;
        private int limiteQuantidade = 10;
        private int quantidadeEstornada;
        private String statusAtualizado;

        @Override
        public Pedido buscarPorId(int id) {
            return pedido;
        }

        @Override
        public int buscarLimiteQuantidadeCancelamento() {
            return limiteQuantidade;
        }

        @Override
        public void registrarCancelamentoPendente(int pedidoId,
                                                  int solicitanteId,
                                                  String solicitanteNome,
                                                  String justificativa) {
            statusAtualizado = STATUS_PEDIDO_CANCELAMENTO_PENDENTE;
        }

        @Override
        public void cancelarComEstorno(int pedidoId,
                                       List<ItemPedido> itens,
                                       int solicitanteId,
                                       String solicitanteNome,
                                       String justificativa,
                                       Integer aprovadorId,
                                       String aprovadorNome) {
            statusAtualizado = STATUS_PEDIDO_CANCELADO;
            for (ItemPedido item : itens) {
                quantidadeEstornada += item.getQuantidade();
            }
        }
    }

    private static class FakeItemPedidoDAO extends ItemPedidoDAO {
        private List<ItemPedido> itens = new ArrayList<>();

        @Override
        public List<ItemPedido> listarPorPedido(int pedidoId) throws SQLException {
            return itens;
        }
    }
}
