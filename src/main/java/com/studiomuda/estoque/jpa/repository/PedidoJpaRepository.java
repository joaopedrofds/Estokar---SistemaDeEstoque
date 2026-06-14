package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.PedidoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoJpaRepository extends JpaRepository<PedidoJpaEntity, Integer> {
    List<PedidoJpaEntity> findByStatusOrderByDataCancelamentoDesc(String status);
}
