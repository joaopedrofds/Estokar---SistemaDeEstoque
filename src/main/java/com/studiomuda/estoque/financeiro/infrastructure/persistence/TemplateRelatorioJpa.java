package com.studiomuda.estoque.financeiro.infrastructure.persistence;

import com.studiomuda.estoque.financeiro.domain.CategoriaId;
import com.studiomuda.estoque.financeiro.domain.TemplateId;
import com.studiomuda.estoque.financeiro.domain.TemplateRelatorio;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Entidade JPA do agregado {@link TemplateRelatorio} (E-12). Mapeia a tabela
 * {@code template_relatorio} e suas duas coleções filhas
 * ({@code template_categoria} e {@code template_indicador}) via
 * {@link ElementCollection}. Tradução domínio ↔ persistência por
 * {@code fromDomain}/{@code toDomain}.
 */
@Entity
@Table(name = "template_relatorio")
public class TemplateRelatorioJpa {

    @Id
    private String id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "periodo_padrao")
    private String periodoPadrao;

    @Column(name = "agrupamento")
    private String agrupamento;

    @Column(name = "ativo")
    private boolean ativo;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "template_categoria", joinColumns = @JoinColumn(name = "template_id"))
    @Column(name = "categoria_id")
    private Set<String> categoriaIds = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "template_indicador", joinColumns = @JoinColumn(name = "template_id"))
    @Column(name = "indicador")
    private Set<String> indicadores = new HashSet<>();

    protected TemplateRelatorioJpa() {
    }

    public static TemplateRelatorioJpa fromDomain(TemplateRelatorio template) {
        TemplateRelatorioJpa jpa = new TemplateRelatorioJpa();
        jpa.id = template.getId().getValor();
        jpa.nome = template.getNome();
        jpa.descricao = template.getDescricao();
        jpa.periodoPadrao = template.getPeriodoPadrao();
        jpa.agrupamento = template.getAgrupamento();
        jpa.ativo = template.isAtivo();
        jpa.categoriaIds = template.getCategoriaIds().stream()
                .map(CategoriaId::getValor)
                .collect(Collectors.toCollection(HashSet::new));
        jpa.indicadores = new HashSet<>(template.getIndicadores());
        return jpa;
    }

    public TemplateRelatorio toDomain() {
        List<CategoriaId> catList = categoriaIds.stream()
                .map(CategoriaId::de)
                .collect(Collectors.toList());
        List<String> indList = new ArrayList<>(indicadores);
        return new TemplateRelatorio(
                TemplateId.de(id),
                nome,
                descricao,
                periodoPadrao,
                agrupamento,
                ativo,
                catList,
                indList);
    }
}
