package com.studiomuda.estoque.indicadores.infrastructure.persistence;

import com.studiomuda.estoque.indicadores.domain.IIndicadorOperacionalRepositorio;
import com.studiomuda.estoque.indicadores.domain.IndicadorId;
import com.studiomuda.estoque.indicadores.domain.IndicadorOperacional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter JPA que implementa a porta {@link IIndicadorOperacionalRepositorio},
 * traduzindo domínio ↔ {@link IndicadorOperacionalJpa} via {@code fromDomain}/{@code toDomain}.
 */
@Repository
public class IndicadorOperacionalRepositorioJpa implements IIndicadorOperacionalRepositorio {

    private final IndicadorOperacionalJpaRepository jpa;

    public IndicadorOperacionalRepositorioJpa(IndicadorOperacionalJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void salvar(IndicadorOperacional indicador) {
        jpa.save(IndicadorOperacionalJpa.fromDomain(indicador));
    }

    @Override
    public Optional<IndicadorOperacional> buscarPorId(IndicadorId id) {
        return jpa.findById(id.getValor()).map(IndicadorOperacionalJpa::toDomain);
    }

    @Override
    public Optional<IndicadorOperacional> buscarPorCodigo(String codigo) {
        return jpa.findByCodigo(codigo).map(IndicadorOperacionalJpa::toDomain);
    }

    @Override
    public List<IndicadorOperacional> listarTodosOrdenadoPorNome() {
        return jpa.findAllByOrderByNomeAsc().stream()
                .map(IndicadorOperacionalJpa::toDomain)
                .toList();
    }

    @Override
    public List<IndicadorOperacional> listarAtivosOrdenadoPorNome() {
        return jpa.findByAtivoTrueOrderByNomeAsc().stream()
                .map(IndicadorOperacionalJpa::toDomain)
                .toList();
    }
}
