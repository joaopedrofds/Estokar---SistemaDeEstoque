package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.dao.MovimentacaoEstoqueDAO;
import com.studiomuda.estoque.dao.ProdutoDAO;
import com.studiomuda.estoque.model.MovimentacaoEstoque;
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
import java.util.Map;

@Controller
@RequestMapping("/estoque")
public class EstoqueController {

    private final MovimentacaoEstoqueDAO movimentacaoDAO = new MovimentacaoEstoqueDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    @GetMapping
    public String listarMovimentacoes(Model model,
                                    @RequestParam(required = false) String produto,
                                    @RequestParam(required = false) String tipo,
                                    @RequestParam(required = false) String dataInicio,
                                    @RequestParam(required = false) String dataFim) {
        try {
            List<MovimentacaoEstoque> movimentacoes;
            
            // Se há filtros, usar busca filtrada, senão listar todos
            if (produto != null || tipo != null || dataInicio != null || dataFim != null) {
                movimentacoes = movimentacaoDAO.buscarComFiltros(produto, tipo, dataInicio, dataFim);
            } else {
                movimentacoes = movimentacaoDAO.listar();
            }
            
            model.addAttribute("movimentacoes", movimentacoes);
            model.addAttribute("filtroProduto", produto);
            model.addAttribute("filtroTipo", tipo);
            model.addAttribute("filtroDataInicio", dataInicio);
            model.addAttribute("filtroDataFim", dataFim);
            return "estoque/lista";
        } catch (SQLException e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @GetMapping("/nova")
    public String formNovaMovimentacao(Model model) {
        try {
            MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
            movimentacao.setData(Date.valueOf(LocalDate.now()));
            movimentacao.setTipo("entrada"); // Valor padru00e3o
            
            model.addAttribute("movimentacao", movimentacao);
            model.addAttribute("produtos", produtoDAO.listar());
            return "estoque/form";
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao preparar formulu00e1rio: " + e.getMessage());
            return "erro";
        }
    }

    @PostMapping("/salvar")
    public String salvarMovimentacao(@ModelAttribute MovimentacaoEstoque movimentacao,
                                    @RequestParam(value = "dataStr", required = false) String dataStr) {
        try {
            // Converter string de data para Date
            if (dataStr != null && !dataStr.isEmpty()) {
                movimentacao.setData(Date.valueOf(dataStr));
            } else {
                // Usar a data atual se não for fornecida
                movimentacao.setData(Date.valueOf(LocalDate.now()));
            }
            
            movimentacaoDAO.registrar(movimentacao);
            return "redirect:/estoque";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @GetMapping("/excluir/{id}")
    public String excluirMovimentacao(@PathVariable("id") int id) {
        try {
            System.out.println("Tentando excluir movimentação ID: " + id);
            
            // Verificar se a movimentação existe
            MovimentacaoEstoque movimentacao = movimentacaoDAO.buscarPorId(id);
            if (movimentacao == null) {
                return "redirect:/erro?mensagem=Movimentação não encontrada";
            }
            
            // Verificar se a exclusão de uma entrada causará estoque negativo
            if (movimentacao.getTipo().equalsIgnoreCase("entrada")) {
                // Buscar o produto para verificar o estoque atual
                int produtoId = movimentacao.getIdProduto();
                int quantidadeMovimentada = movimentacao.getQuantidade();
                
                // Verificar se o estoque ficará negativo após o estorno
                int estoqueAtual = produtoDAO.buscarPorId(produtoId).getQuantidade();
                if (estoqueAtual < quantidadeMovimentada) {
                    return "redirect:/erro?mensagem=Não é possível excluir esta movimentação pois resultaria em estoque negativo. Estoque atual: " + estoqueAtual;
                }
            }
            
            movimentacaoDAO.deletar(id);
            System.out.println("Movimentação excluída com sucesso!");
            return "redirect:/estoque";
        } catch (SQLException e) {
            System.out.println("ERRO ao excluir movimentação: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }
    
}

@RestController
@RequestMapping("/api/estoque")
class EstoqueApiController {
    private final MovimentacaoEstoqueDAO movimentacaoDAO = new MovimentacaoEstoqueDAO();

    @GetMapping("/count")
    public ResponseEntity<?> contarMovimentacoes() {
        try {
            List<MovimentacaoEstoque> movimentacoes = movimentacaoDAO.listar();
            int count = movimentacoes.size();
            return ResponseEntity.ok(count);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao contar movimentações: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
