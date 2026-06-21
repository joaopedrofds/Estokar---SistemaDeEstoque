package com.studiomuda.estoque.security.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Repositório Spring Data do {@link LogAcessoJpa} (chave {@code Integer}). */
public interface LogAcessoJpaRepository extends JpaRepository<LogAcessoJpa, Integer> {

    List<LogAcessoJpa> findAllByOrderByDataHoraDesc(Pageable pageable);

    List<LogAcessoJpa> findByResultadoOrderByDataHoraDesc(String resultado, Pageable pageable);
}
