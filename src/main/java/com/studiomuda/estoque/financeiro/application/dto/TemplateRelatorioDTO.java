package com.studiomuda.estoque.financeiro.application.dto;

import com.studiomuda.estoque.financeiro.domain.CategoriaId;
import com.studiomuda.estoque.financeiro.domain.TemplateRelatorio;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO de apresentação do template de relatório (E-12): listagem e formulário.
 * {@code categoriaIds} são expostos como {@code String} para casar com os
 * checkboxes ({@code categoriaIds.contains(cat.id)}) — padrão View/DTO PetCollar.
 */
public class TemplateRelatorioDTO {

    private final String id;
    private final String nome;
    private final String descricao;
    private final String periodoPadrao;
    private final String agrupamento;
    private final boolean ativo;
    private final List<String> categoriaIds;
    private final List<String> indicadores;

    public TemplateRelatorioDTO(String id, String nome, String descricao, String periodoPadrao,
                                String agrupamento, boolean ativo, List<String> categoriaIds,
                                List<String> indicadores) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.periodoPadrao = periodoPadrao;
        this.agrupamento = agrupamento;
        this.ativo = ativo;
        this.categoriaIds = categoriaIds;
        this.indicadores = indicadores;
    }

    public static TemplateRelatorioDTO de(TemplateRelatorio t) {
        List<String> cats = new ArrayList<>();
        for (CategoriaId c : t.getCategoriaIds()) {
            cats.add(c.getValor());
        }
        return new TemplateRelatorioDTO(t.getId().getValor(), t.getNome(), t.getDescricao(),
                t.getPeriodoPadrao(), t.getAgrupamento(), t.isAtivo(), cats,
                new ArrayList<>(t.getIndicadores()));
    }

    /** Formulário em branco para criação (id nulo, listas vazias, defaults MES). */
    public static TemplateRelatorioDTO vazio() {
        return new TemplateRelatorioDTO(null, null, null, "MES", "MES", true,
                new ArrayList<>(), new ArrayList<>());
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getPeriodoPadrao() { return periodoPadrao; }
    public String getAgrupamento() { return agrupamento; }
    public boolean isAtivo() { return ativo; }
    public List<String> getCategoriaIds() { return categoriaIds; }
    public List<String> getIndicadores() { return indicadores; }
}
