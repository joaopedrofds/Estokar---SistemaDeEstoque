package com.studiomuda.estoque.indicadores.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositório Spring Data do {@link AlertaIndicadorJpa} (chave {@code String}).
 */
public interface AlertaIndicadorJpaRepository extends JpaRepository<AlertaIndicadorJpa, String> {

    List<AlertaIndicadorJpa> findByStatusOrderByDataAlertaDesc(String status);

    AlertaIndicadorJpa findFirstByIndicadorIdAndStatusOrderByDataAlertaDesc(String indicadorId, String status);
}
