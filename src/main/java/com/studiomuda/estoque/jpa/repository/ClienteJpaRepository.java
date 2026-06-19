package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.ClienteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteJpaRepository extends JpaRepository<ClienteJpaEntity, Integer> {
    List<ClienteJpaEntity> findByAtivoTrueOrderByNomeAsc();
}
