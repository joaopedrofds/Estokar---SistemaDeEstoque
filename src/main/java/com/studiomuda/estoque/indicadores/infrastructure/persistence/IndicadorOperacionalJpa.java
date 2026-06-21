package com.studiomuda.estoque.indicadores.infrastructure.persistence;

import com.studiomuda.estoque.indicadores.domain.IndicadorId;
import com.studiomuda.estoque.indicadores.domain.IndicadorOperacional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entidade JPA do agregado {@link IndicadorOperacional} (E-13). Id como
 * {@code String} do {@link IndicadorId}; mapeamento manual via
 * {@code fromDomain}/{@code toDomain} — padrão PetCollar.
 */
@Entity
@Table(name = "indicador_operacional")
public class IndicadorOperacionalJpa {

    @Id
    private String id;

    @Column(name = "codigo", nullable = false, unique = true)
    private String codigo;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "tipo_calculo", nullable = false)
    private String tipoCalculo;

    @Column(name = "periodo_padrao", nullable = false)
    private String periodoPadrao;

    @Column(name = "ativo")
    private boolean ativo;

    protected IndicadorOperacionalJpa() {}

    public static IndicadorOperacionalJpa fromDomain(IndicadorOperacional i) {
        IndicadorOperacionalJpa jpa = new IndicadorOperacionalJpa();
        jpa.id = i.getId().getValor();
        jpa.codigo = i.getCodigo();
        jpa.nome = i.getNome();
        jpa.descricao = i.getDescricao();
        jpa.tipoCalculo = i.getTipoCalculo();
        jpa.periodoPadrao = i.getPeriodoPadrao();
        jpa.ativo = i.isAtivo();
        return jpa;
    }

    public IndicadorOperacional toDomain() {
        return new IndicadorOperacional(
                IndicadorId.de(id),
                codigo,
                nome,
                descricao,
                tipoCalculo,
                periodoPadrao,
                ativo);
    }
}
