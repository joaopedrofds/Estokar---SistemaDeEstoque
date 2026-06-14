package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.ItemPedidoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemPedidoJpaRepository extends JpaRepository<ItemPedidoJpaEntity, Integer> {
    List<ItemPedidoJpaEntity> findByPedidoId(Integer pedidoId);
}
