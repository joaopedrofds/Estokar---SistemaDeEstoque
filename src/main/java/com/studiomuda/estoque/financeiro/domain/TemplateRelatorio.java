package com.studiomuda.estoque.financeiro.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Template de relatório financeiro (E-12) — agregado de domínio puro (padrão
 * PetCollar). Identidade por {@link TemplateId}. Reúne as categorias
 * ({@link CategoriaId}) e os indicadores (códigos) que comporão o relatório;
 * cada categoria/indicador aparece no máximo uma vez (deduplicado na entrada).
 */
public class TemplateRelatorio {

    private final TemplateId id;
    private final String nome;
    private final String descricao;
    private final String periodoPadrao;
    private final String agrupamento;
    private final boolean ativo;
    private final List<CategoriaId> categoriaIds;
    private final List<String> indicadores;

    public TemplateRelatorio(TemplateId id, String nome, String descricao, String periodoPadrao,
                             String agrupamento, boolean ativo, List<CategoriaId> categoriaIds,
                             List<String> indicadores) {
        if (id == null) {
            throw new IllegalArgumentException("Id do template não pode ser nulo.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do template não pode ser vazio.");
        }
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.periodoPadrao = periodoPadrao;
        this.agrupamento = agrupamento;
        this.ativo = ativo;
        this.categoriaIds = new ArrayList<>(new LinkedHashSet<>(
                categoriaIds == null ? List.of() : categoriaIds));
        this.indicadores = new ArrayList<>(new LinkedHashSet<>(
                indicadores == null ? List.of() : indicadores));
    }

    public TemplateId getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getPeriodoPadrao() { return periodoPadrao; }
    public String getAgrupamento() { return agrupamento; }
    public boolean isAtivo() { return ativo; }

    public List<CategoriaId> getCategoriaIds() { return Collections.unmodifiableList(categoriaIds); }
    public List<String> getIndicadores() { return Collections.unmodifiableList(indicadores); }
}
