package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.AcordoPagamentoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AcordoPagamentoJpaRepository extends JpaRepository<AcordoPagamentoJpaEntity, Integer> {
    List<AcordoPagamentoJpaEntity> findAllByOrderByDataAcordoDesc();

    @Query("select a from AcordoPagamentoJpaEntity a where a.cliente.id = :clienteId and upper(a.status) = 'ATIVO' and a.dataInicio <= :hoje and (a.dataFim is null or a.dataFim >= :hoje)")
    List<AcordoPagamentoJpaEntity> buscarAtivosValidos(@Param("clienteId") Integer clienteId, @Param("hoje") LocalDate hoje);
}
