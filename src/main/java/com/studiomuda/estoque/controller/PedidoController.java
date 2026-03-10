package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.dao.ClienteDAO;
import com.studiomuda.estoque.dao.CupomDAO;
import com.studiomuda.estoque.dao.FuncionarioDAO;
import com.studiomuda.estoque.dao.ItemPedidoDAO;
import com.studiomuda.estoque.dao.MovimentacaoEstoqueDAO;
import com.studiomuda.estoque.dao.PedidoDAO;
import com.studiomuda.estoque.dao.ProdutoDAO;
import com.studiomuda.estoque.model.Cupom;
import com.studiomuda.estoque.model.ItemPedido;
import com.studiomuda.estoque.model.MovimentacaoEstoque;
import com.studiomuda.estoque.model.Pedido;
import com.studiomuda.estoque.model.Produto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private final CupomDAO cupomDAO = new CupomDAO();

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
            
            model.addAttribute("pedido", pedido);
            model.addAttribute("clientes", clienteDAO.listarAtivos());
            model.addAttribute("funcionarios", funcionarioDAO.listar());
            model.addAttribute("cupons", cupomDAO.listar());
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
                              @RequestParam(value = "cupomId", required = false) Integer cupomId) {
        try {
            // Converter strings de data para Date
            if (dataRequisicaoStr != null && !dataRequisicaoStr.isEmpty()) {
                pedido.setDataRequisicao(Date.valueOf(dataRequisicaoStr));
            }
            
            if (dataEntregaStr != null && !dataEntregaStr.isEmpty()) {
                pedido.setDataEntrega(Date.valueOf(dataEntregaStr));
            }
            
            // Verificar e aplicar cupom se existir
            if (cupomId != null && cupomId > 0) {
                Cupom cupom = cupomDAO.buscarPorId(cupomId);
                if (cupom != null && cupom.isValido()) {
                    pedido.setCupomId(cupomId);
                    pedido.setValorDesconto(cupom.getValor());
                } else {
                    pedido.setCupomId(0);
                    pedido.setValorDesconto(0.0);
                }
            } else {
                pedido.setCupomId(0);
                pedido.setValorDesconto(0.0);
            }
            
            if (pedido.getId() == 0) {
                pedidoDAO.inserir(pedido);
            } else {
                pedidoDAO.atualizar(pedido);
            }
            return "redirect:/pedidos/itens/" + pedido.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }
    
    @PostMapping("/api/salvar")
    @ResponseBody
    public ResponseEntity<?> salvarPedidoApi(@RequestBody Pedido pedido) {
        try {
            // Verificar e aplicar cupom se existir
            if (pedido.getCupomId() > 0) {
                Cupom cupom = cupomDAO.buscarPorId(pedido.getCupomId());
                if (cupom != null && cupom.isValido()) {
                    pedido.setValorDesconto(cupom.getValor());
                } else {
                    pedido.setCupomId(0);
                    pedido.setValorDesconto(0.0);
                }
            } else {
                pedido.setCupomId(0);
                pedido.setValorDesconto(0.0);
            }
            
            if (pedido.getId() == 0) {
                pedidoDAO.inserir(pedido);
            } else {
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
                model.addAttribute("pedido", pedido);
                model.addAttribute("clientes", clienteDAO.listarAtivos());
                model.addAttribute("funcionarios", funcionarioDAO.listar());
                model.addAttribute("cupons", cupomDAO.listar());
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
