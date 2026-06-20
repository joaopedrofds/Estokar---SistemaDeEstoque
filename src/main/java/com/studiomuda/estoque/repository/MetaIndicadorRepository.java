package com.studiomuda.estoque.repository;

import com.studiomuda.estoque.model.MetaIndicador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetaIndicadorRepository extends JpaRepository<MetaIndicador, Integer> {

    /**
     * Metas ativas e vigentes (vigencia_inicio <= hoje <= vigencia_fim) de um indicador,
     * mais recente primeiro. O serviço usa o primeiro elemento como meta vigente.
     */
    @Query("SELECT m FROM MetaIndicador m " +
           "WHERE m.indicadorId = :indicadorId AND m.ativo = true " +
           "AND m.vigenciaInicio <= CURRENT_DATE " +
           "AND (m.vigenciaFim IS NULL OR m.vigenciaFim >= CURRENT_DATE) " +
           "ORDER BY m.vigenciaInicio DESC")
    List<MetaIndicador> buscarVigentesPorIndicador(@Param("indicadorId") int indicadorId);

    @Modifying
    @Query("UPDATE MetaIndicador m SET m.ativo = false " +
           "WHERE m.indicadorId = :indicadorId AND m.id <> :metaAtivaId")
    void desativarOutrasMetas(@Param("indicadorId") int indicadorId, @Param("metaAtivaId") int metaAtivaId);
}
