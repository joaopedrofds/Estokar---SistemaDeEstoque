package com.studiomuda.estoque.repository;

import com.studiomuda.estoque.model.Devolucao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DevolucaoRepository extends JpaRepository<Devolucao, Integer> {

    @Query("SELECT d FROM Devolucao d WHERE d.status = :status ORDER BY d.dataSolicitacao DESC")
    List<Devolucao> findByStatusOrderByDataSolicitacaoDesc(@Param("status") String status);
}