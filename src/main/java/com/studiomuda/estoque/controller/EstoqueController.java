package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.model.MovimentacaoEstoque;
import com.studiomuda.estoque.service.EstoqueService;
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
@RequestMapping("/estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @GetMapping
    public String listarMovimentacoes(Model model,
                                    @RequestParam(required = false) String produto,
                                    @RequestParam(required = false) String tipo,
                                    @RequestParam(required = false) String dataInicio,
                                    @RequestParam(required = false) String dataFim) {
        try {
            List<MovimentacaoEstoque> movimentacoes;

            if (produto != null || tipo != null || dataInicio != null || dataFim != null) {
                movimentacoes = estoqueService.buscarMovimentacoesComFiltros(produto, tipo, dataInicio, dataFim);
            } else {
                movimentacoes = estoqueService.listarMovimentacoes();
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
            movimentacao.setTipo("entrada");

            model.addAttribute("movimentacao", movimentacao);
            model.addAttribute("produtos", estoqueService.listarProdutos());
            return "estoque/form";
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao preparar formulário: " + e.getMessage());
            return "erro";
        }
    }

    @PostMapping("/salvar")
    public String salvarMovimentacao(@ModelAttribute MovimentacaoEstoque movimentacao,
                                    @RequestParam(value = "dataStr", required = false) String dataStr) {
        try {
            if (dataStr != null && !dataStr.isEmpty()) {
                movimentacao.setData(Date.valueOf(dataStr));
            } else {
                movimentacao.setData(Date.valueOf(LocalDate.now()));
            }

            estoqueService.registrarMovimentacao(movimentacao);
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
            estoqueService.excluirMovimentacao(id);
            System.out.println("Movimentação excluída com sucesso!");
            return "redirect:/estoque";
        } catch (IllegalArgumentException e) {
            return "redirect:/erro?mensagem=Movimentação não encontrada";
        } catch (IllegalStateException e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
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

    private final EstoqueService estoqueService;

    EstoqueApiController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @GetMapping("/count")
    public ResponseEntity<?> contarMovimentacoes() {
        try {
            int count = estoqueService.listarMovimentacoes().size();
            return ResponseEntity.ok(count);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao contar movimentações: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
