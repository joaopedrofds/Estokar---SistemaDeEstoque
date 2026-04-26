package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.dao.ClienteDAO;
import com.studiomuda.estoque.dao.CupomDAO;
import com.studiomuda.estoque.dao.FuncionarioDAO;
import com.studiomuda.estoque.dao.ItemPedidoDAO;
import com.studiomuda.estoque.dao.PedidoDAO;
import com.studiomuda.estoque.dao.ProdutoDAO;
import com.studiomuda.estoque.model.Cliente;
import com.studiomuda.estoque.model.Cupom;
import com.studiomuda.estoque.model.Funcionario;
import com.studiomuda.estoque.model.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
class PedidoControllerInadimplenciaTest {

    private FakePedidoDAO pedidoDAO;
    private FakeClienteDAO clienteDAO;
    private PedidoController controller;

    @BeforeEach
    void setUp() {
        this.pedidoDAO = new FakePedidoDAO();
        this.clienteDAO = new FakeClienteDAO();
        this.controller = new PedidoController(
                pedidoDAO,
                new ItemPedidoDAO(),
                clienteDAO,
                new ProdutoDAO(),
                new FakeFuncionarioDAO(),
                new FakeCupomDAO()
        );
    }

    @Test
    void deveBloquearNovoPedidoQuandoClienteInadimplente() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(0);
        pedido.setClienteId(10);
        pedido.setFuncionarioId(3);
        pedidoDAO.inadimplenciaInfo = new PedidoDAO.InadimplenciaInfo(true, 24, LocalDate.now().minusDays(60), 60);

        Model model = new ExtendedModelMap();

        String view = controller.salvarPedido(pedido, "2026-04-26", null, null, null, model);

        assertEquals("pedidos/form", view);
        assertTrue(model.containsAttribute("mensagemErro"));
        assertTrue(String.valueOf(model.getAttribute("mensagemErro")).contains("60 dias"));
        assertEquals(10, clienteDAO.bloqueadoClienteId);
        assertEquals(10, pedidoDAO.alertaClienteId);
        assertEquals(24, pedidoDAO.alertaPedidoId);
        assertEquals(60, pedidoDAO.alertaDiasAtraso);
        assertTrue(pedidoDAO.alertaMensagem.contains("inadimplência"));
        assertTrue(pedidoDAO.pedidosInseridos.isEmpty());
    }

    @Test
    void devePermitirNovoPedidoQuandoClienteNaoEstaInadimplente() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(0);
        pedido.setClienteId(11);
        pedido.setFuncionarioId(4);
        pedidoDAO.inadimplenciaInfo = new PedidoDAO.InadimplenciaInfo(false, null, null, 0);

        Model model = new ExtendedModelMap();
        String view = controller.salvarPedido(pedido, "2026-04-26", null, null, null, model);

        assertEquals("redirect:/pedidos/itens/123", view);
        assertEquals(1, pedidoDAO.pedidosInseridos.size());
        assertEquals(-1, clienteDAO.bloqueadoClienteId);
    }

    private static class FakePedidoDAO extends PedidoDAO {
        private com.studiomuda.estoque.dao.PedidoDAO.InadimplenciaInfo inadimplenciaInfo =
                new com.studiomuda.estoque.dao.PedidoDAO.InadimplenciaInfo(false, null, null, 0);
        private final List<Pedido> pedidosInseridos = new ArrayList<>();
        private int alertaClienteId = -1;
        private int alertaPedidoId = -1;
        private int alertaDiasAtraso = -1;
        private String alertaMensagem = "";

        @Override
        public com.studiomuda.estoque.dao.PedidoDAO.InadimplenciaInfo verificarInadimplenciaCliente(
                int clienteId, int diasLimite) throws java.sql.SQLException {
            return inadimplenciaInfo;
        }

        @Override
        public void inserir(Pedido pedido) throws java.sql.SQLException {
            pedido.setId(123);
            pedidosInseridos.add(pedido);
        }

        @Override
        public void registrarAlertaFinanceiro(int clienteId, Integer pedidoId, int diasAtraso, String mensagem)
                throws java.sql.SQLException {
            this.alertaClienteId = clienteId;
            this.alertaPedidoId = pedidoId != null ? pedidoId : -1;
            this.alertaDiasAtraso = diasAtraso;
            this.alertaMensagem = mensagem;
        }
    }

    private static class FakeClienteDAO extends ClienteDAO {
        private int bloqueadoClienteId = -1;

        @Override
        public void bloquearPorInadimplencia(int id) {
            this.bloqueadoClienteId = id;
        }

        @Override
        public List<Cliente> listarAtivos() {
            return Collections.emptyList();
        }
    }

    private static class FakeFuncionarioDAO extends FuncionarioDAO {
        @Override
        public List<Funcionario> listar() throws SQLException {
            return Collections.emptyList();
        }
    }

    private static class FakeCupomDAO extends CupomDAO {
        @Override
        public List<Cupom> listar() throws SQLException {
            return Collections.emptyList();
        }
    }
}
