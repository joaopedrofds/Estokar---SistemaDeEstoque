package com.studiomuda.estoque.security.infrastructure.persistence;

import com.studiomuda.estoque.security.dominio.ILogAcessoRepositorio;
import com.studiomuda.estoque.security.dominio.LogAcesso;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Adapter JPA que implementa a porta {@link ILogAcessoRepositorio}. */
@Repository
public class LogAcessoRepositorioJpa implements ILogAcessoRepositorio {

    private final LogAcessoJpaRepository jpa;

    public LogAcessoRepositorioJpa(LogAcessoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<LogAcesso> listarRecentes(String resultado, int limite) {
        Pageable pagina = PageRequest.of(0, Math.max(1, limite));
        List<LogAcessoJpa> registros = (resultado == null || resultado.trim().isEmpty())
                ? jpa.findAllByOrderByDataHoraDesc(pagina)
                : jpa.findByResultadoOrderByDataHoraDesc(resultado.trim().toUpperCase(), pagina);
        return registros.stream().map(LogAcessoJpa::toDomain).toList();
    }
}
