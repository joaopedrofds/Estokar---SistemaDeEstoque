package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.jpa.entity.BeneficioCategoriaJpaEntity;
import com.studiomuda.estoque.jpa.entity.FaixaFidelidadeJpaEntity;
import com.studiomuda.estoque.jpa.repository.FaixaFidelidadeJpaRepository;
import com.studiomuda.estoque.service.FidelidadeService;
import com.studiomuda.estoque.service.RetencaoService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@ConditionalOnBean(FaixaFidelidadeJpaRepository.class)
@RequestMapping("/engajamento")
public class EngajamentoController {
    private final FidelidadeService fidelidadeService;
    private final RetencaoService retencaoService;

    public EngajamentoController(FidelidadeService fidelidadeService, RetencaoService retencaoService) {
        this.fidelidadeService = fidelidadeService;
        this.retencaoService = retencaoService;
    }

    @GetMapping
    public String inicio(Model model) {
        model.addAttribute("faixas", fidelidadeService.listarFaixas());
        model.addAttribute("beneficios", fidelidadeService.listarBeneficios());
        model.addAttribute("clientes", fidelidadeService.listarClientes());
        model.addAttribute("acoes", retencaoService.listarAcoes());
        return "engajamento/index";
    }

    @GetMapping("/faixas")
    public String faixas(Model model) {
        model.addAttribute("faixa", new FaixaFidelidadeJpaEntity());
        model.addAttribute("faixas", fidelidadeService.listarFaixas());
        return "engajamento/faixas";
    }

    @PostMapping("/faixas")
    public String salvarFaixa(@ModelAttribute FaixaFidelidadeJpaEntity faixa, RedirectAttributes redirectAttributes) {
        try {
            fidelidadeService.salvarFaixa(faixa);
            redirectAttributes.addFlashAttribute("mensagem", "Faixa de fidelidade salva.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/engajamento/faixas";
    }

    @GetMapping("/beneficios")
    public String beneficios(Model model) {
        model.addAttribute("beneficio", new BeneficioCategoriaJpaEntity());
        model.addAttribute("beneficios", fidelidadeService.listarBeneficios());
        model.addAttribute("faixas", fidelidadeService.listarFaixas());
        return "engajamento/beneficios";
    }

    @PostMapping("/beneficios")
    public String salvarBeneficio(@ModelAttribute BeneficioCategoriaJpaEntity beneficio,
                                  @RequestParam("faixaId") Integer faixaId,
                                  RedirectAttributes redirectAttributes) {
        try {
            fidelidadeService.salvarBeneficio(beneficio, faixaId);
            redirectAttributes.addFlashAttribute("mensagem", "Beneficio salvo.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/engajamento/beneficios";
    }

    @GetMapping("/clientes")
    public String clientes(Model model) {
        model.addAttribute("clientes", fidelidadeService.listarClientes());
        return "engajamento/clientes";
    }

    @PostMapping("/clientes/{id}/recalcular")
    public String recalcular(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            fidelidadeService.recalcularCategoria(id);
            redirectAttributes.addFlashAttribute("mensagem", "Categoria recalculada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/engajamento/clientes";
    }

    @GetMapping("/retencao")
    public String retencao(Model model) {
        model.addAttribute("clientes", retencaoService.listarClientesElegiveis());
        model.addAttribute("acoes", retencaoService.listarAcoes());
        return "engajamento/retencao";
    }

    @PostMapping("/retencao")
    public String gerarRetencao(@RequestParam("clienteId") Integer clienteId,
                                @RequestParam("percentual") BigDecimal percentual,
                                @RequestParam("validade") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validade,
                                RedirectAttributes redirectAttributes) {
        try {
            retencaoService.gerarAcao(clienteId, percentual, validade);
            redirectAttributes.addFlashAttribute("mensagem", "Cupom de retencao gerado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/engajamento/retencao";
    }

    @PostMapping("/retencao/{id}/encerrar")
    public String encerrarRetencao(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        retencaoService.encerrar(id);
        redirectAttributes.addFlashAttribute("mensagem", "Acao de retencao encerrada.");
        return "redirect:/engajamento/retencao";
    }
}
