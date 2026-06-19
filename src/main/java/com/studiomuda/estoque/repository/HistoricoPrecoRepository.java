package com.studiomuda.estoque.repository;

import com.studiomuda.estoque.model.HistoricoPreco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository JPA para HistoricoPreco.
 * Camada de Persistência: ORM via Spring Data JPA
 */
@Repository
public interface HistoricoPrecoRepository extends JpaRepository<HistoricoPreco, Integer> {
    List<HistoricoPreco> findByProdutoIdOrderByDataAlteracaoDesc(int produtoId);

    @Query("SELECT h FROM HistoricoPreco h ORDER BY h.dataAlteracao DESC")
    List<HistoricoPreco> findAllOrderByDataAlteracaoDesc();
}