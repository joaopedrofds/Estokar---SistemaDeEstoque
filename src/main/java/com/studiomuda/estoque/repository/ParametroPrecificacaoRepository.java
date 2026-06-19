package com.studiomuda.estoque.repository;

import com.studiomuda.estoque.model.ParametroPrecificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ParametroPrecificacaoRepository extends JpaRepository<ParametroPrecificacao, Integer> {
    Optional<ParametroPrecificacao> findFirstByOrderByIdDesc();
}