package com.studiomuda.estoque.security.dominio;

import java.util.List;
import java.util.Optional;

/** Porta de domínio do agregado {@link PerfilAcesso} (E-11). */
public interface IPerfilAcessoRepositorio {

    void salvar(PerfilAcesso perfil);

    Optional<PerfilAcesso> buscarPorId(int id);

    Optional<PerfilAcesso> buscarPorNome(String nome);

    List<PerfilAcesso> listarTodos();

    List<PerfilAcesso> listarAtivos();
}
