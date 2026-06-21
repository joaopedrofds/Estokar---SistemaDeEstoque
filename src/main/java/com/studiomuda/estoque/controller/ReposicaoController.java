package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.service.AlertaReposicaoService;
import com.studiomuda.estoque.service.SuprimentoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reposicao/alertas")
public class ReposicaoController {

    private final AlertaReposicaoService alertaReposicaoService;
    private final SuprimentoService suprimentoService;

    public ReposicaoController(AlertaReposicaoService alertaReposicaoService,
                               SuprimentoService suprimentoService) {
        this.alertaReposicaoService = alertaReposicaoService;
        this.suprimentoService = suprimentoService;
    }

    @GetMapping
    public String listar(Model model,
                         @RequestParam(required = false) String mensagem,
                         @RequestParam(required = false) String erro) {
        model.addAttribute("alertasAtivos", alertaReposicaoService.listarAtivos());
        model.addAttribute("alertasResolvidos", alertaReposicaoService.listarResolvidos());
        model.addAttribute("mensagem", mensagem);
        model.addAttribute("erro", erro);
        return "reposicao/alertas";
    }

    @PostMapping("/verificar")
    public String verificar(RedirectAttributes redirectAttributes) {
        try {
            int ativos = alertaReposicaoService.sincronizarAlertas(suprimentoService.listarParametrosComMetricas());
            redirectAttributes.addFlashAttribute("mensagem",
                    ativos + " alerta(s) de reposicao ativo(s) apos a verificacao.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao verificar reposicao: " + e.getMessage());
        }
        return "redirect:/reposicao/alertas";
    }

    @PostMapping("/resolver")
    public String resolver(@RequestParam Integer id,
                           @RequestParam(required = false) String observacao,
                           RedirectAttributes redirectAttributes) {
        try {
            alertaReposicaoService.resolver(id, observacao);
            redirectAttributes.addFlashAttribute("mensagem", "Alerta resolvido com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao resolver alerta: " + e.getMessage());
        }
        return "redirect:/reposicao/alertas";
    }
}
