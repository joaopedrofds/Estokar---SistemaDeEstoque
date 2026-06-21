package com.studiomuda.estoque.indicadores.infrastructure.persistence;

import com.studiomuda.estoque.indicadores.domain.IndicadorId;
import com.studiomuda.estoque.indicadores.domain.MetaIndicador;
import com.studiomuda.estoque.indicadores.domain.MetaIndicadorId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

/**
 * Entidade JPA do agregado {@link MetaIndicador} (E-13). Id como {@code String}
 * do {@link MetaIndicadorId} (sem {@code @GeneratedValue}); operador persistido
 * como {@code String} (sem {@code @Enumerated}); mapeamento manual via
 * {@code fromDomain}/{@code toDomain} — padrão PetCollar.
 */
@Entity
@Table(name = "meta_indicador")
public class MetaIndicadorJpa {

    @Id
    private String id;

    @Column(name = "indicador_id", nullable = false)
    private String indicadorId;

    @Column(name = "valor_alvo", nullable = false)
    private double valorAlvo;

    @Column(name = "limite_critico", nullable = false)
    private double limiteCritico;

    @Column(name = "operador", nullable = false)
    private String operador;

    @Column(name = "vigencia_inicio", nullable = false)
    private LocalDate vigenciaInicio;

    @Column(name = "vigencia_fim")
    private LocalDate vigenciaFim;

    @Column(name = "ativo")
    private boolean ativo;

    protected MetaIndicadorJpa() {}

    public static MetaIndicadorJpa fromDomain(MetaIndicador m) {
        MetaIndicadorJpa jpa = new MetaIndicadorJpa();
        jpa.id = m.getId().getValor();
        jpa.indicadorId = m.getIndicadorId().getValor();
        jpa.valorAlvo = m.getValorAlvo();
        jpa.limiteCritico = m.getLimiteCritico();
        jpa.operador = m.getOperador();
        jpa.vigenciaInicio = m.getVigenciaInicio();
        jpa.vigenciaFim = m.getVigenciaFim();
        jpa.ativo = m.isAtivo();
        return jpa;
    }

    public MetaIndicador toDomain() {
        return new MetaIndicador(
                MetaIndicadorId.de(id),
                IndicadorId.de(indicadorId),
                valorAlvo,
                limiteCritico,
                operador,
                vigenciaInicio,
                vigenciaFim,
                ativo);
    }
}
