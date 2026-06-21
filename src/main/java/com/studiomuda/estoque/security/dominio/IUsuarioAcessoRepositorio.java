package com.studiomuda.estoque.security.dominio;

import java.util.List;
import java.util.Optional;

/** Porta de domínio do agregado {@link UsuarioAcesso} (E-11). */
public interface IUsuarioAcessoRepositorio {

    void salvar(UsuarioAcesso usuario);

    Optional<UsuarioAcesso> buscarPorId(int id);

    Optional<UsuarioAcesso> buscarPorUsername(String username);

    List<UsuarioAcesso> listarTodos();

    void inativar(int id);
}
