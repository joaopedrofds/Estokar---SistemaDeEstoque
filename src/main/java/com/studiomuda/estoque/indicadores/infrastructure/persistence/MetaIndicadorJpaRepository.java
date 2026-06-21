package com.studiomuda.estoque.indicadores.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositório Spring Data do {@link MetaIndicadorJpa} (chave {@code String}).
 */
public interface MetaIndicadorJpaRepository extends JpaRepository<MetaIndicadorJpa, String> {

    @Query("SELECT m FROM MetaIndicadorJpa m " +
           "WHERE m.indicadorId = :indicadorId AND m.ativo = true " +
           "AND m.vigenciaInicio <= CURRENT_DATE " +
           "AND (m.vigenciaFim IS NULL OR m.vigenciaFim >= CURRENT_DATE) " +
           "ORDER BY m.vigenciaInicio DESC")
    List<MetaIndicadorJpa> buscarVigentesPorIndicador(@Param("indicadorId") String indicadorId);

    @Modifying
    @Query("UPDATE MetaIndicadorJpa m SET m.ativo = false " +
           "WHERE m.indicadorId = :indicadorId AND m.id <> :metaAtivaId")
    void desativarOutrasMetas(@Param("indicadorId") String indicadorId, @Param("metaAtivaId") String metaAtivaId);
}
