package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.ItemPedidoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemPedidoJpaRepository extends JpaRepository<ItemPedidoJpaEntity, Integer> {
    @Query("select ip from ItemPedidoJpaEntity ip where ip.pedido.id = :pedidoId")
    List<ItemPedidoJpaEntity> findByPedidoId(@Param("pedidoId") Integer pedidoId);

    @Query("select coalesce(sum(ip.quantidade * ip.produto.valor), 0) from ItemPedidoJpaEntity ip where ip.pedido.id = :pedidoId")
    Double calcularValorTotalPorPedido(@Param("pedidoId") Integer pedidoId);
}
