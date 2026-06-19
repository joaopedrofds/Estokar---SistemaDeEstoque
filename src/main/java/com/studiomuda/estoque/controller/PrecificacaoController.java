package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.model.*;
import com.studiomuda.estoque.repository.ProdutoRepository;
import com.studiomuda.estoque.service.PrecificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller da camada de apresentação para o motor de precificação.
 * Camada: Apresentação (Arquitetura Limpa)
 */
@Controller
@RequestMapping("/precificacao")
public class PrecificacaoController {

    @Autowired private PrecificacaoService precificacaoService;
    @Autowired private ProdutoRepository produtoRepository;

    // ─── Regras ──────────────────────────────────────────────────────────────

    @GetMapping("/regras")
    public String listarRegras(Model model) {
        List<RegraPrecificacao> regras = precificacaoService.listarRegras();
        // Enriquecer com nome do produto
        regras.forEach(r -> produtoRepository.findById(r.getProdutoId())
            .ifPresent(p -> r.setProdutoNome(p.getNome())));
        model.addAttribute("regras", regras);
        model.addAttribute("produtos", produtoRepository.findAll());
        model.addAttribute("novaRegra", new RegraPrecificacao());
        return "precificacao/regras";
    }

    @PostMapping("/regras/salvar")
    public String salvarRegra(@ModelAttribute RegraPrecificacao regra) {
        precificacaoService.salvarRegra(regra);
        return "redirect:/precificacao/regras?sucesso=salvo";
    }

    @PostMapping("/regras/excluir/{id}")
    public String excluirRegra(@PathVariable int id) {
        precificacaoService.excluirRegra(id);
        return "redirect:/precificacao/regras?sucesso=excluido";
    }

    // ─── Simulador ───────────────────────────────────────────────────────────

    @GetMapping("/simular")
    public String formSimular(Model model) {
        model.addAttribute("produtos", produtoRepository.findAll());
        return "precificacao/simular";
    }

    @GetMapping("/simular/{produtoId}")
    public String simularPorProduto(@PathVariable int produtoId,
                                     Authentication auth, Model model) {
        try {
            String usuario = auth != null ? auth.getName() : "sistema";
            ResultadoSimulacao resultado = precificacaoService.simular(produtoId, usuario);
            model.addAttribute("resultado", resultado);
            model.addAttribute("produtos", produtoRepository.findAll());
            model.addAttribute("produtoSelecionado", produtoId);
            return "precificacao/simular";
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao simular: " + e.getMessage());
            model.addAttribute("produtos", produtoRepository.findAll());
            return "precificacao/simular";
        }
    }

    @PostMapping("/aplicar")
    public String aplicarPreco(@RequestParam int produtoId,
                                @RequestParam double precoNovo,
                                Authentication auth,
                                RedirectAttributes redirect) {
        String usuario = auth != null ? auth.getName() : "sistema";
        try {
            precificacaoService.aplicarPreco(produtoId, precoNovo, usuario);
            redirect.addFlashAttribute("sucesso", "Preço aplicado com sucesso! Histórico atualizado.");
            return "redirect:/precificacao/simular/" + produtoId + "?sucesso=aplicado";
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("mensagemErro", e.getMessage());
            return "redirect:/precificacao/simular/" + produtoId;
        }
    }

    // ─── Histórico ───────────────────────────────────────────────────────────

    @GetMapping("/historico")
    public String historico(@RequestParam(required = false) String status,
                             Model model) {
        List<SimulacaoPreco> simulacoes = status != null && !status.isEmpty()
            ? precificacaoService.listarSimulacoes().stream()
                .filter(s -> status.equals(s.getStatus())).collect(Collectors.toList())
            : precificacaoService.listarSimulacoes();

        simulacoes.forEach(s -> produtoRepository.findById(s.getProdutoId())
            .ifPresent(p -> s.setProdutoNome(p.getNome())));

        model.addAttribute("simulacoes", simulacoes);
        model.addAttribute("statusFiltro", status);
        return "precificacao/historico";
    }

    // ─── Parâmetros ──────────────────────────────────────────────────────────

    @GetMapping("/parametros")
    public String parametros(Model model) {
        model.addAttribute("params", precificacaoService.buscarParametros());
        return "precificacao/parametros";
    }

    @PostMapping("/parametros/salvar")
    public String salvarParametros(@ModelAttribute ParametroPrecificacao params) {
        precificacaoService.salvarParametros(params);
        return "redirect:/precificacao/parametros?sucesso=salvo";
    }
}