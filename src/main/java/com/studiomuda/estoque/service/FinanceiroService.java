package com.studiomuda.estoque.service;

import com.studiomuda.estoque.financeiro.application.dto.CategoriaFinanceiraDTO;
import com.studiomuda.estoque.financeiro.application.dto.LancamentoAjusteDTO;
import com.studiomuda.estoque.financeiro.application.dto.TemplateRelatorioDTO;
import com.studiomuda.estoque.financeiro.domain.CalculadoraRelatorioFinanceiro;
import com.studiomuda.estoque.financeiro.domain.CategoriaFinanceira;
import com.studiomuda.estoque.financeiro.domain.CategoriaId;
import com.studiomuda.estoque.financeiro.domain.ICategoriaFinanceiraRepositorio;
import com.studiomuda.estoque.financeiro.domain.ILancamentoAjusteRepositorio;
import com.studiomuda.estoque.financeiro.domain.IRelatorioGeradoRepositorio;
import com.studiomuda.estoque.financeiro.domain.ITemplateRelatorioRepositorio;
import com.studiomuda.estoque.financeiro.domain.LancamentoAjuste;
import com.studiomuda.estoque.financeiro.domain.LancamentoAjusteId;
import com.studiomuda.estoque.financeiro.domain.RelatorioCategoriaLinha;
import com.studiomuda.estoque.financeiro.domain.RelatorioGerado;
import com.studiomuda.estoque.financeiro.domain.RelatorioId;
import com.studiomuda.estoque.financeiro.domain.RelatorioIndicadorLinha;
import com.studiomuda.estoque.financeiro.domain.TemplateId;
import com.studiomuda.estoque.financeiro.domain.TemplateRelatorio;
import com.studiomuda.estoque.jpa.repository.MovimentacaoEstoqueJpaRepository;
import com.studiomuda.estoque.jpa.repository.PedidoJpaRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço de aplicação do Relatório Financeiro (E-12). Orquestra as portas de
 * domínio do contexto (categorias, templates, ajustes, relatórios) e os repositórios
 * JPA de leitura de outros contextos (pedido, movimentação), montando o relatório
 * consolidado. O cálculo aritmético vive em {@link CalculadoraRelatorioFinanceiro}
 * (domínio puro). Tradução domínio → apresentação via DTOs ({@code de(...)}).
 */
@Service
public class FinanceiroService {

    private final ICategoriaFinanceiraRepositorio categoriaRepo;
    private final ITemplateRelatorioRepositorio templateRepo;
    private final IRelatorioGeradoRepositorio relatorioRepo;
    private final ILancamentoAjusteRepositorio lancamentoRepo;
    private final PedidoJpaRepository pedidoJpaRepository;
    private final MovimentacaoEstoqueJpaRepository movimentacaoJpaRepository;

    private final CalculadoraRelatorioFinanceiro calculadora = new CalculadoraRelatorioFinanceiro();

    public FinanceiroService(ICategoriaFinanceiraRepositorio categoriaRepo,
                             ITemplateRelatorioRepositorio templateRepo,
                             IRelatorioGeradoRepositorio relatorioRepo,
                             ILancamentoAjusteRepositorio lancamentoRepo,
                             PedidoJpaRepository pedidoJpaRepository,
                             MovimentacaoEstoqueJpaRepository movimentacaoJpaRepository) {
        this.categoriaRepo = categoriaRepo;
        this.templateRepo = templateRepo;
        this.relatorioRepo = relatorioRepo;
        this.lancamentoRepo = lancamentoRepo;
        this.pedidoJpaRepository = pedidoJpaRepository;
        this.movimentacaoJpaRepository = movimentacaoJpaRepository;
    }

    // ----- Categorias financeiras -----

    public List<CategoriaFinanceiraDTO> listarCategorias() {
        return categoriaRepo.listarTodas().stream().map(CategoriaFinanceiraDTO::de).toList();
    }

    public List<CategoriaFinanceiraDTO> listarCategoriasAtivas() {
        return categoriaRepo.listarAtivas().stream().map(CategoriaFinanceiraDTO::de).toList();
    }

    public CategoriaFinanceiraDTO buscarCategoria(String id) {
        return categoriaRepo.buscarPorId(CategoriaId.de(id)).map(CategoriaFinanceiraDTO::de).orElse(null);
    }

    /** Cria (id em branco) ou atualiza a categoria conforme a presença do identificador. */
    public void salvarCategoria(String id, String nome, String tipo, String origemSistema,
                                String descricao, boolean ativo) {
        CategoriaId categoriaId = (id == null || id.isBlank()) ? CategoriaId.gerar() : CategoriaId.de(id);
        categoriaRepo.salvar(new CategoriaFinanceira(categoriaId, nome, tipo, origemSistema, descricao, ativo));
    }

    public void inativarCategoria(String id) {
        categoriaRepo.buscarPorId(CategoriaId.de(id)).ifPresent(categoria -> {
            categoria.inativar();
            categoriaRepo.salvar(categoria);
        });
    }

    // ----- Templates de relatório -----

    public List<TemplateRelatorioDTO> listarTemplates() {
        return templateRepo.listarTodos().stream().map(TemplateRelatorioDTO::de).toList();
    }

    public List<TemplateRelatorioDTO> listarTemplatesAtivos() {
        return templateRepo.listarAtivos().stream().map(TemplateRelatorioDTO::de).toList();
    }

    public TemplateRelatorioDTO buscarTemplate(String id) {
        return templateRepo.buscarPorId(TemplateId.de(id)).map(TemplateRelatorioDTO::de).orElse(null);
    }

    /** Cria (id em branco) ou atualiza o template conforme a presença do identificador. */
    public void salvarTemplate(String id, String nome, String descricao, String periodoPadrao,
                               String agrupamento, boolean ativo, List<String> categoriaIds,
                               List<String> indicadores) {
        TemplateId templateId = (id == null || id.isBlank()) ? TemplateId.gerar() : TemplateId.de(id);
        List<CategoriaId> cats = new ArrayList<>();
        if (categoriaIds != null) {
            for (String c : categoriaIds) {
                cats.add(CategoriaId.de(c));
            }
        }
        templateRepo.salvar(new TemplateRelatorio(templateId, nome, descricao, periodoPadrao, agrupamento,
                ativo, cats, indicadores == null ? List.of() : indicadores));
    }

    public void inativarTemplate(String id) {
        templateRepo.buscarPorId(TemplateId.de(id)).ifPresent(t ->
                templateRepo.salvar(new TemplateRelatorio(t.getId(), t.getNome(), t.getDescricao(),
                        t.getPeriodoPadrao(), t.getAgrupamento(), false, t.getCategoriaIds(), t.getIndicadores())));
    }

    // ----- Relatórios gerados -----

    public List<RelatorioGerado> listarHistorico(int limite) {
        return relatorioRepo.listarHistorico(limite);
    }

    public RelatorioGerado buscarRelatorio(String id) {
        return relatorioRepo.buscarPorId(RelatorioId.de(id)).orElse(null);
    }

    /**
     * Monta o relatório financeiro consolidado do período (comparado ao período
     * anterior de mesma duração) e persiste o snapshot resultante.
     */
    public RelatorioGerado gerarRelatorio(String templateId, LocalDate dataInicio, LocalDate dataFim,
                                          Integer usuarioId, String username) {
        TemplateRelatorio template = templateRepo.buscarPorId(TemplateId.de(templateId)).orElse(null);
        if (template == null || !template.isAtivo()) {
            throw new IllegalStateException("Template de relatório inválido ou inativo.");
        }

        long dias = ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
        LocalDate inicioAnterior = dataInicio.minusDays(dias);
        LocalDate fimAnterior = dataInicio.minusDays(1);

        Map<CategoriaId, CategoriaFinanceira> categorias = carregarCategoriasObrigatorias(template);
        Map<CategoriaId, ValoresCategoria> valoresAtuais = calcularValores(categorias, dataInicio, dataFim);
        Map<CategoriaId, ValoresCategoria> valoresAnteriores = calcularValores(categorias, inicioAnterior, fimAnterior);

        double receitaOperacional = 0, custoOperacional = 0, totalAjustesReceita = 0, totalAjustesDespesa = 0;
        List<RelatorioCategoriaLinha> linhas = new ArrayList<>();
        for (CategoriaFinanceira categoria : categorias.values()) {
            ValoresCategoria atual = valoresAtuais.getOrDefault(categoria.getId(), ValoresCategoria.zero());
            ValoresCategoria anterior = valoresAnteriores.getOrDefault(categoria.getId(), ValoresCategoria.zero());
            linhas.add(new RelatorioCategoriaLinha(
                    categoria.getId(), categoria.getNome(), categoria.getTipo(),
                    atual.valorTotal(), anterior.valorTotal(),
                    calculadora.calcularVariacao(atual.valorTotal(), anterior.valorTotal()),
                    atual.rastreio, atual.valorAjuste > 0));
            if ("RECEITA".equalsIgnoreCase(categoria.getTipo())) {
                receitaOperacional += atual.valorAutomatico;
                totalAjustesReceita += atual.valorAjuste;
            } else {
                custoOperacional += atual.valorAutomatico;
                totalAjustesDespesa += atual.valorAjuste;
            }
        }

        ResumoFinanceiroPedido resumoPedidos = resumirReceitaPaga(sql(dataInicio), sql(dataFim));
        ResumoFinanceiroPedido resumoAnterior = resumirReceitaPaga(sql(inicioAnterior), sql(fimAnterior));
        double resultadoOperacional = calculadora.calcularResultadoOperacional(receitaOperacional, custoOperacional);
        double resultadoConsolidado = calculadora.calcularResultadoConsolidado(resultadoOperacional, totalAjustesReceita, totalAjustesDespesa);

        List<RelatorioIndicadorLinha> linhasIndicador = calcularIndicadores(
                template.getIndicadores(), receitaOperacional, custoOperacional, resultadoConsolidado,
                resumoPedidos.quantidadePedidos(), resumoAnterior.receitaTotal(), resumoAnterior.quantidadePedidos());

        RelatorioGerado relatorio = RelatorioGerado.criar(
                RelatorioId.gerar(), template.getId(), template.getNome(),
                dataInicio, dataFim, inicioAnterior, fimAnterior,
                usuarioId, username, receitaOperacional, custoOperacional, resultadoOperacional,
                totalAjustesReceita, totalAjustesDespesa, resultadoConsolidado,
                resumoPedidos.quantidadePedidos(), linhas, linhasIndicador);

        return relatorioRepo.persistir(relatorio);
    }

    // ----- Lançamentos de ajuste manual -----

    public List<LancamentoAjusteDTO> listarAjustes() {
        Map<CategoriaId, CategoriaFinanceira> cache = new LinkedHashMap<>();
        for (CategoriaFinanceira c : categoriaRepo.listarTodas()) {
            cache.put(c.getId(), c);
        }
        return lancamentoRepo.listarTodos().stream()
                .map(a -> LancamentoAjusteDTO.de(a, cache.get(a.getCategoriaId())))
                .toList();
    }

    public void registrarAjuste(String categoriaId, LocalDate dataLancamento, double valor,
                                String descricao, Integer usuarioId, String username) {
        lancamentoRepo.salvar(new LancamentoAjuste(LancamentoAjusteId.gerar(), CategoriaId.de(categoriaId),
                dataLancamento, valor, descricao, usuarioId, username));
    }

    // ----- Consultas JPA para dados financeiros -----

    private ResumoFinanceiroPedido resumirReceitaPaga(Date inicio, Date fim) {
        List<Object[]> rows = pedidoJpaRepository.resumirReceitaPagaNativo(inicio, fim);
        if (rows != null && !rows.isEmpty()) {
            Object[] row = rows.get(0);
            double receita = row[0] != null ? ((Number) row[0]).doubleValue() : 0.0;
            int qtd = row[1] != null ? ((Number) row[1]).intValue() : 0;
            return new ResumoFinanceiroPedido(receita, qtd);
        }
        return new ResumoFinanceiroPedido(0.0, 0);
    }

    private double somarCustoSaida(Date inicio, Date fim) {
        Double result = movimentacaoJpaRepository.somarCustoSaidaNativo(inicio, fim);
        return result != null ? result : 0.0;
    }

    private double somarDevolucoes(Date inicio, Date fim) {
        Double result = movimentacaoJpaRepository.somarDevolucoeNativo(inicio, fim);
        return result != null ? result : 0.0;
    }

    // ----- Montagem do relatório -----

    private Map<CategoriaId, CategoriaFinanceira> carregarCategoriasObrigatorias(TemplateRelatorio template) {
        Map<CategoriaId, CategoriaFinanceira> mapa = new LinkedHashMap<>();
        for (CategoriaId categoriaId : template.getCategoriaIds()) {
            categoriaRepo.buscarPorId(categoriaId).ifPresent(c -> {
                if (c.isAtivo()) {
                    mapa.put(c.getId(), c);
                }
            });
        }
        return mapa;
    }

    private Map<CategoriaId, ValoresCategoria> calcularValores(Map<CategoriaId, CategoriaFinanceira> categorias,
                                                               LocalDate inicio, LocalDate fim) {
        Map<CategoriaId, ValoresCategoria> valores = new LinkedHashMap<>();
        for (CategoriaFinanceira categoria : categorias.values()) {
            valores.put(categoria.getId(), resolverValorCategoria(categoria, inicio, fim));
        }
        return valores;
    }

    private ValoresCategoria resolverValorCategoria(CategoriaFinanceira categoria, LocalDate inicio, LocalDate fim) {
        String origem = categoria.getOrigemSistema() == null ? "" : categoria.getOrigemSistema();
        double valorAutomatico = 0;
        String rastreio = "Sem lançamentos no período";
        switch (origem) {
            case "PEDIDO_PAGO": {
                ResumoFinanceiroPedido resumo = resumirReceitaPaga(sql(inicio), sql(fim));
                valorAutomatico = resumo.receitaTotal();
                rastreio = "PedidoJpaRepository: " + resumo.quantidadePedidos() + " pedido(s) pago(s)";
                break;
            }
            case "MOVIMENTACAO_SAIDA":
                valorAutomatico = somarCustoSaida(sql(inicio), sql(fim));
                rastreio = "MovimentacaoEstoqueJpaRepository: saídas de estoque";
                break;
            case "MOVIMENTACAO_ENTRADA_DEVOLUCAO":
                valorAutomatico = somarDevolucoes(sql(inicio), sql(fim));
                rastreio = "MovimentacaoEstoqueJpaRepository: entradas de devolução/estorno";
                break;
            default:
                rastreio = "Categoria sem origem automática";
                break;
        }
        double valorAjustes = lancamentoRepo.somarPorCategoria(categoria.getId(), inicio, fim);
        if (valorAjustes != 0) {
            rastreio = rastreio + " + LancamentoAjuste: ajustes manuais";
        }
        return new ValoresCategoria(valorAutomatico, valorAjustes, rastreio);
    }

    private List<RelatorioIndicadorLinha> calcularIndicadores(List<String> indicadores, double receita, double custo,
                                                              double resultado, int qtdPedidos, double receitaAnterior,
                                                              int qtdPedidosAnterior) {
        List<RelatorioIndicadorLinha> linhas = new ArrayList<>();
        if (indicadores == null) {
            return linhas;
        }
        for (String indicador : indicadores) {
            switch (indicador) {
                case "MARGEM_BRUTA": {
                    double margem = calculadora.calcularMargemBruta(receita, custo);
                    double margemAnterior = calculadora.calcularMargemBruta(receitaAnterior, custo);
                    linhas.add(new RelatorioIndicadorLinha("MARGEM_BRUTA", margem, margemAnterior,
                            calculadora.calcularVariacao(margem, margemAnterior),
                            "(Receita Total − Custo Total) / Receita Total × 100"));
                    break;
                }
                case "TICKET_MEDIO": {
                    double ticket = calculadora.calcularTicketMedio(receita, qtdPedidos);
                    double ticketAnterior = calculadora.calcularTicketMedio(receitaAnterior, qtdPedidosAnterior);
                    linhas.add(new RelatorioIndicadorLinha("TICKET_MEDIO", ticket, ticketAnterior,
                            calculadora.calcularVariacao(ticket, ticketAnterior),
                            "Receita Total / Quantidade de Pedidos Pagos"));
                    break;
                }
                case "RESULTADO_LIQUIDO":
                    linhas.add(new RelatorioIndicadorLinha("RESULTADO_LIQUIDO", resultado, null, null,
                            "Resultado Operacional + Ajustes de Receita − Ajustes de Despesa"));
                    break;
                default:
                    linhas.add(new RelatorioIndicadorLinha(indicador, 0, null, null, "Indicador não mapeado"));
                    break;
            }
        }
        return linhas;
    }

    private static Date sql(LocalDate data) {
        return Date.valueOf(data);
    }

    // ----- Tipos auxiliares locais -----

    /** Resumo financeiro de pedidos pagos num período (substitui PedidoDAO.ResumoFinanceiroPedido). */
    private record ResumoFinanceiroPedido(double receitaTotal, int quantidadePedidos) {}

    private static class ValoresCategoria {
        private final double valorAutomatico;
        private final double valorAjuste;
        private final String rastreio;

        private ValoresCategoria(double valorAutomatico, double valorAjuste, String rastreio) {
            this.valorAutomatico = valorAutomatico;
            this.valorAjuste = valorAjuste;
            this.rastreio = rastreio;
        }

        private double valorTotal() {
            return valorAutomatico + valorAjuste;
        }

        private static ValoresCategoria zero() {
            return new ValoresCategoria(0, 0, "Sem lançamentos no período");
        }
    }
}
