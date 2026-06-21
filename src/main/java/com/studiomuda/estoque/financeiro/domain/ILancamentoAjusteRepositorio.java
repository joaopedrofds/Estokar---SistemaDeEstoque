package com.studiomuda.estoque.financeiro.domain;

import java.time.LocalDate;
import java.util.List;

/** Porta de domínio do agregado {@link LancamentoAjuste} (E-12). */
public interface ILancamentoAjusteRepositorio {

    void salvar(LancamentoAjuste ajuste);

    /** Todos os lançamentos, mais recentes primeiro. */
    List<LancamentoAjuste> listarTodos();

    /** Soma dos ajustes de uma categoria no período (inclusive). */
    double somarPorCategoria(CategoriaId categoriaId, LocalDate inicio, LocalDate fim);
}
