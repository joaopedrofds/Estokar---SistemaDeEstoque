package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.dao.ClienteDAO;
import com.studiomuda.estoque.dao.CupomDAO;
import com.studiomuda.estoque.dao.FuncionarioDAO;
import com.studiomuda.estoque.dao.ItemPedidoDAO;
import com.studiomuda.estoque.dao.MovimentacaoEstoqueDAO;
import com.studiomuda.estoque.dao.PedidoDAO;
import com.studiomuda.estoque.dao.ProdutoDAO;
import com.studiomuda.estoque.jpa.entity.PedidoJpaEntity;
import com.studiomuda.estoque.model.Cupom;
import com.studiomuda.estoque.model.ItemPedido;
import com.studiomuda.estoque.model.MovimentacaoEstoque;
import com.studiomuda.estoque.model.Pedido;
import com.studiomuda.estoque.model.Produto;
import com.studiomuda.estoque.security.UsuarioAutenticado;
import com.studiomuda.estoque.service.CobrancaService;
import com.studiomuda.estoque.service.CupomService;
import com.studiomuda.estoque.service.PedidoService;
import com.studiomuda.estoque.strategy.ContextoDesconto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {
    private static final int DIAS_LIMITE_INADIMPLENCIA = 45;

    private final PedidoDAO pedidoDAO;
    private final ItemPedidoDAO itemPedidoDAO;
    private final ClienteDAO clienteDAO;
    private final ProdutoDAO produtoDAO;
    private final FuncionarioDAO funcionarioDAO;
    private final CupomDAO cupomDAO;
    private final PedidoService pedidoService;
    private final CobrancaService cobrancaService;
    private final CupomService cupomService;

    @Autowired
    public PedidoController(ObjectProvider<PedidoService> pedidoServiceProvider,
                            ObjectProvider<CobrancaService> cobrancaServiceProvider,
                            ObjectProvider<CupomService> cupomServiceProvider,
                            PedidoDAO pedidoDAO,
                            ItemPedidoDAO itemPedidoDAO,
                            ClienteDAO clienteDAO,
                            ProdutoDAO produtoDAO,
                            FuncionarioDAO funcionarioDAO,
                            CupomDAO cupomDAO) {
        this.pedidoDAO = pedidoDAO;
        this.itemPedidoDAO = itemPedidoDAO;
        this.clienteDAO = clienteDAO;
        this.produtoDAO = produtoDAO;
        this.funcionarioDAO = funcionarioDAO;
        this.cupomDAO = cupomDAO;
        this.pedidoService = pedidoServiceProvider.getIfAvailable();
        this.cobrancaService = cobrancaServiceProvider.getIfAvailable();
        this.cupomService = cupomServiceProvider.getIfAvailable();
    }

    // Construtor fallback para testes (sem Spring)
    PedidoController(PedidoDAO pedidoDAO, ItemPedidoDAO itemPedidoDAO, ClienteDAO clienteDAO,
                     ProdutoDAO produtoDAO, FuncionarioDAO funcionarioDAO, CupomDAO cupomDAO) {
        this(pedidoDAO, itemPedidoDAO, clienteDAO, produtoDAO, funcionarioDAO, cupomDAO,
                new PedidoService(pedidoDAO, itemPedidoDAO), null, null);
    }

    PedidoController(PedidoDAO pedidoDAO, ItemPedidoDAO itemPedidoDAO, ClienteDAO clienteDAO,
                     ProdutoDAO produtoDAO, FuncionarioDAO funcionarioDAO, CupomDAO cupomDAO,
                     PedidoService pedidoService) {
        this(pedidoDAO, itemPedidoDAO, clienteDAO, produtoDAO, funcionarioDAO, cupomDAO,
                pedidoService, null, null);
    }

    PedidoController(PedidoDAO pedidoDAO, ItemPedidoDAO itemPedidoDAO, ClienteDAO clienteDAO,
                     ProdutoDAO produtoDAO, FuncionarioDAO funcionarioDAO, CupomDAO cupomDAO,
                     PedidoService pedidoService, CobrancaService cobrancaService) {
        this(pedidoDAO, itemPedidoDAO, clienteDAO, produtoDAO, funcionarioDAO, cupomDAO,
                pedidoService, cobrancaService, null);
    }

    PedidoController(PedidoDAO pedidoDAO, ItemPedidoDAO itemPedidoDAO, ClienteDAO clienteDAO,
                     ProdutoDAO produtoDAO, FuncionarioDAO funcionarioDAO, CupomDAO cupomDAO,
                     PedidoService pedidoService, CobrancaService cobrancaService, CupomService cupomService) {
        this.pedidoDAO = pedidoDAO;
        this.itemPedidoDAO = itemPedidoDAO;
        this.clienteDAO = clienteDAO;
        this.produtoDAO = produtoDAO;
        this.funcionarioDAO = funcionarioDAO;
        this.cupomDAO = cupomDAO;
        this.pedidoService = pedidoService;
        this.cobrancaService = cobrancaService;
        this.cupomService = cupomService;
    }

    @GetMapping
    public String listarPedidos(@RequestParam(value = "cpfCnpj", required = false) String cpfCnpj, Model model) {
        try {
            if (cpfCnpj != null && !cpfCnpj.trim().isEmpty()) {
                // Buscar cliente por CPF/CNPJ
                String cpfCnpjLimpo = cpfCnpj.replaceAll("[^0-9]", "");
                com.studiomuda.estoque.model.Cliente cliente = clienteDAO.buscarPorCpfCnpj(cpfCnpjLimpo);

                if (cliente != null) {
                    // Cliente encontrado, buscar pedidos deste cliente
                    model.addAttribute("pedidos", pedidoDAO.listarPorCliente(cliente.getId()));
                    model.addAttribute("clienteEncontrado", cliente);
                } else {
                    // Cliente não encontrado
                    model.addAttribute("pedidos", new java.util.ArrayList<>());
                    model.addAttribute("mensagemAviso", "Nenhum cliente encontrado com o CPF/CNPJ informado.");
                }
                model.addAttribute("cpfCnpj", cpfCnpj);
            } else {
                // Listar todos os pedidos
                model.addAttribute("pedidos", pedidoDAO.listar());
            }
            return "pedidos/lista";
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao listar pedidos: " + e.getMessage());
            return "erro";
        }
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<?> listarPedidosApi() {
        try {
            List<Pedido> pedidos = pedidoDAO.listar();
            return ResponseEntity.ok(pedidos);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao listar pedidos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/novo")
    public String formNovoPedido(Model model) {
        try {
            Pedido pedido = new Pedido();
            pedido.setDataRequisicao(Date.valueOf(LocalDate.now()));
            pedido.setStatusPagamento("PENDENTE");
            carregarDadosFormularioPedido(model, pedido);
            return "pedidos/form";
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao preparar formulário: " + e.getMessage());
            return "erro";
        }
    }

    @PostMapping("/salvar")
    public String salvarPedido(@ModelAttribute Pedido pedido,
                              @RequestParam(value = "dataRequisicaoStr", required = false) String dataRequisicaoStr,
                              @RequestParam(value = "dataEntregaStr", required = false) String dataEntregaStr,
                              @RequestParam(value = "dataPagamentoStr", required = false) String dataPagamentoStr,
                              @RequestParam(value = "cupomId", required = false) Integer cupomId,
                              Model model) {
        try {
            // Converter strings de data para Date
            if (dataRequisicaoStr != null && !dataRequisicaoStr.isEmpty()) {
                pedido.setDataRequisicao(Date.valueOf(dataRequisicaoStr));
            }

            if (dataEntregaStr != null && !dataEntregaStr.isEmpty()) {
                pedido.setDataEntrega(Date.valueOf(dataEntregaStr));
            }

            if (dataPagamentoStr != null && !dataPagamentoStr.isEmpty()) {
                pedido.setDataPagamento(Date.valueOf(dataPagamentoStr));
            } else {
                pedido.setDataPagamento(null);
            }

            if (pedido.getStatusPagamento() == null || pedido.getStatusPagamento().trim().isEmpty()) {
                pedido.setStatusPagamento("PENDENTE");
            } else if (!"PAGO".equalsIgnoreCase(pedido.getStatusPagamento())) {
                pedido.setStatusPagamento("PENDENTE");
            }
            if (!"PAGO".equalsIgnoreCase(pedido.getStatusPagamento())) {
                pedido.setDataPagamento(null);
            } else if (pedido.getDataPagamento() == null) {
                pedido.setDataPagamento(Date.valueOf(LocalDate.now()));
            }

            // Verificar e aplicar cupom se existir
            if (cupomId != null && cupomId > 0) {
                Cupom cupom = cupomDAO.buscarPorId(cupomId);
                if (cupom != null) {
                    if (!cupom.isAtivo() || !cupom.isValido()) {
                        carregarDadosFormularioPedido(model, pedido);
                        model.addAttribute("mensagemErro", "Cupom inv\u00e1lido ou expirado.");
                        return "pedidos/form";
                    }
                    if (cupom.isEsgotado()) {
                        carregarDadosFormularioPedido(model, pedido);
                        model.addAttribute("mensagemErro", "Cupom esgotado \u2014 limite de usos atingido.");
                        return "pedidos/form";
                    }
                    if (!cupom.podeSerUsadoPor(pedido.getClienteId())) {
                        carregarDadosFormularioPedido(model, pedido);
                        model.addAttribute("mensagemErro", "Este cupom \u00e9 exclusivo para outro cliente.");
                        return "pedidos/form";
                    }
                    pedido.setCupomId(cupomId);
                    ContextoDesconto ctx = new ContextoDesconto(cupom.getTipoDesconto());
                    pedido.setValorDesconto(ctx.calcular(0, cupom.getValor()));
                }
            } else {
                pedido.setCupomId(0);
                pedido.setValorDesconto(0.0);
            }

            if (pedido.getId() == 0) {
                CobrancaService.AvaliacaoCredito avaliacaoCredito = avaliarCreditoPedido(pedido.getClienteId());
                if (avaliacaoCredito.isBloqueado()) {
                    String alertaFinanceiro = "Cliente bloqueado automaticamente por inadimplencia. Existe fatura com "
                            + avaliacaoCredito.getDiasAtraso() + " dias de atraso (fatura #"
                            + avaliacaoCredito.getFaturaId() + "; limite " + avaliacaoCredito.getLimiteDias() + " dias).";
                    carregarDadosFormularioPedido(model, pedido);
                    model.addAttribute("mensagemErro", alertaFinanceiro);
                    model.addAttribute("mensagemAviso", "Bloqueio calculado pela politica de credito vigente.");
                    return "pedidos/form";
                }
                pedidoDAO.inserir(pedido);
                // Observer: registrar uso do cupom via CupomService
                if (pedido.getCupomId() > 0 && cupomService != null) {
                    try {
                        cupomService.aplicarCupom(pedido.getCupomId(), pedido.getId(),
                                                   pedido.getClienteId(), pedido.getValorDesconto());
                    } catch (Exception e) {
                        System.err.println("[PedidoController] Aviso ao registrar uso do cupom: " + e.getMessage());
                    }
                }
            } else {
                Pedido pedidoAtual = pedidoDAO.buscarPorId(pedido.getId());
                if (pedidoAtual != null && isPedidoImutavel(pedidoAtual)) {
                    carregarDadosFormularioPedido(model, pedidoAtual);
                    model.addAttribute("mensagemErro", "Pedido cancelado ou pendente de aprovacao nao pode ser alterado.");
                    return "pedidos/form";
                }
                pedidoDAO.atualizar(pedido);
            }
            return "redirect:/pedidos/itens/" + pedido.getId();
        } catch (Exception e) {
            try {
                carregarDadosFormularioPedido(model, pedido);
            } catch (SQLException ex) {
                return "redirect:/erro?mensagem=" + ex.getMessage();
            }
            model.addAttribute("mensagemErro", "Erro ao salvar pedido: " + e.getMessage());
            return "pedidos/form";
        }
    }

    @PostMapping("/api/salvar")
    @ResponseBody
    public ResponseEntity<?> salvarPedidoApi(@RequestBody Pedido pedido) {
        try {
            if (pedido.getStatusPagamento() == null || pedido.getStatusPagamento().trim().isEmpty()) {
                pedido.setStatusPagamento("PENDENTE");
            }
            if (!"PAGO".equalsIgnoreCase(pedido.getStatusPagamento())) {
                pedido.setDataPagamento(null);
            } else if (pedido.getDataPagamento() == null) {
                pedido.setDataPagamento(Date.valueOf(LocalDate.now()));
            }

            // Verificar e aplicar cupom se existir
            if (pedido.getCupomId() > 0) {
                Cupom cupom = cupomDAO.buscarPorId(pedido.getCupomId());
                if (cupom != null) {
                    if (!cupom.isAtivo() || !cupom.isValido()) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("erro", "Cupom inv\u00e1lido ou expirado.");
                        return ResponseEntity.badRequest().body(error);
                    }
                    if (cupom.isEsgotado()) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("erro", "Cupom esgotado \u2014 limite de usos atingido.");
                        return ResponseEntity.badRequest().body(error);
                    }
                    if (!cupom.podeSerUsadoPor(pedido.getClienteId())) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("erro", "Este cupom \u00e9 exclusivo para outro cliente.");
                        return ResponseEntity.badRequest().body(error);
                    }
                    ContextoDesconto ctx = new ContextoDesconto(cupom.getTipoDesconto());
                    pedido.setValorDesconto(ctx.calcular(0, cupom.getValor()));
                } else {
                    pedido.setCupomId(0);
                    pedido.setValorDesconto(0.0);
                }
            } else {
                pedido.setCupomId(0);
                pedido.setValorDesconto(0.0);
            }

            if (pedido.getId() == 0) {
                CobrancaService.AvaliacaoCredito avaliacaoCredito = avaliarCreditoPedido(pedido.getClienteId());
                if (avaliacaoCredito.isBloqueado()) {
                    String mensagem = "Venda bloqueada por inadimplencia. Fatura #"
                            + avaliacaoCredito.getFaturaId() + " com "
                            + avaliacaoCredito.getDiasAtraso() + " dias de atraso.";
                    Map<String, Object> error = new HashMap<>();
                    error.put("erro", mensagem);
                    error.put("codigo", "CLIENTE_BLOQUEADO_INADIMPLENCIA");
                    error.put("diasAtraso", avaliacaoCredito.getDiasAtraso());
                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
                }
                pedidoDAO.inserir(pedido);
                // Observer: registrar uso do cupom via service
                if (pedido.getCupomId() > 0 && cupomService != null) {
                    try {
                        cupomService.aplicarCupom(pedido.getCupomId(), pedido.getId(),
                                                   pedido.getClienteId(), pedido.getValorDesconto());
                    } catch (Exception e) {
                        System.err.println("[PedidoController] Aviso ao registrar uso do cupom: " + e.getMessage());
                    }
                }
            } else {
                Pedido pedidoAtual = pedidoDAO.buscarPorId(pedido.getId());
                if (pedidoAtual != null && isPedidoImutavel(pedidoAtual)) {
                    Map<String, String> error = new HashMap<>();
                    error.put("erro", "Pedido cancelado ou pendente de aprovacao nao pode ser alterado.");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
                }
                pedidoDAO.atualizar(pedido);
            }
            return ResponseEntity.ok(pedido);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao salvar pedido: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/editar/{id}")
    public String editarPedido(@PathVariable("id") int id, Model model) {
        try {
            Pedido pedido = pedidoDAO.buscarPorId(id);
            if (pedido != null) {
                carregarDadosFormularioPedido(model, pedido);
                return "pedidos/form";
            } else {
                return "redirect:/pedidos";
            }
        } catch (SQLException e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> buscarPedidoApi(@PathVariable("id") int id) {
        try {
            Pedido pedido = pedidoDAO.buscarPorId(id);
            if (pedido != null) {
                // Buscar e adicionar os itens do pedido
                List<ItemPedido> itens = itemPedidoDAO.listarPorPedido(id);
                // Adiciona os itens como um campo dinâmico no Map para serialização
                Map<String, Object> pedidoMap = new HashMap<>();
                pedidoMap.put("id", pedido.getId());
                pedidoMap.put("dataRequisicao", pedido.getDataRequisicao());
                pedidoMap.put("dataEntrega", pedido.getDataEntrega());
                pedidoMap.put("clienteId", pedido.getClienteId());
                pedidoMap.put("clienteNome", pedido.getClienteNome());
                pedidoMap.put("cupomId", pedido.getCupomId());
                pedidoMap.put("funcionarioId", pedido.getFuncionarioId());
                pedidoMap.put("funcionarioNome", pedido.getFuncionarioNome());
                pedidoMap.put("funcionarioCargo", pedido.getFuncionarioCargo());
                pedidoMap.put("valorDesconto", pedido.getValorDesconto());
                pedidoMap.put("statusPagamento", pedido.getStatusPagamento());
                pedidoMap.put("dataPagamento", pedido.getDataPagamento());
                pedidoMap.put("diasAtrasoPagamento", pedido.getDiasAtrasoPagamento());
                pedidoMap.put("itens", itens);
                return ResponseEntity.ok(pedidoMap);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao buscar pedido: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/excluir/{id}")
    public String excluirPedido(@PathVariable("id") int id) {
        try {
            System.out.println("Tentando excluir pedido ID: " + id);
            // Primeiro excluir os itens do pedido
            System.out.println("Excluindo itens do pedido...");
            itemPedidoDAO.deletarPorPedido(id);
            // Depois excluir o pedido
            System.out.println("Excluindo o pedido...");
            pedidoDAO.deletar(id);
            System.out.println("Pedido excluído com sucesso!");
            return "redirect:/pedidos";
        } catch (SQLException e) {
            System.out.println("ERRO ao excluir pedido: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @GetMapping("/cancelamentos")
    public String listarCancelamentos(Model model) {
        try {
            if (pedidoService != null) {
                List<PedidoJpaEntity> cancelamentos = pedidoService.listarCancelamentosAuditados();
                model.addAttribute("cancelamentos", cancelamentos);
                model.addAttribute("pendentes", pedidoService.listarCancelamentosPendentes());
                model.addAttribute("limiteCancelamento", pedidoService.buscarLimiteQuantidadeCancelamento());
            } else {
                model.addAttribute("cancelamentos", new java.util.ArrayList<>());
                model.addAttribute("pendentes", new java.util.ArrayList<>());
                model.addAttribute("limiteCancelamento", pedidoDAO.buscarLimiteQuantidadeCancelamento());
            }
            return "pedidos/cancelamentos";
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao listar cancelamentos: " + e.getMessage());
            return "erro";
        }
    }

    @GetMapping("/cancelamentos/{id}")
    public String detalheCancelamento(@PathVariable("id") int id, Model model) {
        if (pedidoService != null) {
            PedidoJpaEntity pedido = pedidoService.buscarPedidoJpa(id);
            if (pedido == null) {
                return "redirect:/pedidos/cancelamentos";
            }
            model.addAttribute("pedido", pedido);
        } else {
            return "redirect:/pedidos/cancelamentos";
        }
        return "pedidos/cancelamento-detalhe";
    }

    @PostMapping("/cancelar/{id}")
    public String cancelarPedido(@PathVariable("id") int id,
                                 @RequestParam("justificativa") String justificativa,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            if (pedidoService == null) {
                redirectAttributes.addFlashAttribute("mensagemErro", "Serviço de cancelamento indisponível.");
                return "redirect:/pedidos";
            }
            PedidoService.ResultadoCancelamento resultado = pedidoService.cancelarPedido(
                    id,
                    usuarioOperacao(authentication),
                    justificativa,
                    autoridades(authentication)
            );
            if (PedidoDAO.STATUS_PEDIDO_CANCELAMENTO_PENDENTE.equals(resultado.getStatus())) {
                redirectAttributes.addFlashAttribute("mensagemAviso",
                        "Cancelamento enviado para aprovacao. Volume: " + resultado.getQuantidadeTotal() +
                                " unidades; limite: " + resultado.getLimiteQuantidadeSemAprovacao() + ".");
            } else {
                redirectAttributes.addFlashAttribute("mensagemSucesso", "Pedido cancelado e estoque estornado com sucesso.");
            }
            return "redirect:/pedidos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao cancelar pedido: " + e.getMessage());
            return "redirect:/pedidos";
        }
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> excluirPedidoApi(@PathVariable("id") int id) {
        try {
            // Primeiro excluir os itens do pedido
            itemPedidoDAO.deletarPorPedido(id);
            // Depois excluir o pedido
            pedidoDAO.deletar(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Pedido excluído com sucesso");
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao excluir pedido: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/api/{id}/cancelar")
    @ResponseBody
    public ResponseEntity<?> cancelarPedidoApi(@PathVariable("id") int id,
                                               @RequestBody Map<String, String> body,
                                               Authentication authentication) {
        try {
            if (pedidoService == null) {
                Map<String, String> error = new HashMap<>();
                error.put("erro", "Serviço de cancelamento indisponível.");
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
            }
            PedidoService.ResultadoCancelamento resultado = pedidoService.cancelarPedido(
                    id,
                    usuarioOperacao(authentication),
                    body != null ? body.get("justificativa") : null,
                    autoridades(authentication)
            );
            Map<String, Object> response = new HashMap<>();
            response.put("pedidoId", resultado.getPedidoId());
            response.put("status", resultado.getStatus());
            response.put("quantidadeTotal", resultado.getQuantidadeTotal());
            response.put("limiteQuantidadeSemAprovacao", resultado.getLimiteQuantidadeSemAprovacao());
            response.put("exigiuAprovacao", resultado.isExigiuAprovacao());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao cancelar pedido: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/itens/{pedidoId}")
    public String listarItensPedido(
            @PathVariable("pedidoId") int pedidoId,
            @RequestParam(required = false) String erro,
            Model model) {
        try {
            Pedido pedido = pedidoDAO.buscarPorId(pedidoId);
            List<ItemPedido> itens = itemPedidoDAO.listarPorPedido(pedidoId);
            double valorTotal = 0.0;

            // Calcular o valor total dos itens do pedido
            for (ItemPedido itemPedido : itens) {
                valorTotal += itemPedido.getSubtotal();
            }

            // Aplicar o desconto do cupom, se houver
            double valorComDesconto = valorTotal;
            if (pedido.getValorDesconto() > 0) {
                valorComDesconto = valorTotal - pedido.getValorDesconto();
                if (valorComDesconto < 0) {
                    valorComDesconto = 0; // Garantir que o valor nunca seja negativo
                }
            }

            model.addAttribute("pedido", pedido);
            model.addAttribute("itens", itens);
            model.addAttribute("novoItem", new ItemPedido());
            model.addAttribute("produtos", produtoDAO.listar());
            model.addAttribute("valorTotal", valorTotal);
            model.addAttribute("valorComDesconto", valorComDesconto);

            // Adicionar mensagem de erro, se houver
            if (erro != null && !erro.isEmpty()) {
                model.addAttribute("mensagemErro", erro);
            }

            return "pedidos/itens";
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao listar itens do pedido: " + e.getMessage());
            return "erro";
        }
    }

    @GetMapping("/api/itens/{pedidoId}")
    @ResponseBody
    public ResponseEntity<?> listarItensPedidoApi(@PathVariable("pedidoId") int pedidoId) {
        try {
            List<ItemPedido> itens = itemPedidoDAO.listarPorPedido(pedidoId);
            return ResponseEntity.ok(itens);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao listar itens do pedido: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/itens/adicionar")
    public String adicionarItemPedido(@ModelAttribute ItemPedido item, Model model) {
        try {
            // Verificar se há estoque disponível
            Pedido pedido = pedidoDAO.buscarPorId(item.getPedidoId());
            if (pedido != null && isPedidoImutavel(pedido)) {
                return "redirect:/pedidos/itens/" + item.getPedidoId() + "?erro=Pedido cancelado ou pendente de aprovacao nao permite alterar itens.";
            }

            Produto produto = produtoDAO.buscarPorId(item.getProdutoId());
            if (produto == null) {
                return "redirect:/erro?mensagem=Produto não encontrado";
            }

            if (item.getQuantidade() <= 0) {
                return "redirect:/erro?mensagem=A quantidade deve ser maior que zero";
            }

            if (item.getQuantidade() > produto.getQuantidade()) {
                return "redirect:/pedidos/itens/" + item.getPedidoId() + "?erro=Estoque insuficiente. Quantidade disponível: " + produto.getQuantidade();
            }

            // Se chegou aqui, há estoque suficiente
            itemPedidoDAO.inserir(item);

            // Registrar saída no estoque
            MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
            movimentacao.setIdProduto(item.getProdutoId());
            movimentacao.setTipo("saida");
            movimentacao.setQuantidade(item.getQuantidade());
            movimentacao.setMotivo("Venda - Pedido #" + item.getPedidoId());
            movimentacao.setData(new Date(System.currentTimeMillis()));

            MovimentacaoEstoqueDAO movimentacaoDAO = new MovimentacaoEstoqueDAO();
            movimentacaoDAO.registrar(movimentacao);

            return "redirect:/pedidos/itens/" + item.getPedidoId();
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @PostMapping("/api/itens/adicionar")
    @ResponseBody
    public ResponseEntity<?> adicionarItemPedidoApi(@RequestBody ItemPedido item) {
        try {
            // Verificar se há estoque disponível
            Pedido pedido = pedidoDAO.buscarPorId(item.getPedidoId());
            if (pedido != null && isPedidoImutavel(pedido)) {
                Map<String, String> error = new HashMap<>();
                error.put("erro", "Pedido cancelado ou pendente de aprovacao nao permite alterar itens.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }

            Produto produto = produtoDAO.buscarPorId(item.getProdutoId());
            if (produto == null) {
                Map<String, String> error = new HashMap<>();
                error.put("erro", "Produto não encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            if (item.getQuantidade() <= 0) {
                Map<String, String> error = new HashMap<>();
                error.put("erro", "A quantidade deve ser maior que zero");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            if (item.getQuantidade() > produto.getQuantidade()) {
                Map<String, String> error = new HashMap<>();
                error.put("erro", "Estoque insuficiente. Quantidade disponível: " + produto.getQuantidade());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Se chegou aqui, há estoque suficiente
            itemPedidoDAO.inserir(item);

            // Registrar saída no estoque
            MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
            movimentacao.setIdProduto(item.getProdutoId());
            movimentacao.setTipo("saida");
            movimentacao.setQuantidade(item.getQuantidade());
            movimentacao.setMotivo("Venda - Pedido #" + item.getPedidoId());
            movimentacao.setData(new Date(System.currentTimeMillis()));

            MovimentacaoEstoqueDAO movimentacaoDAO = new MovimentacaoEstoqueDAO();
            movimentacaoDAO.registrar(movimentacao);

            return ResponseEntity.ok(item);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao adicionar item ao pedido: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/itens/excluir/{id}")
    public String excluirItemPedido(@PathVariable("id") int id) {
        try {
            System.out.println("Tentando excluir item de pedido ID: " + id);
            // Buscar o item para obter o ID do pedido antes de excluir
            ItemPedido itemPedido = itemPedidoDAO.buscarPorId(id);
            if (itemPedido == null) {
                return "redirect:/erro?mensagem=Item não encontrado";
            }

            int pedidoId = itemPedido.getPedidoId();
            Pedido pedido = pedidoDAO.buscarPorId(pedidoId);
            if (pedido != null && isPedidoImutavel(pedido)) {
                return "redirect:/pedidos/itens/" + pedidoId + "?erro=Pedido cancelado ou pendente de aprovacao nao permite alterar itens.";
            }
            System.out.println("Item pertence ao pedido ID: " + pedidoId);

            // Restaurar o estoque
            MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
            movimentacao.setIdProduto(itemPedido.getProdutoId());
            movimentacao.setTipo("entrada");
            movimentacao.setQuantidade(itemPedido.getQuantidade());
            movimentacao.setMotivo("Estorno - Cancelamento Item Pedido #" + pedidoId);
            movimentacao.setData(new Date(System.currentTimeMillis()));

            MovimentacaoEstoqueDAO movimentacaoDAO = new MovimentacaoEstoqueDAO();
            movimentacaoDAO.registrar(movimentacao);

            System.out.println("Excluindo item...");
            itemPedidoDAO.deletar(id);
            System.out.println("Item excluído com sucesso!");
            return "redirect:/pedidos/itens/" + pedidoId;
        } catch (SQLException e) {
            System.out.println("ERRO ao excluir item de pedido: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @DeleteMapping("/api/itens/{id}")
    @ResponseBody
    public ResponseEntity<?> excluirItemPedidoApi(@PathVariable("id") int id) {
        try {
            ItemPedido item = itemPedidoDAO.buscarPorId(id);
            if (item == null) {
                Map<String, String> error = new HashMap<>();
                error.put("erro", "Item não encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            Pedido pedido = pedidoDAO.buscarPorId(item.getPedidoId());
            if (pedido != null && isPedidoImutavel(pedido)) {
                Map<String, String> error = new HashMap<>();
                error.put("erro", "Pedido cancelado ou pendente de aprovacao nao permite alterar itens.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }

            // Restaurar o estoque
            MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
            movimentacao.setIdProduto(item.getProdutoId());
            movimentacao.setTipo("entrada");
            movimentacao.setQuantidade(item.getQuantidade());
            movimentacao.setMotivo("Estorno - Cancelamento Item Pedido #" + item.getPedidoId());
            movimentacao.setData(new Date(System.currentTimeMillis()));

            MovimentacaoEstoqueDAO movimentacaoDAO = new MovimentacaoEstoqueDAO();
            movimentacaoDAO.registrar(movimentacao);

            itemPedidoDAO.deletar(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Item excluído com sucesso");
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao excluir item do pedido: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    private void carregarDadosFormularioPedido(Model model, Pedido pedido) throws SQLException {
        model.addAttribute("pedido", pedido);
        model.addAttribute("clientes", clienteDAO.listarAtivos());
        model.addAttribute("funcionarios", funcionarioDAO.listar());
        model.addAttribute("cupons", cupomDAO.listar());
    }

    private boolean isPedidoImutavel(Pedido pedido) {
        String status = pedidoService != null ? pedidoService.normalizarStatusPedido(pedido.getStatus()) : normalizarStatusPedido(pedido.getStatus());
        return PedidoDAO.STATUS_PEDIDO_CANCELADO.equals(status)
                || PedidoDAO.STATUS_PEDIDO_CANCELAMENTO_PENDENTE.equals(status);
    }

    private CobrancaService.AvaliacaoCredito avaliarCreditoPedido(int clienteId) throws SQLException {
        if (cobrancaService != null) {
            return cobrancaService.avaliarVenda(clienteId);
        }
        PedidoDAO.InadimplenciaInfo info = pedidoDAO.verificarInadimplenciaCliente(clienteId, DIAS_LIMITE_INADIMPLENCIA);
        if (info.isBloqueado()) {
            clienteDAO.bloquearPorInadimplencia(clienteId);
            pedidoDAO.registrarAlertaFinanceiro(
                    clienteId,
                    info.getPedidoPendenteId(),
                    info.getDiasAtraso(),
                    "inadimplência (fallback JDBC): cliente bloqueado automaticamente. Existe pendencia com "
                            + info.getDiasAtraso() + " dias de atraso."
            );
            return CobrancaService.AvaliacaoCredito.bloqueada(info.getPedidoPendenteId(), info.getDiasAtraso(), DIAS_LIMITE_INADIMPLENCIA);
        }
        return CobrancaService.AvaliacaoCredito.liberada(DIAS_LIMITE_INADIMPLENCIA);
    }

    private PedidoService.UsuarioOperacao usuarioOperacao(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UsuarioAutenticado) {
            UsuarioAutenticado usuario = (UsuarioAutenticado) authentication.getPrincipal();
            return new PedidoService.UsuarioOperacao(usuario.getId(), usuario.getNome());
        }
        String nome = authentication != null ? authentication.getName() : "sistema";
        return new PedidoService.UsuarioOperacao(1, nome);
    }

    private Collection<String> autoridades(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return java.util.Collections.emptyList();
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    // Método helper para fallback quando PedidoService não está disponível
    private String normalizarStatusPedido(String status) {
        if (status == null || status.trim().isEmpty()) {
            return PedidoDAO.STATUS_PEDIDO_PENDENTE;
        }
        String normalizado = status.trim().toUpperCase();
        if ("CONCLUÍDO".equals(normalizado)) {
            return PedidoDAO.STATUS_PEDIDO_CONCLUIDO;
        }
        return normalizado;
    }

    @GetMapping("/filtros")
    @ResponseBody
    public Map<String, java.util.List<String>> getFiltrosPedidos() throws java.sql.SQLException {
        Map<String, java.util.List<String>> filtros = new java.util.HashMap<>();
        try (java.sql.Connection conn = com.studiomuda.estoque.conexao.Conexao.getConnection()) {
            // Status reais
            java.util.List<String> status = new java.util.ArrayList<>();
            try (java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT status FROM pedido WHERE status IS NOT NULL AND status <> ''")) {
                java.sql.ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    status.add(rs.getString("status"));
                }
            }
            filtros.put("status", status);
        }
        return filtros;
    }
}

@RestController
@RequestMapping("/api/pedidos")
class PedidoApiController {
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    @GetMapping("/count")
    public ResponseEntity<?> contarPedidos() {
        try {
            List<Pedido> pedidos = pedidoDAO.listar();
            int count = pedidos.size();
            return ResponseEntity.ok(count);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao contar pedidos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}