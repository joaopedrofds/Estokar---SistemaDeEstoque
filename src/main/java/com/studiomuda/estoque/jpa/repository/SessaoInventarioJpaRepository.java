package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.SessaoInventarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessaoInventarioJpaRepository extends JpaRepository<SessaoInventarioJpaEntity, Integer> {
    boolean existsBySetorIgnoreCaseAndStatus(String setor, String status);

    List<SessaoInventarioJpaEntity> findAllByOrderByDataAberturaDescIdDesc();
}
