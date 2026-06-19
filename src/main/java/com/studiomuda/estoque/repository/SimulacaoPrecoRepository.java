package com.studiomuda.estoque.repository;

import com.studiomuda.estoque.model.SimulacaoPreco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SimulacaoPrecoRepository extends JpaRepository<SimulacaoPreco, Integer> {
    List<SimulacaoPreco> findByProdutoIdOrderByDataSimulacaoDesc(int produtoId);
    List<SimulacaoPreco> findAllByOrderByDataSimulacaoDesc();
    List<SimulacaoPreco> findByStatusOrderByDataSimulacaoDesc(String status);
    List<SimulacaoPreco> findByAplicadoTrueOrderByDataSimulacaoDesc();
}