package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.model.HistoricoCobranca;
import com.studiomuda.estoque.model.PoliticaCredito;
import com.studiomuda.estoque.service.CobrancaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cobrancas")
public class CobrancaController {
    private final CobrancaService cobrancaService;

    public CobrancaController(CobrancaService cobrancaService) {
        this.cobrancaService = cobrancaService;
    }

    @GetMapping
    public String painel(Model model) {
        model.addAttribute("politica", new PoliticaCredito());
        model.addAttribute("historico", new HistoricoCobranca());
        return "cobrancas/painel";
    }

    @PostMapping("/politicas")
    public String ativarPolitica(@ModelAttribute PoliticaCredito politica, RedirectAttributes redirectAttributes) {
        try {
            cobrancaService.ativarPolitica(politica);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Politica de credito ativada com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao ativar politica: " + e.getMessage());
        }
        return "redirect:/cobrancas";
    }

    @PostMapping("/historicos")
    public String registrarContato(@ModelAttribute HistoricoCobranca historico, RedirectAttributes redirectAttributes) {
        try {
            cobrancaService.registrarContato(historico);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Contato de cobranca registrado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao registrar contato: " + e.getMessage());
        }
        return "redirect:/cobrancas";
    }

    @PostMapping("/historicos/correcao")
    public String corrigirContato(@ModelAttribute HistoricoCobranca historico, RedirectAttributes redirectAttributes) {
        try {
            if (historico.getRegistroOriginalId() == null || historico.getRegistroOriginalId() <= 0) {
                throw new IllegalArgumentException("Informe o registro original.");
            }
            cobrancaService.corrigirContato(historico.getRegistroOriginalId(), historico);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Correcao registrada sem alterar o historico original.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao corrigir contato: " + e.getMessage());
        }
        return "redirect:/cobrancas";
    }
}
