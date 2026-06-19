package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.model.HistoricoPreco;
import com.studiomuda.estoque.model.Produto;
import com.studiomuda.estoque.repository.HistoricoPrecoRepository;
import com.studiomuda.estoque.repository.ProdutoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller da camada de apresentação para histórico de preços.
 * Camada: Apresentação (Arquitetura Limpa)
 * Persistência: ORM via Spring Data JPA
 */
@Controller
@RequestMapping("/produtos/historico")
public class HistoricoPrecosController {

    private final HistoricoPrecoRepository historicoRepository;
    private final ProdutoRepository produtoRepository;

    public HistoricoPrecosController(HistoricoPrecoRepository historicoRepository,
                                      ProdutoRepository produtoRepository) {
        this.historicoRepository = historicoRepository;
        this.produtoRepository = produtoRepository;
    }

    @GetMapping("/{id}")
    public String historicoPorProduto(@PathVariable int id, Model model) {
        Produto produto = produtoRepository.findById(id).orElse(null);
        if (produto == null) return "redirect:/produtos";
        List<HistoricoPreco> historico = historicoRepository.findByProdutoIdOrderByDataAlteracaoDesc(id);
        model.addAttribute("produto", produto);
        model.addAttribute("historico", historico);
        model.addAttribute("modoGeral", false);
        return "produtos/historico";
    }

    @GetMapping
    public String historicoGeral(Model model) {
        List<HistoricoPreco> historico = historicoRepository.findAllOrderByDataAlteracaoDesc();
        // Enriquecer com nome do produto
        for (HistoricoPreco h : historico) {
            Produto p = produtoRepository.findById(h.getProdutoId()).orElse(null);
            if (p != null) {
                h.setProdutoNome(p.getNome());
            }
        }
        model.addAttribute("historico", historico);
        model.addAttribute("modoGeral", true);
        return "produtos/historico";
    }
}