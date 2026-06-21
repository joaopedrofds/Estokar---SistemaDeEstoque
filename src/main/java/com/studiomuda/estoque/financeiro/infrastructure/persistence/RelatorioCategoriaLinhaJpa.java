package com.studiomuda.estoque.financeiro.infrastructure.persistence;

import com.studiomuda.estoque.financeiro.domain.CategoriaId;
import com.studiomuda.estoque.financeiro.domain.RelatorioCategoriaLinha;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entidade JPA da linha de categoria do agregado {@code RelatorioGerado} (E-12).
 * Mapeia {@code relatorio_categoria_linha}; a FK {@code relatorio_id} é gerenciada
 * pelo pai via {@code @JoinColumn}, por isso não há campo aqui. Tradução do VO
 * {@link RelatorioCategoriaLinha} por {@code fromDomainVO}/{@code toDomainVO}.
 */
@Entity
@Table(name = "relatorio_categoria_linha")
public class RelatorioCategoriaLinhaJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "categoria_id")
    private String categoriaId;

    @Column(name = "categoria_nome")
    private String categoriaNome;

    @Column(name = "tipo_categoria")
    private String tipoCategoria;

    @Column(name = "valor_periodo")
    private double valorPeriodo;

    @Column(name = "valor_periodo_anterior")
    private double valorPeriodoAnterior;

    @Column(name = "variacao_percentual")
    private Double variacaoPercentual;

    @Column(name = "origem_rastreio")
    private String origemRastreio;

    @Column(name = "ajuste_manual")
    private boolean ajusteManual;

    protected RelatorioCategoriaLinhaJpa() {
    }

    public static RelatorioCategoriaLinhaJpa fromDomainVO(RelatorioCategoriaLinha vo) {
        RelatorioCategoriaLinhaJpa jpa = new RelatorioCategoriaLinhaJpa();
        jpa.categoriaId = vo.getCategoriaId().getValor();
        jpa.categoriaNome = vo.getCategoriaNome();
        jpa.tipoCategoria = vo.getTipoCategoria();
        jpa.valorPeriodo = vo.getValorPeriodo();
        jpa.valorPeriodoAnterior = vo.getValorPeriodoAnterior();
        jpa.variacaoPercentual = vo.getVariacaoPercentual();
        jpa.origemRastreio = vo.getOrigemRastreio();
        jpa.ajusteManual = vo.isAjusteManual();
        return jpa;
    }

    public RelatorioCategoriaLinha toDomainVO() {
        return new RelatorioCategoriaLinha(
                CategoriaId.de(categoriaId),
                categoriaNome,
                tipoCategoria,
                valorPeriodo,
                valorPeriodoAnterior,
                variacaoPercentual,
                origemRastreio,
                ajusteManual);
    }
}
