package com.studiomuda.estoque.indicadores.infrastructure.persistence;

import com.studiomuda.estoque.indicadores.domain.IMetaIndicadorRepositorio;
import com.studiomuda.estoque.indicadores.domain.IndicadorId;
import com.studiomuda.estoque.indicadores.domain.MetaIndicador;
import com.studiomuda.estoque.indicadores.domain.MetaIndicadorId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adapter JPA que implementa a porta de domínio {@link IMetaIndicadorRepositorio},
 * traduzindo domínio ↔ {@link MetaIndicadorJpa} via {@code fromDomain}/{@code toDomain}.
 */
@Repository
public class MetaIndicadorRepositorioJpa implements IMetaIndicadorRepositorio {

    private final MetaIndicadorJpaRepository jpa;

    public MetaIndicadorRepositorioJpa(MetaIndicadorJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void salvar(MetaIndicador meta) {
        jpa.save(MetaIndicadorJpa.fromDomain(meta));
    }

    @Override
    public Optional<MetaIndicador> buscarPorId(MetaIndicadorId id) {
        return jpa.findById(id.getValor()).map(MetaIndicadorJpa::toDomain);
    }

    @Override
    public List<MetaIndicador> buscarVigentesPorIndicador(IndicadorId indicadorId) {
        return jpa.buscarVigentesPorIndicador(indicadorId.getValor()).stream()
                .map(MetaIndicadorJpa::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void desativarOutrasMetas(IndicadorId indicadorId, MetaIndicadorId metaAtivaId) {
        jpa.desativarOutrasMetas(indicadorId.getValor(), metaAtivaId.getValor());
    }
}
