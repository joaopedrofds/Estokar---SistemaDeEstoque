package com.studiomuda.estoque.repository;

import com.studiomuda.estoque.model.AlertaIndicador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaIndicadorRepository extends JpaRepository<AlertaIndicador, Integer> {

    List<AlertaIndicador> findByStatusOrderByDataAlertaDesc(String status);

    AlertaIndicador findFirstByIndicadorIdAndStatusOrderByDataAlertaDesc(int indicadorId, String status);
}
