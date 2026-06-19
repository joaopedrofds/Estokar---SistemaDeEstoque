package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.CalendarioExcecaoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;

public interface CalendarioExcecaoJpaRepository extends JpaRepository<CalendarioExcecaoJpaEntity, Integer> {
    boolean existsByDataAndAtivaTrue(Date data);
}
