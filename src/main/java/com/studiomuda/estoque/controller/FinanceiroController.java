package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.financeiro.application.dto.CategoriaFinanceiraDTO;
import com.studiomuda.estoque.financeiro.application.dto.TemplateRelatorioDTO;
import com.studiomuda.estoque.financeiro.domain.RelatorioGerado;
import com.studiomuda.estoque.security.UsuarioAutenticado;
import com.studiomuda.estoque.service.FinanceiroService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/financeiro")
public class FinanceiroController {
    private final FinanceiroService financeiroService;

    public FinanceiroController(FinanceiroService financeiroService) {
        this.financeiroService = financeiroService;
    }

    @GetMapping
    public String index() {
        return "redirect:/financeiro/relatorios";
    }

    @GetMapping("/categorias")
    public String categorias(@RequestParam(required = false) String editarId, Model model) {
        try {
            model.addAttribute("categorias", financeiroService.listarCategorias());
            CategoriaFinanceiraDTO form = CategoriaFinanceiraDTO.vazia();
            if (editarId != null && !editarId.isBlank()) {
                CategoriaFinanceiraDTO existente = financeiroService.buscarCategoria(editarId);
                if (existente != null) {
                    form = existente;
                }
            }
            model.addAttribute("categoriaForm", form);
            model.addAttribute("tipos", Arrays.asList("RECEITA", "DESPESA"));
            model.addAttribute("origens", Arrays.asList(
                    "PEDIDO_PAGO", "MOVIMENTACAO_SAIDA", "MOVIMENTACAO_ENTRADA_DEVOLUCAO", "LANCAMENTO_AJUSTE"));
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao carregar categorias: " + e.getMessage());
        }
        return "financeiro/categorias";
    }

    @PostMapping("/categorias/salvar")
    public String salvarCategoria(@RequestParam(required = false) String id,
                                  @RequestParam String nome,
                                  @RequestParam String tipo,
                                  @RequestParam(required = false) String origemSistema,
                                  @RequestParam(required = false) String descricao,
                                  @RequestParam(defaultValue = "false") boolean ativo,
                                  RedirectAttributes redirectAttributes) {
        boolean novo = (id == null || id.isBlank());
        try {
            financeiroService.salvarCategoria(id, nome.trim(), tipo,
                    origemSistema == null || origemSistema.isBlank() ? null : origemSistema.trim(),
                    descricao, ativo);
            redirectAttributes.addFlashAttribute("mensagem",
                    novo ? "Categoria financeira cadastrada." : "Categoria financeira atualizada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar categoria: " + e.getMessage());
        }
        return "redirect:/financeiro/categorias";
    }

    @GetMapping("/categorias/inativar/{id}")
    public String inativarCategoria(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            financeiroService.inativarCategoria(id);
            redirectAttributes.addFlashAttribute("mensagem", "Categoria inativada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao inativar categoria: " + e.getMessage());
        }
        return "redirect:/financeiro/categorias";
    }

    @GetMapping("/templates")
    public String templates(@RequestParam(required = false) String editarId, Model model) {
        try {
            model.addAttribute("templates", financeiroService.listarTemplates());
            model.addAttribute("categorias", financeiroService.listarCategoriasAtivas());
            model.addAttribute("indicadoresDisponiveis", Arrays.asList("MARGEM_BRUTA", "TICKET_MEDIO", "RESULTADO_LIQUIDO"));
            model.addAttribute("periodos", Arrays.asList("DIA", "SEMANA", "MES"));
            TemplateRelatorioDTO form = TemplateRelatorioDTO.vazio();
            if (editarId != null && !editarId.isBlank()) {
                TemplateRelatorioDTO existente = financeiroService.buscarTemplate(editarId);
                if (existente != null) {
                    form = existente;
                }
            }
            model.addAttribute("templateForm", form);
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao carregar templates: " + e.getMessage());
        }
        return "financeiro/templates";
    }

    @PostMapping("/templates/salvar")
    public String salvarTemplate(@RequestParam(required = false) String id,
                                 @RequestParam String nome,
                                 @RequestParam(required = false) String descricao,
                                 @RequestParam String periodoPadrao,
                                 @RequestParam String agrupamento,
                                 @RequestParam(defaultValue = "false") boolean ativo,
                                 @RequestParam(required = false) List<String> categoriaIds,
                                 @RequestParam(required = false) List<String> indicadores,
                                 RedirectAttributes redirectAttributes) {
        boolean novo = (id == null || id.isBlank());
        try {
            financeiroService.salvarTemplate(id, nome.trim(), descricao, periodoPadrao, agrupamento, ativo,
                    categoriaIds == null ? new ArrayList<>() : categoriaIds,
                    indicadores == null ? new ArrayList<>() : indicadores);
            redirectAttributes.addFlashAttribute("mensagem",
                    novo ? "Template de relatório cadastrado." : "Template de relatório atualizado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar template: " + e.getMessage());
        }
        return "redirect:/financeiro/templates";
    }

    @GetMapping("/templates/inativar/{id}")
    public String inativarTemplate(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            financeiroService.inativarTemplate(id);
            redirectAttributes.addFlashAttribute("mensagem", "Template inativado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao inativar template: " + e.getMessage());
        }
        return "redirect:/financeiro/templates";
    }

    @GetMapping("/relatorios")
    public String relatorios(@RequestParam(required = false) String templateId, Model model) {
        try {
            model.addAttribute("templates", financeiroService.listarTemplatesAtivos());
            model.addAttribute("historico", financeiroService.listarHistorico(50));
            LocalDate hoje = LocalDate.now();
            model.addAttribute("dataInicioPadrao", hoje.with(TemporalAdjusters.firstDayOfMonth()));
            model.addAttribute("dataFimPadrao", hoje);
            model.addAttribute("templateSelecionado", templateId);
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao carregar relatórios: " + e.getMessage());
        }
        return "financeiro/relatorios";
    }

    @PostMapping("/relatorios/gerar")
    public String gerarRelatorio(@RequestParam String templateId,
                                 @RequestParam String dataInicio,
                                 @RequestParam String dataFim,
                                 RedirectAttributes redirectAttributes) {
        try {
            UsuarioAutenticado usuario = obterUsuarioAutenticado();
            RelatorioGerado relatorio = financeiroService.gerarRelatorio(
                    templateId, LocalDate.parse(dataInicio), LocalDate.parse(dataFim),
                    usuario != null ? usuario.getId() : null,
                    usuario != null ? usuario.getUsername() : "sistema");
            redirectAttributes.addFlashAttribute("mensagem", "Relatório #" + relatorio.getId().getValor() + " gerado com sucesso.");
            return "redirect:/financeiro/relatorios/" + relatorio.getId().getValor();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao gerar relatório: " + e.getMessage());
            return "redirect:/financeiro/relatorios";
        }
    }

    @GetMapping("/relatorios/{id}")
    public String detalheRelatorio(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            RelatorioGerado relatorio = financeiroService.buscarRelatorio(id);
            if (relatorio == null) {
                redirectAttributes.addFlashAttribute("mensagemErro", "Relatório não encontrado.");
                return "redirect:/financeiro/relatorios";
            }
            model.addAttribute("relatorio", relatorio);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao carregar relatório: " + e.getMessage());
            return "redirect:/financeiro/relatorios";
        }
        return "financeiro/relatorio-detalhe";
    }

    @GetMapping("/relatorios/{id}/exportar")
    public void exportarRelatorio(@PathVariable String id, HttpServletResponse response) throws IOException {
        try {
            RelatorioGerado relatorio = financeiroService.buscarRelatorio(id);
            if (relatorio == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=relatorio-financeiro-" + id + ".csv");
            try (PrintWriter writer = response.getWriter()) {
                writer.println("Relatório Financeiro #" + relatorio.getId().getValor());
                writer.println("Template;" + relatorio.getTemplateNome());
                writer.println("Período;" + relatorio.getDataInicio() + " a " + relatorio.getDataFim());
                writer.println("Gerado por;" + relatorio.getGeradoPorUsername());
                writer.println("Gerado em;" + relatorio.getDataGeracao());
                writer.println();
                writer.println("Categoria;Tipo;Valor Período;Valor Período Anterior;Variação %;Rastreio;Ajuste Manual");
                for (var linha : relatorio.getLinhasCategoria()) {
                    writer.printf("%s;%s;%.2f;%.2f;%s;%s;%s%n",
                            linha.getCategoriaNome(), linha.getTipoCategoria(),
                            linha.getValorPeriodo(), linha.getValorPeriodoAnterior(),
                            linha.getVariacaoPercentual() != null ? String.format("%.2f", linha.getVariacaoPercentual()) : "",
                            linha.getOrigemRastreio(), linha.isAjusteManual() ? "SIM" : "NAO");
                }
                writer.println();
                writer.println("Indicador;Valor;Valor Anterior;Variação %;Fórmula");
                for (var linha : relatorio.getLinhasIndicador()) {
                    writer.printf("%s;%.4f;%s;%s;%s%n",
                            linha.getIndicador(), linha.getValor(),
                            linha.getValorAnterior() != null ? String.format("%.4f", linha.getValorAnterior()) : "",
                            linha.getVariacaoPercentual() != null ? String.format("%.2f", linha.getVariacaoPercentual()) : "",
                            linha.getFormulaDescricao());
                }
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("/ajustes")
    public String ajustes(Model model) {
        try {
            model.addAttribute("ajustes", financeiroService.listarAjustes());
            model.addAttribute("categorias", financeiroService.listarCategoriasAtivas());
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao carregar ajustes: " + e.getMessage());
        }
        return "financeiro/ajustes";
    }

    @PostMapping("/ajustes/salvar")
    public String salvarAjuste(@RequestParam String categoriaId,
                               @RequestParam String dataLancamento,
                               @RequestParam double valor,
                               @RequestParam String descricao,
                               RedirectAttributes redirectAttributes) {
        try {
            UsuarioAutenticado usuario = obterUsuarioAutenticado();
            financeiroService.registrarAjuste(categoriaId, LocalDate.parse(dataLancamento), valor, descricao.trim(),
                    usuario != null ? usuario.getId() : null,
                    usuario != null ? usuario.getUsername() : "sistema");
            redirectAttributes.addFlashAttribute("mensagem", "Lançamento de ajuste registrado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao registrar ajuste: " + e.getMessage());
        }
        return "redirect:/financeiro/ajustes";
    }

    private UsuarioAutenticado obterUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UsuarioAutenticado) {
            return (UsuarioAutenticado) authentication.getPrincipal();
        }
        return null;
    }
}
