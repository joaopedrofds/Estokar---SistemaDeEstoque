package com.studiomuda.estoque.security.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Repositório Spring Data da {@link PermissaoPerfilJpa} (chave {@code Integer}). */
public interface PermissaoPerfilJpaRepository extends JpaRepository<PermissaoPerfilJpa, Integer> {

    List<PermissaoPerfilJpa> findByPerfilIdOrderByRecursoAscOperacaoAsc(int perfilId);

    void deleteByPerfilId(int perfilId);
}
