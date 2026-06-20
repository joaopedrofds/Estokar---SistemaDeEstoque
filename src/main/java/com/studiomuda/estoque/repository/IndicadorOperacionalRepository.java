package com.studiomuda.estoque.repository;

import com.studiomuda.estoque.model.IndicadorOperacional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndicadorOperacionalRepository extends JpaRepository<IndicadorOperacional, Integer> {

    List<IndicadorOperacional> findAllByOrderByNomeAsc();

    List<IndicadorOperacional> findByAtivoTrueOrderByNomeAsc();

    Optional<IndicadorOperacional> findByCodigo(String codigo);
}
