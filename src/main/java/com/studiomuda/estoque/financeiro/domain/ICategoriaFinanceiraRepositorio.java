package com.studiomuda.estoque.financeiro.domain;

import java.util.List;
import java.util.Optional;

/** Porta de domínio do agregado {@link CategoriaFinanceira} (E-12). */
public interface ICategoriaFinanceiraRepositorio {

    void salvar(CategoriaFinanceira categoria);

    Optional<CategoriaFinanceira> buscarPorId(CategoriaId id);

    List<CategoriaFinanceira> listarTodas();

    List<CategoriaFinanceira> listarAtivas();
}
