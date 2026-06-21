package com.studiomuda.estoque.indicadores.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório Spring Data do {@link IndicadorOperacionalJpa} (chave {@code String}).
 */
public interface IndicadorOperacionalJpaRepository extends JpaRepository<IndicadorOperacionalJpa, String> {

    List<IndicadorOperacionalJpa> findAllByOrderByNomeAsc();

    List<IndicadorOperacionalJpa> findByAtivoTrueOrderByNomeAsc();

    Optional<IndicadorOperacionalJpa> findByCodigo(String codigo);
}
