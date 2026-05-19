package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.dao.ProdutoDAO;
import com.studiomuda.estoque.service.SuprimentoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/suprimentos")
public class SuprimentoController {
    private final SuprimentoService suprimentoService = new SuprimentoService();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    @GetMapping
    public String listar(Model model,
                         @RequestParam(required = false) String mensagem,
                         @RequestParam(required = false) String erro) {
        try {
            model.addAttribute("produtos", produtoDAO.listar());
            model.addAttribute("parametros", suprimentoService.listarParametrosComMetricas());
            model.addAttribute("ordens", suprimentoService.listarOrdens());
            model.addAttribute("mensagem", mensagem);
            model.addAttribute("erro", erro);
            return "suprimentos/lista";
        } catch (Exception e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @PostMapping("/parametros")
    public String salvarParametro(@RequestParam int produtoId,
                                  @RequestParam String fornecedorNome,
                                  @RequestParam int leadTimeDias,
                                  @RequestParam int margemSeguranca) {
        try {
            suprimentoService.salvarParametro(produtoId, fornecedorNome, leadTimeDias, margemSeguranca);
            return "redirect:/suprimentos?mensagem=Parametro de reposicao salvo com sucesso";
        } catch (Exception e) {
            return "redirect:/suprimentos?erro=" + e.getMessage();
        }
    }

    @PostMapping("/verificar")
    public String verificarReposicao() {
        try {
            int geradas = suprimentoService.gerarRascunhosPendentes();
            return "redirect:/suprimentos?mensagem=" + geradas + " ordem(ns) de compra gerada(s) em rascunho";
        } catch (Exception e) {
            return "redirect:/suprimentos?erro=" + e.getMessage();
        }
    }

    @PostMapping("/ordens/atualizar")
    public String atualizarRascunho(@RequestParam int ordemId,
                                    @RequestParam int quantidade,
                                    @RequestParam double valorUnitario) {
        try {
            suprimentoService.atualizarRascunho(ordemId, quantidade, valorUnitario);
            return "redirect:/suprimentos?mensagem=Rascunho ajustado com sucesso";
        } catch (Exception e) {
            return "redirect:/suprimentos?erro=" + e.getMessage();
        }
    }

    @PostMapping("/ordens/aprovar")
    public String aprovar(@RequestParam int ordemId) {
        try {
            suprimentoService.aprovar(ordemId);
            return "redirect:/suprimentos?mensagem=Ordem de compra aprovada";
        } catch (Exception e) {
            return "redirect:/suprimentos?erro=" + e.getMessage();
        }
    }

    @PostMapping("/ordens/rejeitar")
    public String rejeitar(@RequestParam int ordemId) {
        try {
            suprimentoService.rejeitar(ordemId);
            return "redirect:/suprimentos?mensagem=Ordem de compra rejeitada";
        } catch (Exception e) {
            return "redirect:/suprimentos?erro=" + e.getMessage();
        }
    }
}
