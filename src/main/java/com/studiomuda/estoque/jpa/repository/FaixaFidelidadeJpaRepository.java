package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.FaixaFidelidadeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FaixaFidelidadeJpaRepository extends JpaRepository<FaixaFidelidadeJpaEntity, Integer> {
    List<FaixaFidelidadeJpaEntity> findAllByOrderByDiasMinimoAsc();

    @Query("select f from FaixaFidelidadeJpaEntity f where f.ativa = true and f.diasMinimo <= :media and f.diasMaximo >= :media order by f.diasMinimo asc")
    Optional<FaixaFidelidadeJpaEntity> buscarPorMedia(@Param("media") double media);

    @Query("select count(f) from FaixaFidelidadeJpaEntity f where f.ativa = true and (:id is null or f.id <> :id) and f.diasMinimo <= :maximo and f.diasMaximo >= :minimo")
    long contarSobreposicoes(@Param("id") Integer id, @Param("minimo") int minimo, @Param("maximo") int maximo);
}
