package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.precificacao.application.command.SalvarParametrosPrecificacaoCommand;
import com.studiomuda.estoque.precificacao.application.command.SalvarPoliticaPrecificacaoCommand;
import com.studiomuda.estoque.precificacao.application.command.SimularPrecoCommand;
import com.studiomuda.estoque.precificacao.application.dto.PainelPrecificacaoView;
import com.studiomuda.estoque.precificacao.application.dto.ResultadoPrecificacaoView;
import com.studiomuda.estoque.precificacao.application.service.PrecificacaoDinamicaApplicationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller de apresentação da funcionalidade 3.
 * Mantém compatibilidade com as rotas antigas e expõe o fluxo novo baseado em JPA.
 */
@Controller
@RequestMapping("/precificacao")
public class PrecificacaoController {

    private final PrecificacaoDinamicaApplicationService service;

    public PrecificacaoController(PrecificacaoDinamicaApplicationService service) {
        this.service = service;
    }

    @GetMapping
    public String index() {
        return "redirect:/precificacao/simular";
    }

    @GetMapping("/simular")
    public String telaSimulador(@RequestParam(required = false) Integer produtoId, Model model) {
        carregarPainel(model);
        if (produtoId != null) {
            model.addAttribute("produtoSelecionado", produtoId);
        }
        return "precificacao/simular";
    }

    @GetMapping("/simular/{produtoId}")
    public String simularPorProduto(@PathVariable int produtoId, Authentication auth, Model model) {
        carregarPainel(model);
        model.addAttribute("produtoSelecionado", produtoId);
        SimularPrecoCommand command = new SimularPrecoCommand();
        command.setProdutoId(produtoId);
        try {
            ResultadoPrecificacaoView resultado = service.simular(command, usuario(auth));
            model.addAttribute("resultado", resultado);
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao simular: " + e.getMessage());
        }
        return "precificacao/simular";
    }

    @PostMapping("/simular")
    public String simular(@ModelAttribute SimularPrecoCommand command,
                          Authentication auth,
                          Model model) {
        carregarPainel(model);
        model.addAttribute("produtoSelecionado", command.getProdutoId());
        try {
            ResultadoPrecificacaoView resultado = service.simular(command, usuario(auth));
            model.addAttribute("resultado", resultado);
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao simular: " + e.getMessage());
        }
        return "precificacao/simular";
    }

    @PostMapping("/aplicar/{id}")
    public String aplicarPrecoPorSimulacao(@PathVariable("id") Long simulacaoId,
                                           Authentication auth,
                                           RedirectAttributes redirect) {
        try {
            service.aplicarPreco(simulacaoId, usuario(auth));
            redirect.addFlashAttribute("sucesso", "Preço aplicado com sucesso! Histórico atualizado.");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/precificacao/historico";
    }

    /**
     * Compatibilidade com a rota antiga que aplicava preço diretamente pelo produto.
     */
    @PostMapping("/aplicar")
    public String aplicarPrecoLegado(@RequestParam int produtoId,
                                     @RequestParam double precoNovo,
                                     Authentication auth,
                                     RedirectAttributes redirect) {
        try {
            service.aplicarPrecoLegado(produtoId, java.math.BigDecimal.valueOf(precoNovo), usuario(auth));
            redirect.addFlashAttribute("sucesso", "Preço aplicado com sucesso! Histórico atualizado.");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/precificacao/simular/" + produtoId;
    }

    @GetMapping("/regras")
    public String regras(Model model) {
        carregarPainel(model);
        model.addAttribute("novaPolitica", service.novaPoliticaPadrao());
        return "precificacao/regras";
    }

    @GetMapping("/regras/editar/{id}")
    public String editarRegra(@PathVariable Long id, Model model) {
        carregarPainel(model);
        model.addAttribute("novaPolitica", service.carregarPoliticaParaEdicao(id));
        model.addAttribute("modoEdicao", true);
        return "precificacao/regras";
    }

    @PostMapping("/regras/salvar")
    public String salvarRegra(@ModelAttribute SalvarPoliticaPrecificacaoCommand command,
                              RedirectAttributes redirect) {
        try {
            service.salvarPolitica(command);
            redirect.addFlashAttribute("sucesso", command.getId() == null
                    ? "Política salva com sucesso!"
                    : "Política atualizada com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/precificacao/regras";
    }

    @PostMapping("/regras/excluir/{id}")
    public String excluirRegra(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.excluirPolitica(id);
            redirect.addFlashAttribute("sucesso", "Política desativada com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/precificacao/regras";
    }

    @GetMapping("/historico")
    public String historico(@RequestParam(required = false) String status, Model model) {
        model.addAttribute("simulacoes", service.listarHistorico(status));
        model.addAttribute("statusFiltro", status);
        return "precificacao/historico";
    }

    @GetMapping("/parametros")
    public String parametros(Model model) {
        model.addAttribute("params", service.buscarParametros());
        return "precificacao/parametros";
    }

    @PostMapping("/parametros/salvar")
    public String salvarParametros(@ModelAttribute SalvarParametrosPrecificacaoCommand command,
                                   RedirectAttributes redirect) {
        try {
            service.salvarParametros(command);
            redirect.addFlashAttribute("sucesso", "Parâmetros atualizados com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/precificacao/parametros";
    }

    private void carregarPainel(Model model) {
        PainelPrecificacaoView painel = service.montarPainel();
        model.addAttribute("painel", painel);
        model.addAttribute("produtos", painel.getProdutos());
        model.addAttribute("politicas", painel.getPoliticas());
        model.addAttribute("simulacoesRecentes", painel.getSimulacoesRecentes());
        model.addAttribute("kpis", painel.getKpis());
        model.addAttribute("params", painel.getParametros());
    }

    private String usuario(Authentication auth) {
        return auth != null ? auth.getName() : "sistema";
    }
}
