package com.studiomuda.estoque.financeiro.infrastructure.persistence;

import com.studiomuda.estoque.financeiro.domain.CategoriaId;
import com.studiomuda.estoque.financeiro.domain.ILancamentoAjusteRepositorio;
import com.studiomuda.estoque.financeiro.domain.LancamentoAjuste;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Adapter JPA que implementa a porta de domínio {@link ILancamentoAjusteRepositorio},
 * traduzindo domínio ↔ {@link LancamentoAjusteJpa} via {@code fromDomain}/{@code toDomain}.
 */
@Repository
public class LancamentoAjusteRepositorioJpa implements ILancamentoAjusteRepositorio {

    private final LancamentoAjusteJpaRepository jpa;

    public LancamentoAjusteRepositorioJpa(LancamentoAjusteJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void salvar(LancamentoAjuste ajuste) {
        jpa.save(LancamentoAjusteJpa.fromDomain(ajuste));
    }

    @Override
    public List<LancamentoAjuste> listarTodos() {
        return jpa.findAllByOrderByDataLancamentoDescIdDesc().stream()
                .map(LancamentoAjusteJpa::toDomain)
                .toList();
    }

    @Override
    public double somarPorCategoria(CategoriaId categoriaId, LocalDate inicio, LocalDate fim) {
        return jpa.somarPorCategoria(categoriaId.getValor(), inicio, fim);
    }
}
