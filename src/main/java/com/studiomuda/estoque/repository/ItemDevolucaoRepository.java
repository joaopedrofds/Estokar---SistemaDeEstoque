package com.studiomuda.estoque.repository;

import com.studiomuda.estoque.model.ItemDevolucao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemDevolucaoRepository extends JpaRepository<ItemDevolucao, Integer> {

    List<ItemDevolucao> findByDevolucaoId(int devolucaoId);
}