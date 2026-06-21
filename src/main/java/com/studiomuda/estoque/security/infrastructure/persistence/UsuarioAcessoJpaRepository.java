package com.studiomuda.estoque.security.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Repositório Spring Data do {@link UsuarioAcessoJpa} (chave {@code Integer}). */
public interface UsuarioAcessoJpaRepository extends JpaRepository<UsuarioAcessoJpa, Integer> {

    Optional<UsuarioAcessoJpa> findByUsername(String username);

    List<UsuarioAcessoJpa> findAllByOrderByAtivoDescUsernameAsc();
}
