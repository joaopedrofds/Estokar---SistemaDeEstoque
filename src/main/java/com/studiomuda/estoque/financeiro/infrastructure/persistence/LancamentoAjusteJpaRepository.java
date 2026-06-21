package com.studiomuda.estoque.financeiro.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/** Spring Data JPA do agregado {@link LancamentoAjusteJpa} (E-12). */
public interface LancamentoAjusteJpaRepository extends JpaRepository<LancamentoAjusteJpa, String> {

    List<LancamentoAjusteJpa> findAllByOrderByDataLancamentoDescIdDesc();

    @Query("SELECT COALESCE(SUM(l.valor),0) FROM LancamentoAjusteJpa l "
            + "WHERE l.categoriaId = :categoriaId "
            + "AND l.dataLancamento BETWEEN :inicio AND :fim")
    double somarPorCategoria(@Param("categoriaId") String categoriaId,
                             @Param("inicio") LocalDate inicio,
                             @Param("fim") LocalDate fim);
}
