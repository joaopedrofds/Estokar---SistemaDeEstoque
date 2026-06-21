package com.studiomuda.estoque.security.infrastructure.persistence;

import com.studiomuda.estoque.security.dominio.IUsuarioAcessoRepositorio;
import com.studiomuda.estoque.security.dominio.UsuarioAcesso;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** Adapter JPA que implementa a porta {@link IUsuarioAcessoRepositorio}. */
@Repository
public class UsuarioAcessoRepositorioJpa implements IUsuarioAcessoRepositorio {

    private final UsuarioAcessoJpaRepository jpa;

    public UsuarioAcessoRepositorioJpa(UsuarioAcessoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional
    public void salvar(UsuarioAcesso usuario) {
        jpa.save(UsuarioAcessoJpa.fromDomain(usuario));
    }

    @Override
    public Optional<UsuarioAcesso> buscarPorId(int id) {
        return jpa.findById(id).map(UsuarioAcessoJpa::toDomain);
    }

    @Override
    public Optional<UsuarioAcesso> buscarPorUsername(String username) {
        return jpa.findByUsername(username).map(UsuarioAcessoJpa::toDomain);
    }

    @Override
    public List<UsuarioAcesso> listarTodos() {
        return jpa.findAllByOrderByAtivoDescUsernameAsc().stream()
                .map(UsuarioAcessoJpa::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void inativar(int id) {
        jpa.findById(id).ifPresent(registro -> {
            UsuarioAcesso usuario = registro.toDomain();
            usuario.inativar();
            jpa.save(UsuarioAcessoJpa.fromDomain(usuario));
        });
    }
}
