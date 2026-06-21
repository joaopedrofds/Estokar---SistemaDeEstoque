package com.studiomuda.estoque.financeiro.infrastructure.persistence;

import com.studiomuda.estoque.financeiro.domain.RelatorioCategoriaLinha;
import com.studiomuda.estoque.financeiro.domain.RelatorioGerado;
import com.studiomuda.estoque.financeiro.domain.RelatorioId;
import com.studiomuda.estoque.financeiro.domain.RelatorioIndicadorLinha;
import com.studiomuda.estoque.financeiro.domain.TemplateId;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Entidade JPA do agregado {@code RelatorioGerado} (E-12). Mapeia
 * {@code relatorio_gerado} e suas duas tabelas filhas
 * ({@code relatorio_categoria_linha} e {@code relatorio_indicador_linha}) via
 * {@code @OneToMany} com cascade. {@code data_geracao} é atribuída pelo banco
 * ({@code DEFAULT CURRENT_TIMESTAMP}), por isso {@code insertable=false} e
 * {@code updatable=false}. Tradução domínio ↔ persistência por
 * {@code fromDomain}/{@code toDomain}.
 */
@Entity
@Table(name = "relatorio_gerado")
public class RelatorioGeradoJpa {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "template_id")
    private String templateId;

    @Column(name = "template_nome")
    private String templateNome;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "data_inicio_anterior")
    private LocalDate dataInicioAnterior;

    @Column(name = "data_fim_anterior")
    private LocalDate dataFimAnterior;

    @Column(name = "gerado_por_usuario_id")
    private Integer geradoPorUsuarioId;

    @Column(name = "gerado_por_username")
    private String geradoPorUsername;

    @Column(name = "data_geracao", insertable = false, updatable = false)
    private LocalDateTime dataGeracao;

    @Column(name = "receita_operacional")
    private double receitaOperacional;

    @Column(name = "custo_operacional")
    private double custoOperacional;

    @Column(name = "resultado_operacional")
    private double resultadoOperacional;

    @Column(name = "total_ajustes_receita")
    private double totalAjustesReceita;

    @Column(name = "total_ajustes_despesa")
    private double totalAjustesDespesa;

    @Column(name = "resultado_consolidado")
    private double resultadoConsolidado;

    @Column(name = "quantidade_pedidos")
    private int quantidadePedidos;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "relatorio_id")
    private List<RelatorioCategoriaLinhaJpa> linhasCategoria = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "relatorio_id")
    private List<RelatorioIndicadorLinhaJpa> linhasIndicador = new ArrayList<>();

    protected RelatorioGeradoJpa() {
    }

    public String getId() {
        return id;
    }

    public static RelatorioGeradoJpa fromDomain(RelatorioGerado domain) {
        RelatorioGeradoJpa jpa = new RelatorioGeradoJpa();
        jpa.id = domain.getId().getValor();
        jpa.templateId = domain.getTemplateId().getValor();
        jpa.templateNome = domain.getTemplateNome();
        jpa.dataInicio = domain.getDataInicio();
        jpa.dataFim = domain.getDataFim();
        jpa.dataInicioAnterior = domain.getDataInicioAnterior();
        jpa.dataFimAnterior = domain.getDataFimAnterior();
        jpa.geradoPorUsuarioId = domain.getGeradoPorUsuarioId();
        jpa.geradoPorUsername = domain.getGeradoPorUsername();
        // data_geracao é DB-generated (insertable=false): não mapeado a partir do domínio.
        jpa.receitaOperacional = domain.getReceitaOperacional();
        jpa.custoOperacional = domain.getCustoOperacional();
        jpa.resultadoOperacional = domain.getResultadoOperacional();
        jpa.totalAjustesReceita = domain.getTotalAjustesReceita();
        jpa.totalAjustesDespesa = domain.getTotalAjustesDespesa();
        jpa.resultadoConsolidado = domain.getResultadoConsolidado();
        jpa.quantidadePedidos = domain.getQuantidadePedidos();
        jpa.linhasCategoria = domain.getLinhasCategoria().stream()
                .map(RelatorioCategoriaLinhaJpa::fromDomainVO)
                .collect(Collectors.toList());
        jpa.linhasIndicador = domain.getLinhasIndicador().stream()
                .map(RelatorioIndicadorLinhaJpa::fromDomainVO)
                .collect(Collectors.toList());
        return jpa;
    }

    public RelatorioGerado toDomain() {
        List<RelatorioCategoriaLinha> categorias = linhasCategoria.stream()
                .map(RelatorioCategoriaLinhaJpa::toDomainVO)
                .collect(Collectors.toList());
        List<RelatorioIndicadorLinha> indicadores = linhasIndicador.stream()
                .map(RelatorioIndicadorLinhaJpa::toDomainVO)
                .collect(Collectors.toList());
        // Construtor de RECONSTRUÇÃO: inclui dataGeracao lida do banco.
        return new RelatorioGerado(
                RelatorioId.de(id),
                TemplateId.de(templateId),
                templateNome,
                dataInicio,
                dataFim,
                dataInicioAnterior,
                dataFimAnterior,
                geradoPorUsuarioId,
                geradoPorUsername,
                dataGeracao,
                receitaOperacional,
                custoOperacional,
                resultadoOperacional,
                totalAjustesReceita,
                totalAjustesDespesa,
                resultadoConsolidado,
                quantidadePedidos,
                categorias,
                indicadores);
    }
}
