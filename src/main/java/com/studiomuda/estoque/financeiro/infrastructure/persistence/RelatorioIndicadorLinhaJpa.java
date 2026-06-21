package com.studiomuda.estoque.financeiro.infrastructure.persistence;

import com.studiomuda.estoque.financeiro.domain.RelatorioIndicadorLinha;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entidade JPA da linha de indicador do agregado {@code RelatorioGerado} (E-12).
 * Mapeia {@code relatorio_indicador_linha}; a FK {@code relatorio_id} é gerenciada
 * pelo pai via {@code @JoinColumn}. Tradução do VO {@link RelatorioIndicadorLinha}
 * por {@code fromDomainVO}/{@code toDomainVO}.
 */
@Entity
@Table(name = "relatorio_indicador_linha")
public class RelatorioIndicadorLinhaJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "indicador")
    private String indicador;

    @Column(name = "valor")
    private double valor;

    @Column(name = "valor_anterior")
    private Double valorAnterior;

    @Column(name = "variacao_percentual")
    private Double variacaoPercentual;

    @Column(name = "formula_descricao")
    private String formulaDescricao;

    protected RelatorioIndicadorLinhaJpa() {
    }

    public static RelatorioIndicadorLinhaJpa fromDomainVO(RelatorioIndicadorLinha vo) {
        RelatorioIndicadorLinhaJpa jpa = new RelatorioIndicadorLinhaJpa();
        jpa.indicador = vo.getIndicador();
        jpa.valor = vo.getValor();
        jpa.valorAnterior = vo.getValorAnterior();
        jpa.variacaoPercentual = vo.getVariacaoPercentual();
        jpa.formulaDescricao = vo.getFormulaDescricao();
        return jpa;
    }

    public RelatorioIndicadorLinha toDomainVO() {
        return new RelatorioIndicadorLinha(
                indicador,
                valor,
                valorAnterior,
                variacaoPercentual,
                formulaDescricao);
    }
}
