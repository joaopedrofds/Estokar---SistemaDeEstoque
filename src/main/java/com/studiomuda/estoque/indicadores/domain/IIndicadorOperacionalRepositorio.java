package com.studiomuda.estoque.indicadores.domain;

import java.util.List;
import java.util.Optional;

/**
 * Porta de domínio do agregado {@link IndicadorOperacional} (E-13). Definida no
 * domínio (Java puro), implementada na infraestrutura por
 * {@code IndicadorOperacionalRepositorioJpa}.
 */
public interface IIndicadorOperacionalRepositorio {

    void salvar(IndicadorOperacional indicador);

    Optional<IndicadorOperacional> buscarPorId(IndicadorId id);

    Optional<IndicadorOperacional> buscarPorCodigo(String codigo);

    /** Todos os indicadores, ordenados por nome. */
    List<IndicadorOperacional> listarTodosOrdenadoPorNome();

    /** Apenas os indicadores ativos, ordenados por nome. */
    List<IndicadorOperacional> listarAtivosOrdenadoPorNome();
}
