package com.studiomuda.estoque.security.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Repositório Spring Data do {@link PerfilAcessoJpa} (chave {@code Integer}). */
public interface PerfilAcessoJpaRepository extends JpaRepository<PerfilAcessoJpa, Integer> {

    List<PerfilAcessoJpa> findAllByOrderByAtivoDescNomeAsc();

    List<PerfilAcessoJpa> findByAtivoTrueOrderByNomeAsc();

    Optional<PerfilAcessoJpa> findByNome(String nome);
}
