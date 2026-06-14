package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.jpa.entity.SessaoInventarioJpaEntity;
import com.studiomuda.estoque.security.UsuarioAutenticado;
import com.studiomuda.estoque.service.InventarioService;
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
@RequestMapping("/inventarios")
public class InventarioController {
    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public String listar(Model model) {
        List<SessaoInventarioJpaEntity> sessoes = inventarioService.listarSessoes();
        long emAndamento = sessoes.stream()
                .filter(sessao -> InventarioService.STATUS_EM_ANDAMENTO.equals(sessao.getStatus()))
                .count();
        long aguardando = sessoes.stream()
                .filter(sessao -> InventarioService.STATUS_AGUARDANDO_APROVACAO.equals(sessao.getStatus()))
                .count();

        model.addAttribute("sessoes", sessoes);
        model.addAttribute("emAndamento", emAndamento);
        model.addAttribute("aguardando", aguardando);
        model.addAttribute("tolerancia", inventarioService.buscarToleranciaQuantidade());
        return "inventarios/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("tolerancia", inventarioService.buscarToleranciaQuantidade());
        return "inventarios/form";
    }

    @PostMapping("/abrir")
    public String abrir(@RequestParam("setor") String setor,
                        @RequestParam(value = "bloqueiaSaidas", defaultValue = "false") boolean bloqueiaSaidas,
                        Authentication authentication,
                        RedirectAttributes redirectAttributes) {
        try {
            SessaoInventarioJpaEntity sessao = inventarioService.abrirSessao(
                    setor,
                    usuarioOperacao(authentication),
                    bloqueiaSaidas
            );
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Inventario aberto em andamento.");
            return "redirect:/inventarios/" + sessao.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao abrir inventario: " + e.getMessage());
            return "redirect:/inventarios/novo";
        }
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("detalhe", inventarioService.buscarDetalhe(id));
            model.addAttribute("produtos", inventarioService.listarProdutos());
            return "inventarios/detalhe";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao carregar inventario: " + e.getMessage());
            return "redirect:/inventarios";
        }
    }

    @PostMapping("/{id}/contagens")
    public String registrarContagem(@PathVariable("id") int id,
                                    @RequestParam("produtoId") int produtoId,
                                    @RequestParam("quantidadeFisica") int quantidadeFisica,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            inventarioService.registrarContagem(
                    id,
                    produtoId,
                    quantidadeFisica,
                    usuarioOperacao(authentication)
            );
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Contagem registrada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao registrar contagem: " + e.getMessage());
        }
        return "redirect:/inventarios/" + id;
    }

    @PostMapping("/{id}/fechar")
    public String fechar(@PathVariable("id") int id,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        try {
            InventarioService.ResultadoFechamento resultado = inventarioService.fecharSessao(
                    id,
                    usuarioOperacao(authentication),
                    autoridades(authentication)
            );
            if (InventarioService.STATUS_AGUARDANDO_APROVACAO.equals(resultado.getStatus())) {
                redirectAttributes.addFlashAttribute("mensagemAviso",
                        "Fechamento retido: divergencia acima da tolerancia exige aprovacao gerencial.");
            } else {
                redirectAttributes.addFlashAttribute("mensagemSucesso", "Inventario fechado e saldos ajustados.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao fechar inventario: " + e.getMessage());
        }
        return "redirect:/inventarios/" + id;
    }

    private InventarioService.UsuarioOperacao usuarioOperacao(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UsuarioAutenticado) {
            UsuarioAutenticado usuario = (UsuarioAutenticado) authentication.getPrincipal();
            return new InventarioService.UsuarioOperacao(usuario.getId(), usuario.getNome());
        }
        String nome = authentication != null ? authentication.getName() : "Usuario";
        return new InventarioService.UsuarioOperacao(1, nome);
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
