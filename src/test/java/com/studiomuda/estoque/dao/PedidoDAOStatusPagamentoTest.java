package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.model.Pedido;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PedidoDAOStatusPagamentoTest {

    private final PedidoDAO pedidoDAO = new PedidoDAO();

    @Test
    void deveNormalizarStatusPagamentoParaPendenteQuandoValorInvalido() {
        assertEquals("PENDENTE", pedidoDAO.normalizarStatusPagamento(null));
        assertEquals("PENDENTE", pedidoDAO.normalizarStatusPagamento(""));
        assertEquals("PENDENTE", pedidoDAO.normalizarStatusPagamento("QUALQUER_OUTRO"));
    }

    @Test
    void deveManterStatusPagoQuandoValorValido() {
        assertEquals("PAGO", pedidoDAO.normalizarStatusPagamento("PAGO"));
        assertEquals("PAGO", pedidoDAO.normalizarStatusPagamento("pago"));
    }

    @Test
    void deveInferirStatusPagoNoModoLegadoQuandoDataEntregaJaPassou() {
        Pedido pedido = new Pedido();
        pedido.setDataEntrega(Date.valueOf(LocalDate.now().minusDays(1)));

        assertEquals("PAGO", pedidoDAO.inferirStatusPagamentoLegado(pedido));
    }

    @Test
    void deveInferirStatusPendenteNoModoLegadoQuandoSemEntrega() {
        Pedido pedido = new Pedido();
        pedido.setDataEntrega(null);

        assertEquals("PENDENTE", pedidoDAO.inferirStatusPagamentoLegado(pedido));
    }
}
