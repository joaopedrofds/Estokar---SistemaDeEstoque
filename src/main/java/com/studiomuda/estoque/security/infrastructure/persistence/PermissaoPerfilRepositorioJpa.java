package com.studiomuda.estoque.security.infrastructure.persistence;

import com.studiomuda.estoque.security.OperacaoAcesso;
import com.studiomuda.estoque.security.RecursoAcesso;
import com.studiomuda.estoque.security.dominio.IPermissaoPerfilRepositorio;
import com.studiomuda.estoque.security.dominio.PermissaoPerfil;
import com.studiomuda.estoque.security.dominio.PoliticaDeAcesso;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Adapter JPA da porta {@link IPermissaoPerfilRepositorio}. A consolidação da
 * matriz (filtra concessões e dobra em {@code recurso → operações}) é delegada
 * ao domínio {@link PoliticaDeAcesso}; linhas com recurso/operação fora dos enums
 * (ex.: seed legado {@code INVENTARIO}) são ignoradas, preservando o comportamento.
 */
@Repository
public class PermissaoPerfilRepositorioJpa implements IPermissaoPerfilRepositorio {

    private final PermissaoPerfilJpaRepository jpa;

    public PermissaoPerfilRepositorioJpa(PermissaoPerfilJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Map<String, Set<String>> carregarMapaPermissoes(int perfilId) {
        List<PoliticaDeAcesso.Permissao> permissoes = new ArrayList<>();
        for (PermissaoPerfilJpa j : jpa.findByPerfilIdOrderByRecursoAscOperacaoAsc(perfilId)) {
            PermissaoPerfil p = j.toDomain();
            try {
                permissoes.add(new PoliticaDeAcesso.Permissao(
                        p.getPerfilId(),
                        RecursoAcesso.valueOf(p.getRecurso()),
                        OperacaoAcesso.valueOf(p.getOperacao()),
                        p.isPermitido()));
            } catch (IllegalArgumentException naoMapeado) {
                // recurso/operação fora do enum: ignora (comportamento legado)
            }
        }
        return PoliticaDeAcesso.montarMapaPermitidas(permissoes);
    }

    @Override
    @Transactional
    public void substituir(int perfilId, List<PermissaoPerfil> permissoes) {
        jpa.deleteByPerfilId(perfilId);
        List<PermissaoPerfilJpa> novos = new ArrayList<>();
        for (PermissaoPerfil p : permissoes) {
            novos.add(PermissaoPerfilJpa.fromDomain(p));
        }
        jpa.saveAll(novos);
    }
}
