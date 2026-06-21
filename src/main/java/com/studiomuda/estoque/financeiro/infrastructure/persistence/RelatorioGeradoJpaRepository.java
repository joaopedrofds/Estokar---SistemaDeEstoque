package com.studiomuda.estoque.financeiro.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repositório Spring Data JPA do agregado {@code RelatorioGerado} (E-12).
 * O histórico é limitado via {@link Pageable} (ex.: {@code PageRequest.of(0, limite)}).
 */
public interface RelatorioGeradoJpaRepository extends JpaRepository<RelatorioGeradoJpa, String> {

    @Query("SELECT r FROM RelatorioGeradoJpa r ORDER BY r.dataGeracao DESC")
    List<RelatorioGeradoJpa> listarHistorico(Pageable pageable);
}
