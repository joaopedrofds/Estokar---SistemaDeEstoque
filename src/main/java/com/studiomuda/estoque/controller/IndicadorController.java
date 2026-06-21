package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.indicadores.domain.IndicadorId;
import com.studiomuda.estoque.indicadores.domain.IndicadorOperacional;
import com.studiomuda.estoque.indicadores.domain.MetaIndicador;
import com.studiomuda.estoque.indicadores.domain.MetaIndicadorId;
import com.studiomuda.estoque.security.dominio.IUsuarioAcessoRepositorio;
import com.studiomuda.estoque.security.dominio.UsuarioAcesso;
import com.studiomuda.estoque.service.IndicadorService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Controller
@RequestMapping("/kpis")
public class IndicadorController {
    private final IndicadorService indicadorService;
    private final IUsuarioAcessoRepositorio usuarioAcessoRepo;

    public IndicadorController(IndicadorService indicadorService,
                               IUsuarioAcessoRepositorio usuarioAcessoRepo) {
        this.indicadorService = indicadorService;
        this.usuarioAcessoRepo = usuarioAcessoRepo;
    }

    @GetMapping
    public String index(Model model) {
        try {
            List<IndicadorOperacional> indicadores = indicadorService.listarIndicadores();
            model.addAttribute("indicadores", indicadores);

            // Meta vigente e último snapshot por indicador
            for (IndicadorOperacional ind : indicadores) {
                model.addAttribute("meta_" + ind.getId(), indicadorService.buscarMetaVigente(ind.getId()));
                model.addAttribute("ultimo_snap_" + ind.getId(), indicadorService.buscarUltimoSnapshot(ind.getId()));
            }

            model.addAttribute("alertasAtivosCount", indicadorService.listarAlertas("ATIVO").size());
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao carregar indicadores: " + e.getMessage());
        }
        return "kpis/lista";
    }

    @GetMapping("/alertas")
    public String alertas(Model model) {
        try {
            model.addAttribute("alertasAtivos", indicadorService.listarAlertas("ATIVO"));
            model.addAttribute("alertasResolvidos", indicadorService.listarAlertas("RESOLVIDO"));
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao carregar alertas: " + e.getMessage());
        }
        return "kpis/alertas";
    }

    @PostMapping("/alertas/resolver")
    public String resolverAlerta(@RequestParam String id,
                                 @RequestParam String observacao,
                                 RedirectAttributes redirectAttributes) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            indicadorService.resolverAlerta(id, username, observacao);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Alerta operacional resolvido e registrado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao resolver alerta: " + e.getMessage());
        }
        return "redirect:/kpis/alertas";
    }

    @GetMapping("/snapshots")
    public String snapshots(Model model) {
        try {
            model.addAttribute("snapshots", indicadorService.listarSnapshots());
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao carregar histórico de snapshots: " + e.getMessage());
        }
        return "kpis/snapshots";
    }

    @PostMapping("/recalcular")
    public String recalcular(RedirectAttributes redirectAttributes) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            UsuarioAcesso usuario = usuarioAcessoRepo.buscarPorUsername(username).orElse(null);
            int usuarioId = usuario != null ? usuario.getId() : 0;

            // Define período do mês corrente
            LocalDate hoje = LocalDate.now();
            LocalDate inicio = hoje.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate fim = hoje.with(TemporalAdjusters.lastDayOfMonth());

            int recalculados = indicadorService.recalcularTodos(inicio, fim, usuarioId, username);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Recálculo de todos os " + recalculados + " indicadores finalizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao recalcular indicadores: " + e.getMessage());
        }
        return "redirect:/kpis";
    }

    @GetMapping("/meta/nova/{indicadorId}")
    public String formMeta(@PathVariable String indicadorId, Model model) {
        try {
            IndicadorId id = IndicadorId.de(indicadorId);
            IndicadorOperacional ind = indicadorService.buscarIndicador(id);
            if (ind == null) {
                return "redirect:/kpis";
            }

            MetaIndicador meta = indicadorService.buscarMetaVigente(id);
            if (meta == null) {
                meta = MetaIndicador.padraoPara(id);
            }

            model.addAttribute("indicador", ind);
            model.addAttribute("meta", meta);
            model.addAttribute("operadores", List.of("MAIOR_IGUAL", "MENOR_IGUAL"));
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao carregar formulário de metas: " + e.getMessage());
        }
        return "kpis/form-meta";
    }

    @PostMapping("/meta/salvar")
    public String salvarMeta(@RequestParam String indicadorId,
                             @RequestParam double valorAlvo,
                             @RequestParam double limiteCritico,
                             @RequestParam String operador,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate vigenciaInicio,
                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate vigenciaFim,
                             @RequestParam(defaultValue = "false") boolean ativo,
                             RedirectAttributes redirectAttributes) {
        try {
            MetaIndicador meta = new MetaIndicador(MetaIndicadorId.gerar(), IndicadorId.de(indicadorId), valorAlvo, limiteCritico,
                operador, vigenciaInicio, vigenciaFim, ativo);

            indicadorService.salvarMeta(meta);

            redirectAttributes.addFlashAttribute("mensagemSucesso", "Meta de indicadores operacionais configurada e persistida no banco com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar meta: " + e.getMessage());
        }
        return "redirect:/kpis";
    }
}
