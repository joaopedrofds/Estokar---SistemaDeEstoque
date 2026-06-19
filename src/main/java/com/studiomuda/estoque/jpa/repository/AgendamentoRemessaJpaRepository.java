package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.AgendamentoRemessaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface AgendamentoRemessaJpaRepository extends JpaRepository<AgendamentoRemessaJpaEntity, Integer> {
    @Query("select ar from AgendamentoRemessaJpaEntity ar " +
            "join fetch ar.doca d " +
            "join fetch ar.distribuidora di " +
            "order by ar.data desc, ar.horario desc, ar.id desc")
    List<AgendamentoRemessaJpaEntity> listarComRelacionamentos();

    @Query("select coalesce(sum(ar.volumePaletes), 0) from AgendamentoRemessaJpaEntity ar " +
            "where ar.doca.id = :docaId and ar.data = :data and ar.status = :status")
    Integer somarVolumeConfirmado(@Param("docaId") Integer docaId,
                                  @Param("data") Date data,
                                  @Param("status") String status);

    boolean existsByDoca_IdAndDataAndHorarioAndStatus(Integer docaId, Date data, String horario, String status);
}
