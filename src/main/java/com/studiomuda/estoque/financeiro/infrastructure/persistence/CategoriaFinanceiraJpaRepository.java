package com.studiomuda.estoque.financeiro.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositório Spring Data do {@link CategoriaFinanceiraJpa} (chave {@code String}).
 */
public interface CategoriaFinanceiraJpaRepository extends JpaRepository<CategoriaFinanceiraJpa, String> {

    List<CategoriaFinanceiraJpa> findAllByOrderByAtivoDescTipoAscNomeAsc();

    List<CategoriaFinanceiraJpa> findByAtivoTrueOrderByTipoAscNomeAsc();
}
