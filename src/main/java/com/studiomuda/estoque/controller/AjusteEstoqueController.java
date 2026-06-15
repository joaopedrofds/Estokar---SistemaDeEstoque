package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.jpa.entity.SolicitacaoAjusteEstoqueJpaEntity;
import com.studiomuda.estoque.security.UsuarioAutenticado;
import com.studiomuda.estoque.service.AjusteEstoqueService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ajustes-estoque")
public class AjusteEstoqueController {
    private final AjusteEstoqueService ajusteEstoqueService;

    public AjusteEstoqueController(AjusteEstoqueService ajusteEstoqueService) {
        this.ajusteEstoqueService = ajusteEstoqueService;
    }

    @GetMapping
    public String listar(@RequestParam(value = "status", required = false) String status, Model model) {
        List<SolicitacaoAjusteEstoqueJpaEntity> solicitacoes = ajusteEstoqueService.listarSolicitacoes(status);
        long pendentes = solicitacoes.stream()
                .filter(solicitacao -> "PENDENTE_APROVACAO".equals(solicitacao.getStatus()))
                .count();
        long aplicadas = solicitacoes.stream()
                .filter(solicitacao -> "APLICADO_AUTOMATICAMENTE".equals(solicitacao.getStatus())
                        || "APROVADO".equals(solicitacao.getStatus()))
                .count();

        model.addAttribute("solicitacoes", solicitacoes);
        model.addAttribute("statusSelecionado", status);
        model.addAttribute("pendentes", pendentes);
        model.addAttribute("aplicadas", aplicadas);
        model.addAttribute("parametros", ajusteEstoqueService.buscarParametros());
        return "ajustes-estoque/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("produtos", ajusteEstoqueService.listarProdutos());
        model.addAttribute("parametros", ajusteEstoqueService.buscarParametros());
        return "ajustes-estoque/form";
    }

    @PostMapping
    public String solicitar(@RequestParam("produtoId") Integer produtoId,
                            @RequestParam("tipo") String tipo,
                            @RequestParam("quantidade") Integer quantidade,
                            @RequestParam("justificativa") String justificativa,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        try {
            SolicitacaoAjusteEstoqueJpaEntity solicitacao = ajusteEstoqueService.solicitarAjuste(
                    produtoId,
                    tipo,
                    quantidade,
                    justificativa,
                    usuarioOperacao(authentication)
            );
            if ("PENDENTE_APROVACAO".equals(solicitacao.getStatus())) {
                redirectAttributes.addFlashAttribute("mensagemAviso",
                        "Ajuste registrado e retido para aprovacao gerencial.");
            } else {
                redirectAttributes.addFlashAttribute("mensagemSucesso",
                        "Ajuste aplicado automaticamente e movimentacao registrada.");
            }
            return "redirect:/ajustes-estoque/" + solicitacao.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao solicitar ajuste: " + e.getMessage());
            return "redirect:/ajustes-estoque/novo";
        }
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable("id") Integer id,
                          Authentication authentication,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("detalhe", ajusteEstoqueService.buscarDetalhe(id));
            model.addAttribute("podeAprovar", ajusteEstoqueService.possuiAlcadaAprovacao(autoridades(authentication)));
            return "ajustes-estoque/detalhe";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao carregar ajuste: " + e.getMessage());
            return "redirect:/ajustes-estoque";
        }
    }

    @PostMapping("/{id}/aprovar")
    public String aprovar(@PathVariable("id") Integer id,
                          @RequestParam("motivoDecisao") String motivoDecisao,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        try {
            ajusteEstoqueService.aprovar(id, motivoDecisao, usuarioOperacao(authentication), autoridades(authentication));
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Ajuste aprovado e aplicado ao estoque.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao aprovar ajuste: " + e.getMessage());
        }
        return "redirect:/ajustes-estoque/" + id;
    }

    @PostMapping("/{id}/reprovar")
    public String reprovar(@PathVariable("id") Integer id,
                           @RequestParam("motivoDecisao") String motivoDecisao,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        try {
            ajusteEstoqueService.reprovar(id, motivoDecisao, usuarioOperacao(authentication), autoridades(authentication));
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Ajuste reprovado sem alterar o saldo.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao reprovar ajuste: " + e.getMessage());
        }
        return "redirect:/ajustes-estoque/" + id;
    }

    private AjusteEstoqueService.UsuarioOperacao usuarioOperacao(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UsuarioAutenticado) {
            UsuarioAutenticado usuario = (UsuarioAutenticado) authentication.getPrincipal();
            return new AjusteEstoqueService.UsuarioOperacao(usuario.getId(), usuario.getNome());
        }
        String nome = authentication != null ? authentication.getName() : "Usuario";
        return new AjusteEstoqueService.UsuarioOperacao(1, nome);
    }

    private Collection<String> autoridades(Authentication authentication) {
        if (authentication == null) {
            return java.util.Collections.emptyList();
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }
}
