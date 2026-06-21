package com.studiomuda.estoque.security.infrastructure.persistence;

import com.studiomuda.estoque.security.dominio.IPerfilAcessoRepositorio;
import com.studiomuda.estoque.security.dominio.PerfilAcesso;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Adapter JPA que implementa a porta {@link IPerfilAcessoRepositorio}. */
@Repository
public class PerfilAcessoRepositorioJpa implements IPerfilAcessoRepositorio {

    private final PerfilAcessoJpaRepository jpa;

    public PerfilAcessoRepositorioJpa(PerfilAcessoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void salvar(PerfilAcesso perfil) {
        jpa.save(PerfilAcessoJpa.fromDomain(perfil));
    }

    @Override
    public Optional<PerfilAcesso> buscarPorId(int id) {
        return jpa.findById(id).map(PerfilAcessoJpa::toDomain);
    }

    @Override
    public Optional<PerfilAcesso> buscarPorNome(String nome) {
        return jpa.findByNome(nome).map(PerfilAcessoJpa::toDomain);
    }

    @Override
    public List<PerfilAcesso> listarTodos() {
        return jpa.findAllByOrderByAtivoDescNomeAsc().stream().map(PerfilAcessoJpa::toDomain).toList();
    }

    @Override
    public List<PerfilAcesso> listarAtivos() {
        return jpa.findByAtivoTrueOrderByNomeAsc().stream().map(PerfilAcessoJpa::toDomain).toList();
    }
}
