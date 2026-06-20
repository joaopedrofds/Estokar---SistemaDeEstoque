package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.BeneficioCategoriaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BeneficioCategoriaJpaRepository extends JpaRepository<BeneficioCategoriaJpaEntity, Integer> {
    List<BeneficioCategoriaJpaEntity> findAllByOrderByFaixaDiasMinimoAsc();
    Optional<BeneficioCategoriaJpaEntity> findFirstByFaixaIdAndAtivoTrueAndTipoOrderByIdAsc(Integer faixaId, String tipo);
    long countByFaixaId(Integer faixaId);
}
