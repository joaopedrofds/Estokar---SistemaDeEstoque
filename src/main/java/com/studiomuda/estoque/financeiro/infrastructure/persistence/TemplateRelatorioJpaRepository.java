package com.studiomuda.estoque.financeiro.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositório Spring Data do agregado {@link TemplateRelatorioJpa} (E-12).
 */
public interface TemplateRelatorioJpaRepository extends JpaRepository<TemplateRelatorioJpa, String> {

    List<TemplateRelatorioJpa> findAllByOrderByAtivoDescNomeAsc();

    List<TemplateRelatorioJpa> findByAtivoTrueOrderByNomeAsc();
}
