package com.studiomuda.estoque.repository;

import com.studiomuda.estoque.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository JPA para ItemPedido.
 * Camada de Persistência: ORM via Spring Data JPA
 */
@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Integer> {
    List<ItemPedido> findByPedidoId(int pedidoId);

    @Modifying
    @Query("DELETE FROM ItemPedido ip WHERE ip.pedidoId = :pedidoId AND ip.produtoId = :produtoId")
    void deleteByPedidoIdAndProdutoId(int pedidoId, int produtoId);
}