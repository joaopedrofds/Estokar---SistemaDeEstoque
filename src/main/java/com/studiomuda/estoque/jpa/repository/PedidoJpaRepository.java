package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.PedidoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface PedidoJpaRepository extends JpaRepository<PedidoJpaEntity, Integer> {
    List<PedidoJpaEntity> findByStatusOrderByDataCancelamentoDesc(String status);

    @Query("select p.dataRequisicao from PedidoJpaEntity p where p.cliente.id = :clienteId and upper(p.status) = 'CONCLUIDO' and p.dataRequisicao is not null order by p.dataRequisicao desc")
    List<Date> listarDatasComprasConfirmadas(@Param("clienteId") Integer clienteId);
}
