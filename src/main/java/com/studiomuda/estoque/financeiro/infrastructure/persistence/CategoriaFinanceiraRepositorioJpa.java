package com.studiomuda.estoque.financeiro.infrastructure.persistence;

import com.studiomuda.estoque.financeiro.domain.CategoriaFinanceira;
import com.studiomuda.estoque.financeiro.domain.CategoriaId;
import com.studiomuda.estoque.financeiro.domain.ICategoriaFinanceiraRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter JPA que implementa a porta de domínio {@link ICategoriaFinanceiraRepositorio},
 * traduzindo domínio ↔ {@link CategoriaFinanceiraJpa} via {@code fromDomain}/{@code toDomain}.
 */
@Repository
public class CategoriaFinanceiraRepositorioJpa implements ICategoriaFinanceiraRepositorio {

    private final CategoriaFinanceiraJpaRepository jpa;

    public CategoriaFinanceiraRepositorioJpa(CategoriaFinanceiraJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void salvar(CategoriaFinanceira categoria) {
        jpa.save(CategoriaFinanceiraJpa.fromDomain(categoria));
    }

    @Override
    public Optional<CategoriaFinanceira> buscarPorId(CategoriaId id) {
        return jpa.findById(id.getValor()).map(CategoriaFinanceiraJpa::toDomain);
    }

    @Override
    public List<CategoriaFinanceira> listarTodas() {
        return jpa.findAllByOrderByAtivoDescTipoAscNomeAsc().stream()
                .map(CategoriaFinanceiraJpa::toDomain)
                .toList();
    }

    @Override
    public List<CategoriaFinanceira> listarAtivas() {
        return jpa.findByAtivoTrueOrderByTipoAscNomeAsc().stream()
                .map(CategoriaFinanceiraJpa::toDomain)
                .toList();
    }
}
