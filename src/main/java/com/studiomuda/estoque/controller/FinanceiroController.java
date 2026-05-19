package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.dao.CategoriaFinanceiraDAO;
import com.studiomuda.estoque.dao.LancamentoAjusteDAO;
import com.studiomuda.estoque.dao.RelatorioDAO;
import com.studiomuda.estoque.dao.TemplateRelatorioDAO;
import com.studiomuda.estoque.model.CategoriaFinanceira;
import com.studiomuda.estoque.model.LancamentoAjuste;
import com.studiomuda.estoque.model.RelatorioGerado;
import com.studiomuda.estoque.model.TemplateRelatorio;
import com.studiomuda.estoque.security.UsuarioAutenticado;
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
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/financeiro")
public class FinanceiroController {
    private final CategoriaFinanceiraDAO categoriaFinanceiraDAO = new CategoriaFinanceiraDAO();
    private final TemplateRelatorioDAO templateRelatorioDAO = new TemplateRelatorioDAO();
    private final RelatorioDAO relatorioDAO = new RelatorioDAO();
    private final LancamentoAjusteDAO lancamentoAjusteDAO = new LancamentoAjusteDAO();

    @GetMapping
    public String index() {
        return "redirect:/financeiro/relatorios";
    }

    @GetMapping("/categorias")
    public String categorias(@RequestParam(required = false) Integer editarId, Model model) {
        try {
            model.addAttribute("categorias", categoriaFinanceiraDAO.listarTodos());
            CategoriaFinanceira form = new CategoriaFinanceira();
            if (editarId != null) {
                CategoriaFinanceira existente = categoriaFinanceiraDAO.buscarPorId(editarId);
                if (existente != null) {
                    form = existente;
                }
            }
            model.addAttribute("categoriaForm", form);
            model.addAttribute("tipos", Arrays.asList("RECEITA", "DESPESA"));
            model.addAttribute("origens", Arrays.asList(
                    "PEDIDO_PAGO",
                    "MOVIMENTACAO_SAIDA",
                    "MOVIMENTACAO_ENTRADA_DEVOLUCAO",
                    "LANCAMENTO_AJUSTE"
            ));
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao carregar categorias: " + e.getMessage());
        }
        return "financeiro/categorias";
    }

    @PostMapping("/categorias/salvar")
    public String salvarCategoria(@RequestParam int id,
                                  @RequestParam String nome,
                                  @RequestParam String tipo,
                                  @RequestParam(required = false) String origemSistema,
                                  @RequestParam(required = false) String descricao,
                                  @RequestParam(defaultValue = "false") boolean ativo,
                                  RedirectAttributes redirectAttributes) {
        try {
            CategoriaFinanceira categoria = new CategoriaFinanceira();
            categoria.setId(id);
            categoria.setNome(nome.trim());
            categoria.setTipo(tipo);
            categoria.setOrigemSistema(origemSistema == null || origemSistema.isBlank() ? null : origemSistema.trim());
            categoria.setDescricao(descricao);
            categoria.setAtivo(ativo);
            if (id == 0) {
                categoriaFinanceiraDAO.inserir(categoria);
                redirectAttributes.addFlashAttribute("mensagem", "Categoria financeira cadastrada.");
            } else {
                categoriaFinanceiraDAO.atualizar(categoria);
                redirectAttributes.addFlashAttribute("mensagem", "Categoria financeira atualizada.");
            }
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar categoria: " + e.getMessage());
        }
        return "redirect:/financeiro/categorias";
    }

    @GetMapping("/categorias/inativar/{id}")
    public String inativarCategoria(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            categoriaFinanceiraDAO.inativar(id);
            redirectAttributes.addFlashAttribute("mensagem", "Categoria inativada.");
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao inativar categoria: " + e.getMessage());
        }
        return "redirect:/financeiro/categorias";
    }

    @GetMapping("/templates")
    public String templates(@RequestParam(required = false) Integer editarId, Model model) {
        try {
            model.addAttribute("templates", templateRelatorioDAO.listarTodos());
            model.addAttribute("categorias", categoriaFinanceiraDAO.listarAtivas());
            model.addAttribute("indicadoresDisponiveis", Arrays.asList("MARGEM_BRUTA", "TICKET_MEDIO", "RESULTADO_LIQUIDO"));
            model.addAttribute("periodos", Arrays.asList("DIA", "SEMANA", "MES"));
            TemplateRelatorio form = new TemplateRelatorio();
            if (editarId != null) {
                TemplateRelatorio existente = templateRelatorioDAO.buscarPorId(editarId);
                if (existente != null) {
                    form = existente;
                }
            }
            model.addAttribute("templateForm", form);
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao carregar templates: " + e.getMessage());
        }
        return "financeiro/templates";
    }

    @PostMapping("/templates/salvar")
    public String salvarTemplate(@RequestParam int id,
                                   @RequestParam String nome,
                                   @RequestParam(required = false) String descricao,
                                   @RequestParam String periodoPadrao,
                                   @RequestParam String agrupamento,
                                   @RequestParam(defaultValue = "false") boolean ativo,
                                   @RequestParam(required = false) List<Integer> categoriaIds,
                                   @RequestParam(required = false) List<String> indicadores,
                                   RedirectAttributes redirectAttributes) {
        try {
            TemplateRelatorio template = new TemplateRelatorio();
            template.setId(id);
            template.setNome(nome.trim());
            template.setDescricao(descricao);
            template.setPeriodoPadrao(periodoPadrao);
            template.setAgrupamento(agrupamento);
            template.setAtivo(ativo);
            template.setCategoriaIds(categoriaIds == null ? new ArrayList<>() : categoriaIds);
            template.setIndicadores(indicadores == null ? new ArrayList<>() : indicadores);
            if (id == 0) {
                templateRelatorioDAO.inserir(template);
                redirectAttributes.addFlashAttribute("mensagem", "Template de relatório cadastrado.");
            } else {
                templateRelatorioDAO.atualizar(template);
                redirectAttributes.addFlashAttribute("mensagem", "Template de relatório atualizado.");
            }
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar template: " + e.getMessage());
        }
        return "redirect:/financeiro/templates";
    }

    @GetMapping("/templates/inativar/{id}")
    public String inativarTemplate(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            templateRelatorioDAO.inativar(id);
            redirectAttributes.addFlashAttribute("mensagem", "Template inativado.");
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao inativar template: " + e.getMessage());
        }
        return "redirect:/financeiro/templates";
    }

    @GetMapping("/relatorios")
    public String relatorios(@RequestParam(required = false) Integer templateId, Model model) {
        try {
            List<TemplateRelatorio> templates = templateRelatorioDAO.listarAtivos();
            model.addAttribute("templates", templates);
            model.addAttribute("historico", relatorioDAO.listarHistorico(50));

            LocalDate hoje = LocalDate.now();
            LocalDate inicioMes = hoje.with(TemporalAdjusters.firstDayOfMonth());
            model.addAttribute("dataInicioPadrao", inicioMes);
            model.addAttribute("dataFimPadrao", hoje);
            model.addAttribute("templateSelecionado", templateId);
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao carregar relatórios: " + e.getMessage());
        }
        return "financeiro/relatorios";
    }

    @PostMapping("/relatorios/gerar")
    public String gerarRelatorio(@RequestParam int templateId,
                                 @RequestParam String dataInicio,
                                 @RequestParam String dataFim,
                                 RedirectAttributes redirectAttributes) {
        try {
            UsuarioAutenticado usuario = obterUsuarioAutenticado();
            RelatorioGerado relatorio = relatorioDAO.gerarRelatorio(
                    templateId,
                    LocalDate.parse(dataInicio),
                    LocalDate.parse(dataFim),
                    usuario != null ? usuario.getId() : null,
                    usuario != null ? usuario.getUsername() : "sistema"
            );
            redirectAttributes.addFlashAttribute("mensagem", "Relatório #" + relatorio.getId() + " gerado com sucesso.");
            return "redirect:/financeiro/relatorios/" + relatorio.getId();
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao gerar relatório: " + e.getMessage());
            return "redirect:/financeiro/relatorios";
        }
    }

    @GetMapping("/relatorios/{id}")
    public String detalheRelatorio(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            RelatorioGerado relatorio = relatorioDAO.buscarPorId(id);
            if (relatorio == null) {
                redirectAttributes.addFlashAttribute("mensagemErro", "Relatório não encontrado.");
                return "redirect:/financeiro/relatorios";
            }
            model.addAttribute("relatorio", relatorio);
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao carregar relatório: " + e.getMessage());
            return "redirect:/financeiro/relatorios";
        }
        return "financeiro/relatorio-detalhe";
    }

    @GetMapping("/relatorios/{id}/exportar")
    public void exportarRelatorio(@PathVariable int id, HttpServletResponse response) throws IOException {
        try {
            RelatorioGerado relatorio = relatorioDAO.buscarPorId(id);
            if (relatorio == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=relatorio-financeiro-" + id + ".csv");

            try (PrintWriter writer = response.getWriter()) {
                writer.println("Relatório Financeiro #" + relatorio.getId());
                writer.println("Template;" + relatorio.getTemplateNome());
                writer.println("Período;" + relatorio.getDataInicio() + " a " + relatorio.getDataFim());
                writer.println("Gerado por;" + relatorio.getGeradoPorUsername());
                writer.println("Gerado em;" + relatorio.getDataGeracao());
                writer.println();
                writer.println("Categoria;Tipo;Valor Período;Valor Período Anterior;Variação %;Rastreio;Ajuste Manual");
                for (var linha : relatorio.getLinhasCategoria()) {
                    writer.printf("%s;%s;%.2f;%.2f;%s;%s;%s%n",
                            linha.getCategoriaNome(),
                            linha.getTipoCategoria(),
                            linha.getValorPeriodo(),
                            linha.getValorPeriodoAnterior(),
                            linha.getVariacaoPercentual() != null ? String.format("%.2f", linha.getVariacaoPercentual()) : "",
                            linha.getOrigemRastreio(),
                            linha.isAjusteManual() ? "SIM" : "NAO");
                }
                writer.println();
                writer.println("Indicador;Valor;Valor Anterior;Variação %;Fórmula");
                for (var linha : relatorio.getLinhasIndicador()) {
                    writer.printf("%s;%.4f;%s;%s;%s%n",
                            linha.getIndicador(),
                            linha.getValor(),
                            linha.getValorAnterior() != null ? String.format("%.4f", linha.getValorAnterior()) : "",
                            linha.getVariacaoPercentual() != null ? String.format("%.2f", linha.getVariacaoPercentual()) : "",
                            linha.getFormulaDescricao());
                }
            }
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("/ajustes")
    public String ajustes(Model model) {
        try {
            model.addAttribute("ajustes", lancamentoAjusteDAO.listarTodos());
            model.addAttribute("categorias", categoriaFinanceiraDAO.listarAtivas());
            model.addAttribute("ajusteForm", new LancamentoAjuste());
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao carregar ajustes: " + e.getMessage());
        }
        return "financeiro/ajustes";
    }

    @PostMapping("/ajustes/salvar")
    public String salvarAjuste(@RequestParam int categoriaId,
                               @RequestParam String dataLancamento,
                               @RequestParam double valor,
                               @RequestParam String descricao,
                               RedirectAttributes redirectAttributes) {
        try {
            UsuarioAutenticado usuario = obterUsuarioAutenticado();
            LancamentoAjuste ajuste = new LancamentoAjuste();
            ajuste.setCategoriaId(categoriaId);
            ajuste.setDataLancamento(Date.valueOf(LocalDate.parse(dataLancamento)));
            ajuste.setValor(valor);
            ajuste.setDescricao(descricao.trim());
            ajuste.setUsuarioId(usuario != null ? usuario.getId() : null);
            ajuste.setUsername(usuario != null ? usuario.getUsername() : "sistema");
            lancamentoAjusteDAO.inserir(ajuste);
            redirectAttributes.addFlashAttribute("mensagem", "Lançamento de ajuste registrado.");
        } catch (SQLException e) {
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
