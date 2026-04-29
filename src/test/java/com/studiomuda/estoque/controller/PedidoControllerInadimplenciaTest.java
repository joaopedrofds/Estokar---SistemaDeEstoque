package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.application.cliente.ListarClientesUseCase;
import com.studiomuda.estoque.application.cliente.ports.PedidoQueryPort;
import com.studiomuda.estoque.application.cupom.ListarCuponsUseCase;
import com.studiomuda.estoque.application.estoque.RegistrarMovimentacaoUseCase;
import com.studiomuda.estoque.application.funcionario.ListarFuncionariosUseCase;
import com.studiomuda.estoque.application.pedido.AdicionarItemUseCase;
import com.studiomuda.estoque.application.pedido.AtualizarPedidoUseCase;
import com.studiomuda.estoque.application.pedido.BuscarPedidoUseCase;
import com.studiomuda.estoque.application.pedido.CriarPedidoUseCase;
import com.studiomuda.estoque.application.pedido.ExcluirPedidoUseCase;
import com.studiomuda.estoque.application.pedido.ListarItensPedidoUseCase;
import com.studiomuda.estoque.application.pedido.ListarPedidosUseCase;
import com.studiomuda.estoque.application.pedido.RemoverItemUseCase;
import com.studiomuda.estoque.application.produto.ListarProdutosUseCase;
import com.studiomuda.estoque.domain.cliente.Cliente;
import com.studiomuda.estoque.domain.cliente.ClienteRepository;
import com.studiomuda.estoque.domain.cliente.CpfCnpj;
import com.studiomuda.estoque.domain.cliente.Endereco;
import com.studiomuda.estoque.domain.cliente.TipoPessoa;
import com.studiomuda.estoque.domain.cupom.Cupom;
import com.studiomuda.estoque.domain.cupom.CupomRepository;
import com.studiomuda.estoque.domain.estoque.MovimentacaoEstoque;
import com.studiomuda.estoque.domain.estoque.MovimentacaoEstoqueComProduto;
import com.studiomuda.estoque.domain.estoque.MovimentacaoEstoqueRepository;
import com.studiomuda.estoque.domain.funcionario.Cargo;
import com.studiomuda.estoque.domain.funcionario.Cpf;
import com.studiomuda.estoque.domain.funcionario.Funcionario;
import com.studiomuda.estoque.domain.funcionario.FuncionarioRepository;
import com.studiomuda.estoque.domain.pedido.AnaliseInadimplencia;
import com.studiomuda.estoque.domain.pedido.ItemPedido;
import com.studiomuda.estoque.domain.pedido.ItemPedidoComProduto;
import com.studiomuda.estoque.domain.pedido.ItemPedidoRepository;
import com.studiomuda.estoque.domain.pedido.Pedido;
import com.studiomuda.estoque.domain.pedido.PedidoComJoins;
import com.studiomuda.estoque.domain.pedido.PedidoRepository;
import com.studiomuda.estoque.domain.produto.Produto;
import com.studiomuda.estoque.domain.produto.ProdutoRepository;
import com.studiomuda.estoque.domain.produto.StatusEstoque;
import com.studiomuda.estoque.domain.produto.TipoProduto;
import com.studiomuda.estoque.presentation.web.pedido.PedidoForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PedidoControllerInadimplenciaTest {

    private FakePedidoRepository pedidoRepository;
    private FakeClienteRepository clienteRepository;
    private PedidoController controller;

    @BeforeEach
    void setUp() {
        this.pedidoRepository = new FakePedidoRepository();
        this.clienteRepository = new FakeClienteRepository();

        FakeCupomRepository cupomRepository = new FakeCupomRepository();
        FakeItemPedidoRepository itemPedidoRepository = new FakeItemPedidoRepository();
        FakeProdutoRepository produtoRepository = new FakeProdutoRepository();
        FakeMovimentacaoRepository movimentacaoRepository = new FakeMovimentacaoRepository();
        FakeFuncionarioRepository funcionarioRepository = new FakeFuncionarioRepository();

        RegistrarMovimentacaoUseCase registrarMovimentacao = new RegistrarMovimentacaoUseCase(
                movimentacaoRepository, produtoRepository);

        this.controller = new PedidoController(
                new CriarPedidoUseCase(pedidoRepository, clienteRepository, cupomRepository),
                new AtualizarPedidoUseCase(pedidoRepository, cupomRepository),
                new BuscarPedidoUseCase(pedidoRepository),
                new ExcluirPedidoUseCase(pedidoRepository, itemPedidoRepository),
                new ListarPedidosUseCase(pedidoRepository, clienteRepository),
                new ListarItensPedidoUseCase(itemPedidoRepository),
                new AdicionarItemUseCase(itemPedidoRepository, produtoRepository, registrarMovimentacao),
                new RemoverItemUseCase(itemPedidoRepository, registrarMovimentacao),
                new ListarClientesUseCase(clienteRepository, new FakePedidoQueryPort()),
                new ListarFuncionariosUseCase(funcionarioRepository),
                new ListarCuponsUseCase(cupomRepository),
                new ListarProdutosUseCase(produtoRepository)
        );
    }

    @Test
    void deveBloquearNovoPedidoQuandoClienteInadimplente() {
        PedidoForm pedido = new PedidoForm();
        pedido.setId(0);
        pedido.setClienteId(10);
        pedido.setFuncionarioId(3);
        pedidoRepository.inadimplenciaInfo = AnaliseInadimplencia.bloqueado(24, LocalDate.now().minusDays(60), 60);

        Model model = new ExtendedModelMap();

        String view = controller.salvarPedido(pedido, "2026-04-26", null, null, null, model);

        assertEquals("pedidos/form", view);
        assertTrue(model.containsAttribute("mensagemErro"));
        assertTrue(String.valueOf(model.getAttribute("mensagemErro")).contains("60 dias"));
        assertEquals(10, clienteRepository.bloqueadoClienteId);
        assertEquals(10, pedidoRepository.alertaClienteId);
        assertEquals(24, pedidoRepository.alertaPedidoId);
        assertEquals(60, pedidoRepository.alertaDiasAtraso);
        assertTrue(pedidoRepository.alertaMensagem.contains("inadimplência"));
        assertTrue(pedidoRepository.pedidosInseridos.isEmpty());
    }

    @Test
    void devePermitirNovoPedidoQuandoClienteNaoEstaInadimplente() {
        PedidoForm pedido = new PedidoForm();
        pedido.setId(0);
        pedido.setClienteId(11);
        pedido.setFuncionarioId(4);
        pedidoRepository.inadimplenciaInfo = AnaliseInadimplencia.naoBloqueado();

        Model model = new ExtendedModelMap();
        String view = controller.salvarPedido(pedido, "2026-04-26", null, null, null, model);

        assertEquals("redirect:/pedidos/itens/123", view);
        assertEquals(1, pedidoRepository.pedidosInseridos.size());
        assertEquals(-1, clienteRepository.bloqueadoClienteId);
    }

    private static class FakePedidoRepository implements PedidoRepository {
        private AnaliseInadimplencia inadimplenciaInfo = AnaliseInadimplencia.naoBloqueado();
        private final List<Pedido> pedidosInseridos = new ArrayList<>();
        private int alertaClienteId = -1;
        private int alertaPedidoId = -1;
        private int alertaDiasAtraso = -1;
        private String alertaMensagem = "";

        @Override
        public Pedido salvar(Pedido pedido) {
            Pedido salvo = new Pedido(
                    123,
                    pedido.dataRequisicao(),
                    pedido.dataEntrega(),
                    pedido.clienteId(),
                    pedido.funcionarioId(),
                    pedido.cupomId(),
                    pedido.valorDesconto(),
                    pedido.status(),
                    pedido.statusPagamento(),
                    pedido.dataPagamento()
            );
            pedidosInseridos.add(salvo);
            return salvo;
        }

        @Override
        public void atualizar(Pedido pedido) {}

        @Override
        public Optional<Pedido> buscarPorId(int id) {
            return pedidosInseridos.stream().filter(p -> p.id() == id).findFirst();
        }

        @Override
        public List<PedidoComJoins> listarTodos() {
            return Collections.emptyList();
        }

        @Override
        public List<PedidoComJoins> listarPorCliente(int clienteId) {
            return Collections.emptyList();
        }

        @Override
        public List<PedidoComJoins> buscarComFiltros(String cpfCnpj, String status, LocalDate dataInicio,
                                                     LocalDate dataFim, Integer funcionarioId, Integer clienteId,
                                                     Integer cupomId) {
            return Collections.emptyList();
        }

        @Override
        public void remover(int id) {}

        @Override
        public AnaliseInadimplencia verificarInadimplenciaCliente(int clienteId, int diasLimite) {
            return inadimplenciaInfo;
        }

        @Override
        public void registrarAlertaFinanceiro(int clienteId, Integer pedidoId, int diasAtraso, String mensagem) {
            this.alertaClienteId = clienteId;
            this.alertaPedidoId = pedidoId != null ? pedidoId : -1;
            this.alertaDiasAtraso = diasAtraso;
            this.alertaMensagem = mensagem;
        }

        @Override
        public List<LocalDate> listarDatasCompraPorCliente(int clienteId) {
            return Collections.emptyList();
        }
    }

    private static class FakeClienteRepository implements ClienteRepository {
        private int bloqueadoClienteId = -1;

        @Override
        public Cliente salvar(Cliente cliente) {
            return cliente;
        }

        @Override
        public void atualizar(Cliente cliente) {
            this.bloqueadoClienteId = cliente.id();
        }

        @Override
        public Optional<Cliente> buscarPorId(int id) {
            return Optional.of(novoCliente(id));
        }

        @Override
        public Optional<Cliente> buscarPorCpfCnpj(CpfCnpj cpfCnpj) {
            return Optional.empty();
        }

        @Override
        public List<Cliente> listarTodos() {
            return Collections.emptyList();
        }

        @Override
        public List<Cliente> listarAtivos() { return Collections.emptyList(); }

        @Override
        public List<Cliente> listarInativos() { return Collections.emptyList(); }

        @Override
        public List<Cliente> buscarComFiltros(String nome, TipoPessoa tipo, Boolean ativo) { return Collections.emptyList(); }

        @Override
        public void desativar(int id) {}

        private Cliente novoCliente(int id) {
            return new Cliente(
                    id,
                    "Cliente Teste",
                    CpfCnpj.of("12345678901", TipoPessoa.PF),
                    "11999999999",
                    "cliente@teste.com",
                    new Endereco("01001000", "Rua A", "10", "Centro", "SP", "SP"),
                    true,
                    LocalDate.of(1990, 1, 1)
            );
        }
    }

    private static class FakeFuncionarioRepository implements FuncionarioRepository {
        @Override
        public Funcionario salvar(Funcionario funcionario) { return funcionario; }

        @Override
        public void atualizar(Funcionario funcionario) {}

        @Override
        public Optional<Funcionario> buscarPorId(int id) { return Optional.empty(); }

        @Override
        public Optional<Funcionario> buscarPorCpf(Cpf cpf) { return Optional.empty(); }

        @Override
        public List<Funcionario> listarTodos() {
            return Collections.emptyList();
        }

        @Override
        public List<Funcionario> buscarComFiltros(String nome, Cargo cargo, Boolean ativo) {
            return Collections.emptyList();
        }

        @Override
        public void desativar(int id) {}
    }

    private static class FakeCupomRepository implements CupomRepository {
        @Override
        public Cupom salvar(Cupom cupom) { return cupom; }

        @Override
        public void atualizar(Cupom cupom) {}

        @Override
        public Optional<Cupom> buscarPorId(int id) { return Optional.empty(); }

        @Override
        public Optional<Cupom> buscarPorCodigo(String codigo) { return Optional.empty(); }

        @Override
        public List<Cupom> listarTodos() {
            return Collections.emptyList();
        }

        @Override
        public List<Cupom> listarValidos() {
            return Collections.emptyList();
        }

        @Override
        public List<Cupom> buscarComFiltros(String codigo, String status) {
            return Collections.emptyList();
        }

        @Override
        public void remover(int id) {}
    }

    private static class FakeItemPedidoRepository implements ItemPedidoRepository {
        @Override
        public ItemPedido salvar(ItemPedido item) { return item; }

        @Override
        public Optional<ItemPedido> buscarPorId(int id) { return Optional.empty(); }

        @Override
        public List<ItemPedidoComProduto> listarPorPedido(int pedidoId) { return Collections.emptyList(); }

        @Override
        public void remover(int id) {}

        @Override
        public void removerPorPedido(int pedidoId) {}
    }

    private static class FakeProdutoRepository implements ProdutoRepository {
        @Override
        public Produto salvar(Produto produto) { return produto; }

        @Override
        public void atualizar(Produto produto) {}

        @Override
        public Optional<Produto> buscarPorId(int id) {
            return Optional.of(new Produto(id, "Produto", "Desc", TipoProduto.PRODUTO, 999, 10.0));
        }

        @Override
        public List<Produto> listarTodos() { return Collections.emptyList(); }

        @Override
        public List<Produto> buscarComFiltros(String nome, TipoProduto tipo, StatusEstoque estoque) {
            return Collections.emptyList();
        }

        @Override
        public void remover(int id) {}
    }

    private static class FakeMovimentacaoRepository implements MovimentacaoEstoqueRepository {
        @Override
        public MovimentacaoEstoque registrar(MovimentacaoEstoque movimentacao) { return movimentacao; }

        @Override
        public void removerComEstorno(MovimentacaoEstoque movimentacao) {}

        @Override
        public Optional<MovimentacaoEstoque> buscarPorId(int id) { return Optional.empty(); }

        @Override
        public void atualizarMetadados(int id, String motivo, LocalDate data) {}

        @Override
        public List<MovimentacaoEstoqueComProduto> listarTodas() { return Collections.emptyList(); }

        @Override
        public List<MovimentacaoEstoqueComProduto> buscarComFiltros(String produtoNome, String tipo,
                                                                     LocalDate dataInicio, LocalDate dataFim) {
            return Collections.emptyList();
        }
    }

    private static class FakePedidoQueryPort implements PedidoQueryPort {
        @Override
        public List<LocalDate> listarDatasCompraPorCliente(int clienteId) {
            return Collections.emptyList();
        }
    }
}
