package com.studiomuda.estoque.financeiro.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Relatório financeiro consolidado e persistido (E-12) — agregado de domínio puro
 * (padrão PetCollar), imutável (snapshot). Identidade por {@link RelatorioId};
 * contém as linhas de categoria e de indicador como Value Objects.
 *
 * <p>{@code dataGeracao} é atribuída pelo banco ({@code DEFAULT CURRENT_TIMESTAMP}),
 * portanto nula num relatório recém-criado e preenchida só na reconstrução.</p>
 */
public class RelatorioGerado {

    private final RelatorioId id;
    private final TemplateId templateId;
    private final String templateNome;
    private final LocalDate dataInicio;
    private final LocalDate dataFim;
    private final LocalDate dataInicioAnterior;
    private final LocalDate dataFimAnterior;
    private final Integer geradoPorUsuarioId;
    private final String geradoPorUsername;
    private final LocalDateTime dataGeracao;
    private final double receitaOperacional;
    private final double custoOperacional;
    private final double resultadoOperacional;
    private final double totalAjustesReceita;
    private final double totalAjustesDespesa;
    private final double resultadoConsolidado;
    private final int quantidadePedidos;
    private final List<RelatorioCategoriaLinha> linhasCategoria;
    private final List<RelatorioIndicadorLinha> linhasIndicador;

    /** Cria um novo relatório (sem data de geração, atribuída pelo banco ao persistir). */
    public static RelatorioGerado criar(RelatorioId id, TemplateId templateId, String templateNome,
                                        LocalDate dataInicio, LocalDate dataFim,
                                        LocalDate dataInicioAnterior, LocalDate dataFimAnterior,
                                        Integer geradoPorUsuarioId, String geradoPorUsername,
                                        double receitaOperacional, double custoOperacional,
                                        double resultadoOperacional, double totalAjustesReceita,
                                        double totalAjustesDespesa, double resultadoConsolidado,
                                        int quantidadePedidos, List<RelatorioCategoriaLinha> linhasCategoria,
                                        List<RelatorioIndicadorLinha> linhasIndicador) {
        return new RelatorioGerado(id, templateId, templateNome, dataInicio, dataFim, dataInicioAnterior,
                dataFimAnterior, geradoPorUsuarioId, geradoPorUsername, null, receitaOperacional,
                custoOperacional, resultadoOperacional, totalAjustesReceita, totalAjustesDespesa,
                resultadoConsolidado, quantidadePedidos, linhasCategoria, linhasIndicador);
    }

    // Construtor de RECONSTRUÇÃO (infra → domínio); inclui dataGeracao do banco.
    public RelatorioGerado(RelatorioId id, TemplateId templateId, String templateNome,
                           LocalDate dataInicio, LocalDate dataFim, LocalDate dataInicioAnterior,
                           LocalDate dataFimAnterior, Integer geradoPorUsuarioId, String geradoPorUsername,
                           LocalDateTime dataGeracao, double receitaOperacional, double custoOperacional,
                           double resultadoOperacional, double totalAjustesReceita, double totalAjustesDespesa,
                           double resultadoConsolidado, int quantidadePedidos,
                           List<RelatorioCategoriaLinha> linhasCategoria,
                           List<RelatorioIndicadorLinha> linhasIndicador) {
        if (id == null) {
            throw new IllegalArgumentException("Id do relatório não pode ser nulo.");
        }
        if (templateId == null) {
            throw new IllegalArgumentException("Template do relatório não pode ser nulo.");
        }
        this.id = id;
        this.templateId = templateId;
        this.templateNome = templateNome;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.dataInicioAnterior = dataInicioAnterior;
        this.dataFimAnterior = dataFimAnterior;
        this.geradoPorUsuarioId = geradoPorUsuarioId;
        this.geradoPorUsername = geradoPorUsername;
        this.dataGeracao = dataGeracao;
        this.receitaOperacional = receitaOperacional;
        this.custoOperacional = custoOperacional;
        this.resultadoOperacional = resultadoOperacional;
        this.totalAjustesReceita = totalAjustesReceita;
        this.totalAjustesDespesa = totalAjustesDespesa;
        this.resultadoConsolidado = resultadoConsolidado;
        this.quantidadePedidos = quantidadePedidos;
        this.linhasCategoria = new ArrayList<>(linhasCategoria == null ? List.of() : linhasCategoria);
        this.linhasIndicador = new ArrayList<>(linhasIndicador == null ? List.of() : linhasIndicador);
    }

    public RelatorioId getId() { return id; }
    public TemplateId getTemplateId() { return templateId; }
    public String getTemplateNome() { return templateNome; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public LocalDate getDataInicioAnterior() { return dataInicioAnterior; }
    public LocalDate getDataFimAnterior() { return dataFimAnterior; }
    public Integer getGeradoPorUsuarioId() { return geradoPorUsuarioId; }
    public String getGeradoPorUsername() { return geradoPorUsername; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public double getReceitaOperacional() { return receitaOperacional; }
    public double getCustoOperacional() { return custoOperacional; }
    public double getResultadoOperacional() { return resultadoOperacional; }
    public double getTotalAjustesReceita() { return totalAjustesReceita; }
    public double getTotalAjustesDespesa() { return totalAjustesDespesa; }
    public double getResultadoConsolidado() { return resultadoConsolidado; }
    public int getQuantidadePedidos() { return quantidadePedidos; }
    public List<RelatorioCategoriaLinha> getLinhasCategoria() { return Collections.unmodifiableList(linhasCategoria); }
    public List<RelatorioIndicadorLinha> getLinhasIndicador() { return Collections.unmodifiableList(linhasIndicador); }
}
