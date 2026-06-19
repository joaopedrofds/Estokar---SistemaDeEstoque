package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.FaturaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FaturaJpaRepository extends JpaRepository<FaturaJpaEntity, Integer> {
    List<FaturaJpaEntity> findAllByOrderByDataVencimentoAsc();

    @Query("select f from FaturaJpaEntity f where f.cliente.id = :clienteId and f.dataVencimento < :limite and upper(f.status) <> 'PAGO' order by f.dataVencimento asc")
    List<FaturaJpaEntity> buscarVencidasAlemDoLimite(@Param("clienteId") Integer clienteId, @Param("limite") LocalDate limite);

    @Query("select f from FaturaJpaEntity f where f.acordoPagamento.id = :acordoId and f.dataVencimento < :hoje and upper(f.status) <> 'PAGO'")
    List<FaturaJpaEntity> buscarParcelasAtrasadasDoAcordo(@Param("acordoId") Integer acordoId, @Param("hoje") LocalDate hoje);

    Optional<FaturaJpaEntity> findFirstByPedidoId(Integer pedidoId);
}
