package com.studiomuda.estoque.repository;

import com.studiomuda.estoque.model.AlertaReposicao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlertaReposicaoRepository extends JpaRepository<AlertaReposicao, Integer> {
    List<AlertaReposicao> findByStatusOrderByCriadoEmDesc(String status);
    Optional<AlertaReposicao> findByProdutoIdAndStatus(Integer produtoId, String status);
}
