package com.studiomuda.estoque.repository;

import com.studiomuda.estoque.model.RegraPrecificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegraPrecificacaoRepository extends JpaRepository<RegraPrecificacao, Integer> {
    Optional<RegraPrecificacao> findFirstByProdutoIdAndAtivoTrueOrderByDataCriacaoDesc(int produtoId);
    List<RegraPrecificacao> findAllByOrderByDataCriacaoDesc();
    List<RegraPrecificacao> findByAtivoTrue();
}