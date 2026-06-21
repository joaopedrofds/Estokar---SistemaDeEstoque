package com.studiomuda.estoque.financeiro.infrastructure.persistence;

import com.studiomuda.estoque.financeiro.domain.ITemplateRelatorioRepositorio;
import com.studiomuda.estoque.financeiro.domain.TemplateId;
import com.studiomuda.estoque.financeiro.domain.TemplateRelatorio;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adapter JPA que implementa a porta de domínio {@link ITemplateRelatorioRepositorio},
 * traduzindo domínio ↔ {@link TemplateRelatorioJpa} via {@code fromDomain}/{@code toDomain}.
 */
@Repository
public class TemplateRelatorioRepositorioJpa implements ITemplateRelatorioRepositorio {

    private final TemplateRelatorioJpaRepository jpa;

    public TemplateRelatorioRepositorioJpa(TemplateRelatorioJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional
    public void salvar(TemplateRelatorio template) {
        jpa.save(TemplateRelatorioJpa.fromDomain(template));
    }

    @Override
    public Optional<TemplateRelatorio> buscarPorId(TemplateId id) {
        return jpa.findById(id.getValor()).map(TemplateRelatorioJpa::toDomain);
    }

    @Override
    public List<TemplateRelatorio> listarTodos() {
        return jpa.findAllByOrderByAtivoDescNomeAsc().stream()
                .map(TemplateRelatorioJpa::toDomain)
                .toList();
    }

    @Override
    public List<TemplateRelatorio> listarAtivos() {
        return jpa.findByAtivoTrueOrderByNomeAsc().stream()
                .map(TemplateRelatorioJpa::toDomain)
                .toList();
    }
}
