package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.HistoricoCobrancaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoCobrancaJpaRepository extends JpaRepository<HistoricoCobrancaJpaEntity, Integer> {
    List<HistoricoCobrancaJpaEntity> findAllByOrderByDataContatoDesc();
}
