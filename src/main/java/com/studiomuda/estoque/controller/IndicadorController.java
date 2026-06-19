package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.dao.AlertaIndicadorDAO;
import com.studiomuda.estoque.dao.IndicadorOperacionalDAO;
import com.studiomuda.estoque.dao.MetaIndicadorDAO;
import com.studiomuda.estoque.dao.SnapshotIndicadorDAO;
import com.studiomuda.estoque.dao.UsuarioAcessoDAO;
import com.studiomuda.estoque.model.AlertaIndicador;
import com.studiomuda.estoque.model.IndicadorOperacional;
import com.studiomuda.estoque.model.MetaIndicador;
import com.studiomuda.estoque.model.UsuarioAcesso;
import com.studiomuda.estoque.service.IndicadorService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Controller
@RequestMapping("/kpis")
public class IndicadorController {
    private final IndicadorOperacionalDAO indicadorDAO = new IndicadorOperacionalDAO();
    private final MetaIndicadorDAO metaDAO = new MetaIndicadorDAO();
    private final SnapshotIndicadorDAO snapshotDAO = new SnapshotIndicadorDAO();
    private final AlertaIndicadorDAO alertaDAO = new AlertaIndicadorDAO();
    private final UsuarioAcessoDAO usuarioDAO = new UsuarioAcessoDAO();
    private final IndicadorService indicadorService = new IndicadorService();

    @GetMapping
    public String index(Model model) {
        try {
            List<IndicadorOperacional> indicadores = indicadorDAO.listarTodos();
            model.addAttribute("indicadores", indicadores);
            
            // Adicionar metas ativas atuais
            for (IndicadorOperacional ind : indicadores) {
                MetaIndicador meta = metaDAO.buscarAtivaPorIndicador(ind.getId());
                model.addAttribute("meta_" + ind.getId(), meta);
                
                // Buscar último snapshot para exibir valor atualizado
                model.addAttribute("ultimo_snap_" + ind.getId(), snapshotDAO.buscarUltimoPorIndicador(ind.getId()));
            }
            
            model.addAttribute("alertasAtivosCount", alertaDAO.listarPorStatus("ATIVO").size());
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao carregar indicadores: " + e.getMessage());
        }
        return "kpis/lista";
    }

    @GetMapping("/alertas")
    public String alertas(Model model) {
        try {
            model.addAttribute("alertasAtivos", alertaDAO.listarPorStatus("ATIVO"));
            model.addAttribute("alertasResolvidos", alertaDAO.listarPorStatus("RESOLVIDO"));
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao carregar alertas: " + e.getMessage());
        }
        return "kpis/alertas";
    }

    @PostMapping("/alertas/resolver")
    public String resolverAlerta(@RequestParam int id, 
                                 @RequestParam String observacao, 
                                 RedirectAttributes redirectAttributes) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            alertaDAO.resolverAlerta(id, username, observacao);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Alerta operacional resolvido e registrado com sucesso!");
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao resolver alerta: " + e.getMessage());
        }
        return "redirect:/kpis/alertas";
    }

    @GetMapping("/snapshots")
    public String snapshots(Model model) {
        try {
            model.addAttribute("snapshots", snapshotDAO.listarTodos());
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao carregar histórico de snapshots: " + e.getMessage());
        }
        return "kpis/snapshots";
    }

    @PostMapping("/recalcular")
    public String recalcular(RedirectAttributes redirectAttributes) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            UsuarioAcesso usuario = usuarioDAO.buscarPorUsername(username);
            int usuarioId = usuario != null ? usuario.getId() : 0;

            // Define período do mês corrente
            LocalDate hoje = LocalDate.now();
            LocalDate inicio = hoje.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate fim = hoje.with(TemporalAdjusters.lastDayOfMonth());

            int recalculados = indicadorService.recalcularTodos(inicio, fim, usuarioId, username);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Recálculo de todos os " + recalculados + " indicadores finalizado com sucesso!");
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao recalcular indicadores: " + e.getMessage());
        }
        return "redirect:/kpis";
    }

    @GetMapping("/meta/nova/{indicadorId}")
    public String formMeta(@PathVariable int indicadorId, Model model) {
        try {
            IndicadorOperacional ind = indicadorDAO.buscarPorId(indicadorId);
            if (ind == null) {
                return "redirect:/kpis";
            }
            
            MetaIndicador meta = metaDAO.buscarAtivaPorIndicador(indicadorId);
            if (meta == null) {
                meta = new MetaIndicador();
                meta.setIndicadorId(indicadorId);
                meta.setOperador("MAIOR_IGUAL");
                meta.setVigenciaInicio(LocalDate.now());
                meta.setAtivo(true);
            }
            
            model.addAttribute("indicador", ind);
            model.addAttribute("meta", meta);
            model.addAttribute("operadores", List.of("MAIOR_IGUAL", "MENOR_IGUAL"));
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao carregar formulário de metas: " + e.getMessage());
        }
        return "kpis/form-meta";
    }

    @PostMapping("/meta/salvar")
    public String salvarMeta(@RequestParam int indicadorId,
                             @RequestParam double valorAlvo,
                             @RequestParam double limiteCritico,
                             @RequestParam String operador,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate vigenciaInicio,
                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate vigenciaFim,
                             @RequestParam(defaultValue = "false") boolean ativo,
                             RedirectAttributes redirectAttributes) {
        try {
            MetaIndicador meta = new MetaIndicador();
            meta.setIndicadorId(indicadorId);
            meta.setValorAlvo(valorAlvo);
            meta.setLimiteCritico(limiteCritico);
            meta.setOperador(operador);
            meta.setVigenciaInicio(vigenciaInicio);
            meta.setVigenciaFim(vigenciaFim);
            meta.setAtivo(ativo);

            metaDAO.inserir(meta);
            
            if (ativo) {
                metaDAO.desativarOutrasMetas(indicadorId, meta.getId());
            }
            
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Meta de indicadores operacionais configurada e persistida no banco com sucesso!");
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar meta: " + e.getMessage());
        }
        return "redirect:/kpis";
    }
}
