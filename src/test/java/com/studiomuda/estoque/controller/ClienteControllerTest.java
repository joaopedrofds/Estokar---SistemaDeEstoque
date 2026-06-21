package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.model.Cliente;
import com.studiomuda.estoque.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClienteControllerTest {

    private ClienteController controller;
    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        clienteService = mock(ClienteService.class);
        controller = new ClienteController(clienteService);
    }

    @Test
    void listarClientesSemFiltrosUsaListagemCompleta() throws Exception {
        Cliente ana = cliente(1, "Ana");
        List<Cliente> clientes = List.of(ana);
        when(clienteService.listar(null, null, null)).thenReturn(clientes);
        Model model = new ExtendedModelMap();

        String view = controller.listarClientes(model, null, null, null);

        assertEquals("clientes/lista", view);
        assertSame(clientes, model.getAttribute("clientes"));
        assertEquals(null, model.getAttribute("filtroNome"));
        assertEquals(null, model.getAttribute("filtroTipo"));
        assertEquals(null, model.getAttribute("filtroStatus"));
    }

    @Test
    void listarClientesComFiltrosPreservaParametrosNoModel() throws Exception {
        Cliente joao = cliente(2, "João");
        List<Cliente> clientes = List.of(joao);
        when(clienteService.listar("jo", "PF", "ativo")).thenReturn(clientes);
        Model model = new ExtendedModelMap();

        String view = controller.listarClientes(model, "jo", "PF", "ativo");

        assertEquals("clientes/lista", view);
        assertSame(clientes, model.getAttribute("clientes"));
        assertEquals("jo", model.getAttribute("filtroNome"));
        assertEquals("PF", model.getAttribute("filtroTipo"));
        assertEquals("ativo", model.getAttribute("filtroStatus"));
    }

    @Test
    void listarClientesApiRetornaListaDoService() throws Exception {
        Cliente maria = cliente(3, "Maria");
        List<Cliente> clientes = List.of(maria);
        when(clienteService.listar(null, null, null)).thenReturn(clientes);

        ResponseEntity<?> response = controller.listarClientesApi();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(clientes, response.getBody());
    }

    @Test
    void listarClientesApiRetornaErro500QuandoServiceFalha() throws Exception {
        when(clienteService.listar(null, null, null)).thenThrow(new IllegalStateException("falha controlada"));

        ResponseEntity<?> response = controller.listarClientesApi();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        assertTrue(((Map<?, ?>) response.getBody()).get("erro").toString().contains("falha controlada"));
    }

    @Test
    void contarClientesUsaResultadoDoService() {
        when(clienteService.contar()).thenReturn(2L);

        ResponseEntity<?> response = new ClienteApiController(clienteService).contarClientes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2L, response.getBody());
    }

    @Test
    void salvarClienteRetornaErroDeValidacaoQuandoServiceRejeita() throws Exception {
        Cliente cliente = cliente(0, "");
        Model model = new ExtendedModelMap();
        when(clienteService.listarTiposDisponiveis()).thenReturn(List.of("PF", "PJ"));
        org.mockito.Mockito.doThrow(new IllegalArgumentException("O nome é obrigatório."))
                .when(clienteService).salvar(cliente);

        String view = controller.salvarCliente(cliente, model);

        assertEquals("clientes/form", view);
        assertEquals("O nome é obrigatório.", model.getAttribute("mensagemErro"));
        assertSame(cliente, model.getAttribute("cliente"));
    }

    @Test
    void salvarClienteApiRetornaBadRequestQuandoServiceRejeita() throws Exception {
        Cliente cliente = cliente(0, "Cliente");
        org.mockito.Mockito.doThrow(new IllegalArgumentException("E-mail em formato inválido."))
                .when(clienteService).salvar(cliente);

        ResponseEntity<?> response = controller.salvarClienteApi(cliente);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        assertTrue(((Map<?, ?>) response.getBody()).get("erro").toString().contains("E-mail em formato inválido."));
    }

    @Test
    void buscarClienteApiRetornaNotFoundQuandoClienteNaoExiste() {
        when(clienteService.buscarPorId(99)).thenReturn(null);

        ResponseEntity<?> response = controller.buscarClienteApi(99);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void editarClienteCarregaClienteNoModelQuandoEncontrado() {
        Cliente cliente = cliente(7, "Helena");
        when(clienteService.buscarPorId(7)).thenReturn(cliente);
        Model model = new ExtendedModelMap();

        String view = controller.editarCliente(7, model);

        assertEquals("clientes/form", view);
        assertSame(cliente, model.getAttribute("cliente"));
    }

    private Cliente cliente(int id, String nome) {
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setNome(nome);
        cliente.setTipo("PF");
        cliente.setCpfCnpj("12345678901");
        cliente.setTelefone("81999999999");
        cliente.setEmail("teste@teste.com");
        cliente.setAtivo(true);
        return cliente;
    }
}
