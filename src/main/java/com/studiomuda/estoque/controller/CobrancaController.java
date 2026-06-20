package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.jpa.entity.AcordoPagamentoJpaEntity;
import com.studiomuda.estoque.jpa.entity.FaturaJpaEntity;
import com.studiomuda.estoque.jpa.entity.HistoricoCobrancaJpaEntity;
import com.studiomuda.estoque.jpa.entity.PoliticaCreditoJpaEntity;
import com.studiomuda.estoque.jpa.repository.ClienteJpaRepository;
import com.studiomuda.estoque.service.CobrancaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
// A anotação @ConditionalOnBean(ClienteJpaRepository.class) foi removida para garantir o mapeamento da rota
@RequestMapping("/cobrancas")
public class CobrancaController {
    private final CobrancaService cobrancaService;

    public CobrancaController(CobrancaService cobrancaService) {
        this.cobrancaService = cobrancaService;
    }

    @GetMapping
    public String inicio(Model model) {
        model.addAttribute("politicas", cobrancaService.listarPoliticas());
        model.addAttribute("faturas", cobrancaService.listarFaturas());
        model.addAttribute("acordos", cobrancaService.listarAcordos());
        model.addAttribute("historicos", cobrancaService.listarHistoricos());
        return "cobrancas/index";
    }

    @GetMapping("/politicas")
    public String politicas(Model model) {
        model.addAttribute("politica", new PoliticaCreditoJpaEntity());
        model.addAttribute("politicas", cobrancaService.listarPoliticas());
        return "cobrancas/politicas";
    }

    @PostMapping("/politicas")
    public String salvarPolitica(@ModelAttribute PoliticaCreditoJpaEntity politica, RedirectAttributes redirectAttributes) {
        cobrancaService.salvarPolitica(politica);
        redirectAttributes.addFlashAttribute("mensagem", "Politica de credito salva com sucesso.");
        return "redirect:/cobrancas/politicas";
    }

    @GetMapping("/faturas")
    public String faturas(Model model) {
        model.addAttribute("fatura", new FaturaJpaEntity());
        model.addAttribute("faturas", cobrancaService.listarFaturas());
        model.addAttribute("clientes", cobrancaService.listarClientesAtivos());
        model.addAttribute("acordos", cobrancaService.listarAcordos());
        return "cobrancas/faturas";
    }

    @PostMapping("/faturas")
    public String salvarFatura(@ModelAttribute FaturaJpaEntity fatura,
                               @RequestParam("clienteId") Integer clienteId,
                               @RequestParam(value = "acordoId", required = false) Integer acordoId,
                               RedirectAttributes redirectAttributes) {
        cobrancaService.salvarFatura(fatura, clienteId, acordoId);
        redirectAttributes.addFlashAttribute("mensagem", "Fatura salva com sucesso.");
        return "redirect:/cobrancas/faturas";
    }

    @GetMapping("/acordos")
    public String acordos(Model model) {
        model.addAttribute("acordo", new AcordoPagamentoJpaEntity());
        model.addAttribute("acordos", cobrancaService.listarAcordos());
        model.addAttribute("clientes", cobrancaService.listarClientesAtivos());
        return "cobrancas/acordos";
    }

    @PostMapping("/acordos")
    public String salvarAcordo(@ModelAttribute AcordoPagamentoJpaEntity acordo,
                               @RequestParam("clienteId") Integer clienteId,
                               RedirectAttributes redirectAttributes) {
        cobrancaService.salvarAcordo(acordo, clienteId);
        redirectAttributes.addFlashAttribute("mensagem", "Acordo salvo com sucesso.");
        return "redirect:/cobrancas/acordos";
    }

    @GetMapping("/historicos")
    public String historicos(Model model) {
        model.addAttribute("historico", new HistoricoCobrancaJpaEntity());
        model.addAttribute("historicos", cobrancaService.listarHistoricos());
        model.addAttribute("clientes", cobrancaService.listarClientesAtivos());
        model.addAttribute("faturas", cobrancaService.listarFaturas());
        return "cobrancas/historicos";
    }

    @PostMapping("/historicos")
    public String registrarHistorico(@ModelAttribute HistoricoCobrancaJpaEntity historico,
                                     @RequestParam("clienteId") Integer clienteId,
                                     @RequestParam(value = "faturaId", required = false) Integer faturaId,
                                     @RequestParam(value = "registroOriginalId", required = false) Integer registroOriginalId,
                                     RedirectAttributes redirectAttributes) {
        cobrancaService.registrarHistorico(historico, clienteId, faturaId, registroOriginalId);
        redirectAttributes.addFlashAttribute("mensagem", "Historico de cobranca registrado.");
        return "redirect:/cobrancas/historicos";
    }
}