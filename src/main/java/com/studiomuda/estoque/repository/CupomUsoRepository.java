package com.studiomuda.estoque.repository;

import com.studiomuda.estoque.model.CupomUso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CupomUsoRepository extends JpaRepository<CupomUso, Integer> {

    List<CupomUso> findByCupomIdOrderByDataUsoDesc(int cupomId);
    List<CupomUso> findAllByOrderByDataUsoDesc();
}