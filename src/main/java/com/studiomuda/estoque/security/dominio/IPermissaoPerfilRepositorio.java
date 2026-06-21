package com.studiomuda.estoque.security.dominio;

import java.util.List;
import java.util.Map;
import java.util.Set;

/** Porta de domínio da matriz de permissões {@link PermissaoPerfil} (E-11). */
public interface IPermissaoPerfilRepositorio {

    /** Mapa {@code recurso → {operações permitidas}} de um perfil (só concessões). */
    Map<String, Set<String>> carregarMapaPermissoes(int perfilId);

    /** Substitui (apaga e regrava) todas as permissões do perfil. */
    void substituir(int perfilId, List<PermissaoPerfil> permissoes);
}
