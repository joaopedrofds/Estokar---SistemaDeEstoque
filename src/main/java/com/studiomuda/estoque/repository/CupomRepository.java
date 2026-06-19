package com.studiomuda.estoque.repository;

import com.studiomuda.estoque.model.Cupom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository JPA para Cupom.
 * Camada de Persistência: ORM via Spring Data JPA
 */
@Repository
public interface CupomRepository extends JpaRepository<Cupom, Integer> {
    Optional<Cupom> findByCodigo(String codigo);
    List<Cupom> findByAtivoTrueOrderByDataInicioDesc();
    @Query("SELECT c FROM Cupom c WHERE c.validade > :data AND c.ativo = :ativo")
    List<Cupom> findByValidadeAfterAndAtivoTrue(java.time.LocalDate data, boolean ativo);
}