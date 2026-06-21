package com.studiomuda.estoque.financeiro.infrastructure.persistence;

import com.studiomuda.estoque.financeiro.domain.IRelatorioGeradoRepositorio;
import com.studiomuda.estoque.financeiro.domain.RelatorioGerado;
import com.studiomuda.estoque.financeiro.domain.RelatorioId;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Adapter JPA que implementa a porta de domínio {@link IRelatorioGeradoRepositorio},
 * traduzindo domínio ↔ {@link RelatorioGeradoJpa} via {@code fromDomain}/{@code toDomain}.
 */
@Repository
public class RelatorioGeradoRepositorioJpa implements IRelatorioGeradoRepositorio {

    private final RelatorioGeradoJpaRepository jpa;

    public RelatorioGeradoRepositorioJpa(RelatorioGeradoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional
    public RelatorioGerado persistir(RelatorioGerado relatorio) {
        // saveAndFlush força o INSERT para o banco gerar data_geracao (DEFAULT CURRENT_TIMESTAMP);
        // como data_geracao é insertable=false, a entidade salva não a contém — re-lemos por id
        // para reconstruir o domínio já com a data atribuída pelo banco.
        jpa.saveAndFlush(RelatorioGeradoJpa.fromDomain(relatorio));
        return jpa.findById(relatorio.getId().getValor())
                .map(RelatorioGeradoJpa::toDomain)
                .orElseThrow(() -> new NoSuchElementException(
                        "Relatório não encontrado após persistir: " + relatorio.getId().getValor()));
    }

    @Override
    public Optional<RelatorioGerado> buscarPorId(RelatorioId id) {
        return jpa.findById(id.getValor()).map(RelatorioGeradoJpa::toDomain);
    }

    @Override
    public List<RelatorioGerado> listarHistorico(int limite) {
        return jpa.listarHistorico(PageRequest.of(0, limite)).stream()
                .map(RelatorioGeradoJpa::toDomain)
                .toList();
    }
}
