package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.application.cliente.ListarClientesUseCase;
import com.studiomuda.estoque.application.cliente.dto.ClienteComFrequencia;
import com.studiomuda.estoque.application.cupom.ListarCuponsUseCase;
import com.studiomuda.estoque.application.funcionario.ListarFuncionariosUseCase;
import com.studiomuda.estoque.application.pedido.AdicionarItemUseCase;
import com.studiomuda.estoque.application.pedido.AtualizarPedidoUseCase;
import com.studiomuda.estoque.application.pedido.BuscarPedidoUseCase;
import com.studiomuda.estoque.application.pedido.CriarPedidoUseCase;
import com.studiomuda.estoque.application.pedido.ExcluirPedidoUseCase;
import com.studiomuda.estoque.application.pedido.ListarItensPedidoUseCase;
import com.studiomuda.estoque.application.pedido.ListarPedidosUseCase;
import com.studiomuda.estoque.application.pedido.RemoverItemUseCase;
import com.studiomuda.estoque.application.pedido.dto.SalvarPedidoCommand;
import com.studiomuda.estoque.application.produto.ListarProdutosUseCase;
import com.studiomuda.estoque.domain.cliente.Cliente;
import com.studiomuda.estoque.domain.cupom.Cupom;
import com.studiomuda.estoque.domain.funcionario.Funcionario;
import com.studiomuda.estoque.domain.pedido.Pedido;
import com.studiomuda.estoque.domain.pedido.PedidoComJoins;
import com.studiomuda.estoque.domain.pedido.exceptions.ClienteInadimplenteException;
import com.studiomuda.estoque.presentation.web.cliente.ClienteView;
import com.studiomuda.estoque.presentation.web.cupom.CupomView;
import com.studiomuda.estoque.presentation.web.funcionario.FuncionarioView;
import com.studiomuda.estoque.presentation.web.pedido.ItemPedidoForm;
import com.studiomuda.estoque.presentation.web.pedido.ItemPedidoView;
import com.studiomuda.estoque.presentation.web.pedido.PedidoForm;
import com.studiomuda.estoque.presentation.web.pedido.PedidoView;
import com.studiomuda.estoque.presentation.web.produto.ProdutoView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final CriarPedidoUseCase criar;
    private final AtualizarPedidoUseCase atualizar;
    private final BuscarPedidoUseCase buscar;
    private final ExcluirPedidoUseCase excluir;
    private final ListarPedidosUseCase listarPedidos;
    private final ListarItensPedidoUseCase listarItens;
    private final AdicionarItemUseCase adicionarItem;
    private final RemoverItemUseCase removerItem;
    private final ListarClientesUseCase listarClientes;
    private final ListarFuncionariosUseCase listarFuncionarios;
    private final ListarCuponsUseCase listarCupons;
    private final ListarProdutosUseCase listarProdutos;

    public PedidoController(CriarPedidoUseCase criar,
                            AtualizarPedidoUseCase atualizar,
                            BuscarPedidoUseCase buscar,
                            ExcluirPedidoUseCase excluir,
                            ListarPedidosUseCase listarPedidos,
                            ListarItensPedidoUseCase listarItens,
                            AdicionarItemUseCase adicionarItem,
                            RemoverItemUseCase removerItem,
                            ListarClientesUseCase listarClientes,
                            ListarFuncionariosUseCase listarFuncionarios,
                            ListarCuponsUseCase listarCupons,
                            ListarProdutosUseCase listarProdutos) {
        this.criar = criar;
        this.atualizar = atualizar;
        this.buscar = buscar;
        this.excluir = excluir;
        this.listarPedidos = listarPedidos;
        this.listarItens = listarItens;
        this.adicionarItem = adicionarItem;
        this.removerItem = removerItem;
        this.listarClientes = listarClientes;
        this.listarFuncionarios = listarFuncionarios;
        this.listarCupons = listarCupons;
        this.listarProdutos = listarProdutos;
    }

    @GetMapping
    public String listarPedidos(@RequestParam(value = "cpfCnpj", required = false) String cpfCnpj, Model model) {
        try {
            if (cpfCnpj != null && !cpfCnpj.trim().isEmpty()) {
                ListarPedidosUseCase.ResultadoBuscaPorCpf resultado = listarPedidos.listarPorCpfCnpj(cpfCnpj);
                List<PedidoView> pedidos = resultado.pedidos().stream().map(PedidoView::new).collect(Collectors.toList());
                model.addAttribute("pedidos", pedidos);
                if (resultado.cliente() != null) {
                    model.addAttribute("clienteEncontrado", ClienteView.semFrequencia(resultado.cliente()));
                } else {
                    model.addAttribute("mensagemAviso", "Nenhum cliente encontrado com o CPF/CNPJ informado.");
                }
                model.addAttribute("cpfCnpj", cpfCnpj);
            } else {
                List<PedidoView> pedidos = listarPedidos.listarTodos().stream().map(PedidoView::new).collect(Collectors.toList());
                model.addAttribute("pedidos", pedidos);
            }
            return "pedidos/lista";
        } catch (RuntimeException e) {
            model.addAttribute("mensagemErro", "Erro ao listar pedidos: " + e.getMessage());
            return "erro";
        }
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<?> listarPedidosApi() {
        try {
            List<PedidoView> pedidos = listarPedidos.listarTodos().stream().map(PedidoView::new).collect(Collectors.toList());
            return ResponseEntity.ok(pedidos);
        } catch (RuntimeException e) {
            return erro("Erro ao listar pedidos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/novo")
    public String formNovoPedido(Model model) {
        PedidoForm form = new PedidoForm();
        form.setDataRequisicao(java.sql.Date.valueOf(LocalDate.now()));
        form.setStatusPagamento("PENDENTE");
        carregarDadosFormularioPedido(model, form);
        return "pedidos/form";
    }

    @PostMapping("/salvar")
    public String salvarPedido(@ModelAttribute("pedido") PedidoForm form,
                               @RequestParam(value = "dataRequisicaoStr", required = false) String dataRequisicaoStr,
                               @RequestParam(value = "dataEntregaStr", required = false) String dataEntregaStr,
                               @RequestParam(value = "dataPagamentoStr", required = false) String dataPagamentoStr,
                               @RequestParam(value = "cupomId", required = false) Integer cupomId,
                               Model model) {
        try {
            SalvarPedidoCommand command = toCommand(form, dataRequisicaoStr, dataEntregaStr, dataPagamentoStr, cupomId);
            int pedidoId;
            if (form.getId() == 0) {
                Pedido pedido = criar.executar(command);
                pedidoId = pedido.id();
            } else {
                atualizar.executar(command);
                pedidoId = form.getId();
            }
            return "redirect:/pedidos/itens/" + pedidoId;
        } catch (ClienteInadimplenteException e) {
            carregarDadosFormularioPedido(model, form);
            model.addAttribute("mensagemErro", e.getMessage());
            model.addAttribute("mensagemAviso", "Alerta enviado para o Gestor Financeiro.");
            return "pedidos/form";
        } catch (RuntimeException e) {
            carregarDadosFormularioPedido(model, form);
            model.addAttribute("mensagemErro", "Erro ao salvar pedido: " + e.getMessage());
            return "pedidos/form";
        }
    }

    @PostMapping("/api/salvar")
    @ResponseBody
    public ResponseEntity<?> salvarPedidoApi(@RequestBody PedidoForm form) {
        try {
            Integer cupomId = (form.getCupomId() != null && form.getCupomId() > 0) ? form.getCupomId() : null;
            LocalDate dataRequisicao = form.getDataRequisicao() != null ? form.getDataRequisicao().toLocalDate() : LocalDate.now();
            LocalDate dataEntrega = form.getDataEntrega() != null ? form.getDataEntrega().toLocalDate() : null;
            LocalDate dataPagamento = form.getDataPagamento() != null ? form.getDataPagamento().toLocalDate() : null;
            SalvarPedidoCommand command = form.toCommand(dataRequisicao, dataEntrega, dataPagamento, cupomId);

            Pedido pedido;
            if (form.getId() == 0) {
                pedido = criar.executar(command);
            } else {
                atualizar.executar(command);
                pedido = buscar.porId(form.getId()).orElse(null);
            }

            if (pedido == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(new PedidoView(pedido));
        } catch (ClienteInadimplenteException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("erro", e.getMessage());
            error.put("codigo", "CLIENTE_BLOQUEADO_INADIMPLENCIA");
            error.put("diasAtraso", e.analise() != null ? e.analise().diasAtraso() : null);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
        } catch (RuntimeException e) {
            return erro("Erro ao salvar pedido: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/editar/{id}")
    public String editarPedido(@PathVariable("id") int id, Model model) {
        Optional<Pedido> pedido = buscar.porId(id);
        if (pedido.isPresent()) {
            PedidoForm form = PedidoForm.desde(pedido.get());
            carregarDadosFormularioPedido(model, form);
            return "pedidos/form";
        }
        return "redirect:/pedidos";
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> buscarPedidoApi(@PathVariable("id") int id) {
        Optional<PedidoView> pedido = buscarPedidoCompleto(id);
        if (pedido.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<ItemPedidoView> itens = listarItens.porPedido(id).stream().map(ItemPedidoView::new).collect(Collectors.toList());
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", pedido.get().getId());
        payload.put("dataRequisicao", pedido.get().getDataRequisicao());
        payload.put("dataEntrega", pedido.get().getDataEntrega());
        payload.put("clienteId", pedido.get().getClienteId());
        payload.put("clienteNome", pedido.get().getClienteNome());
        payload.put("cupomId", pedido.get().getCupomId());
        payload.put("funcionarioId", pedido.get().getFuncionarioId());
        payload.put("funcionarioNome", pedido.get().getFuncionarioNome());
        payload.put("funcionarioCargo", pedido.get().getFuncionarioCargo());
        payload.put("valorDesconto", pedido.get().getValorDesconto());
        payload.put("statusPagamento", pedido.get().getStatusPagamento());
        payload.put("dataPagamento", pedido.get().getDataPagamento());
        payload.put("diasAtrasoPagamento", pedido.get().getDiasAtrasoPagamento());
        payload.put("itens", itens);
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/excluir/{id}")
    public String excluirPedido(@PathVariable("id") int id) {
        try {
            excluir.executar(id);
            return "redirect:/pedidos";
        } catch (RuntimeException e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> excluirPedidoApi(@PathVariable("id") int id) {
        try {
            excluir.executar(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Pedido excluído com sucesso");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return erro("Erro ao excluir pedido: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/itens/{pedidoId}")
    public String listarItensPedido(@PathVariable("pedidoId") int pedidoId,
                                    @RequestParam(required = false) String erro,
                                    Model model) {
        try {
            Optional<PedidoView> pedido = buscarPedidoCompleto(pedidoId);
            if (pedido.isEmpty()) {
                return "redirect:/pedidos";
            }

            List<ItemPedidoView> itens = listarItens.porPedido(pedidoId).stream().map(ItemPedidoView::new).collect(Collectors.toList());
            double valorTotal = itens.stream().mapToDouble(ItemPedidoView::getSubtotal).sum();
            double valorComDesconto = Math.max(0.0, valorTotal - pedido.get().getValorDesconto());

            ItemPedidoForm novoItem = new ItemPedidoForm();
            novoItem.setPedidoId(pedidoId);

            model.addAttribute("pedido", pedido.get());
            model.addAttribute("itens", itens);
            model.addAttribute("novoItem", novoItem);
            model.addAttribute("produtos", listarProdutos.listarTodos().stream().map(ProdutoView::new).collect(Collectors.toList()));
            model.addAttribute("valorTotal", valorTotal);
            model.addAttribute("valorComDesconto", valorComDesconto);
            if (erro != null && !erro.isEmpty()) {
                model.addAttribute("mensagemErro", erro);
            }
            return "pedidos/itens";
        } catch (RuntimeException e) {
            model.addAttribute("mensagemErro", "Erro ao listar itens do pedido: " + e.getMessage());
            return "erro";
        }
    }

    @GetMapping("/api/itens/{pedidoId}")
    @ResponseBody
    public ResponseEntity<?> listarItensPedidoApi(@PathVariable("pedidoId") int pedidoId) {
        try {
            List<ItemPedidoView> itens = listarItens.porPedido(pedidoId).stream().map(ItemPedidoView::new).collect(Collectors.toList());
            return ResponseEntity.ok(itens);
        } catch (RuntimeException e) {
            return erro("Erro ao listar itens do pedido: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/itens/adicionar")
    public String adicionarItemPedido(@ModelAttribute("novoItem") ItemPedidoForm form, Model model) {
        try {
            adicionarItem.executar(form.toCommand());
            return "redirect:/pedidos/itens/" + form.getPedidoId();
        } catch (RuntimeException e) {
            try {
                return listarItensPedido(form.getPedidoId(), e.getMessage(), model);
            } catch (RuntimeException ignored) {
                return "redirect:/pedidos/itens/" + form.getPedidoId() + "?erro=" + e.getMessage();
            }
        }
    }

    @PostMapping("/api/itens/adicionar")
    @ResponseBody
    public ResponseEntity<?> adicionarItemPedidoApi(@RequestBody ItemPedidoForm form) {
        try {
            return ResponseEntity.ok(new ItemPedidoView(new com.studiomuda.estoque.domain.pedido.ItemPedidoComProduto(
                    adicionarItem.executar(form.toCommand()), null, 0
            )));
        } catch (RuntimeException e) {
            return erro("Erro ao adicionar item ao pedido: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/itens/excluir/{id}")
    public String excluirItemPedido(@PathVariable("id") int id) {
        try {
            int pedidoId = removerItem.executar(id);
            return "redirect:/pedidos/itens/" + pedidoId;
        } catch (RuntimeException e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @DeleteMapping("/api/itens/{id}")
    @ResponseBody
    public ResponseEntity<?> excluirItemPedidoApi(@PathVariable("id") int id) {
        try {
            int pedidoId = removerItem.executar(id);
            Map<String, Object> response = new HashMap<>();
            response.put("mensagem", "Item excluído com sucesso");
            response.put("pedidoId", pedidoId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return erro("Erro ao excluir item do pedido: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/filtros")
    @ResponseBody
    public Map<String, List<String>> getFiltrosPedidos() throws java.sql.SQLException {
        Map<String, List<String>> filtros = new HashMap<>();
        try (Connection conn = com.studiomuda.estoque.conexao.Conexao.getConnection()) {
            List<String> status = new java.util.ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT status FROM pedido WHERE status IS NOT NULL AND status <> ''");
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    status.add(rs.getString("status"));
                }
            }
            filtros.put("status", status);
        }
        return filtros;
    }

    private SalvarPedidoCommand toCommand(PedidoForm form, String dataRequisicaoStr, String dataEntregaStr,
                                          String dataPagamentoStr, Integer cupomId) {
        LocalDate dataRequisicao = parseData(dataRequisicaoStr, LocalDate.now());
        LocalDate dataEntrega = parseData(dataEntregaStr, null);
        String statusPagamento = normalizarStatusPagamento(form.getStatusPagamento());
        LocalDate dataPagamento = "PAGO".equals(statusPagamento)
                ? parseData(dataPagamentoStr, LocalDate.now())
                : null;
        Integer cupomNormalizado = (cupomId != null && cupomId > 0) ? cupomId : null;
        form.setStatusPagamento(statusPagamento);
        form.setCupomId(cupomNormalizado);
        return form.toCommand(dataRequisicao, dataEntrega, dataPagamento, cupomNormalizado);
    }

    private String normalizarStatusPagamento(String statusPagamento) {
        if ("PAGO".equalsIgnoreCase(statusPagamento)) {
            return "PAGO";
        }
        return "PENDENTE";
    }

    private LocalDate parseData(String valor, LocalDate padrao) {
        if (valor == null || valor.trim().isEmpty()) {
            return padrao;
        }
        return LocalDate.parse(valor.trim());
    }

    private void carregarDadosFormularioPedido(Model model, PedidoForm pedido) {
        List<ClienteView> clientes = listarClientes.listarTodos().stream()
                .map(ClienteComFrequencia::cliente)
                .filter(Cliente::ativo)
                .map(ClienteView::semFrequencia)
                .collect(Collectors.toList());
        List<FuncionarioView> funcionarios = listarFuncionarios.listarTodos().stream()
                .filter(Funcionario::ativo)
                .map(FuncionarioView::new)
                .collect(Collectors.toList());
        List<CupomView> cupons = listarCupons.listarTodos().stream().map(CupomView::new).collect(Collectors.toList());

        model.addAttribute("pedido", pedido);
        model.addAttribute("clientes", clientes);
        model.addAttribute("funcionarios", funcionarios);
        model.addAttribute("cupons", cupons);
    }

    private Optional<PedidoView> buscarPedidoCompleto(int pedidoId) {
        Optional<PedidoComJoins> comJoins = listarPedidos.listarTodos().stream()
                .filter(p -> p.pedido().id() == pedidoId)
                .findFirst();
        if (comJoins.isPresent()) {
            return Optional.of(new PedidoView(comJoins.get()));
        }
        return buscar.porId(pedidoId).map(PedidoView::new);
    }

    private ResponseEntity<?> erro(String mensagem, HttpStatus status) {
        Map<String, String> payload = new HashMap<>();
        payload.put("erro", mensagem);
        return ResponseEntity.status(status).body(payload);
    }
}

@RestController
@RequestMapping("/api/pedidos")
class PedidoApiController {
    private final ListarPedidosUseCase listarPedidos;

    PedidoApiController(ListarPedidosUseCase listarPedidos) {
        this.listarPedidos = listarPedidos;
    }

    @GetMapping("/count")
    public ResponseEntity<?> contarPedidos() {
        try {
            return ResponseEntity.ok(listarPedidos.listarTodos().size());
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao contar pedidos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
