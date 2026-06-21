package com.studiomuda.estoque.security.infrastructure.persistence;

import com.studiomuda.estoque.security.dominio.PermissaoPerfil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entidade JPA da {@link PermissaoPerfil} (E-11). Id {@code int} auto-increment
 * legado. {@code recurso}/{@code operacao} persistidos como {@code String}.
 */
@Entity
@Table(name = "permissao_perfil")
public class PermissaoPerfilJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "perfil_id")
    private int perfilId;

    @Column(name = "recurso")
    private String recurso;

    @Column(name = "operacao")
    private String operacao;

    @Column(name = "permitido")
    private boolean permitido;

    protected PermissaoPerfilJpa() {}

    public static PermissaoPerfilJpa fromDomain(PermissaoPerfil p) {
        PermissaoPerfilJpa jpa = new PermissaoPerfilJpa();
        jpa.id = p.getId() == 0 ? null : p.getId();
        jpa.perfilId = p.getPerfilId();
        jpa.recurso = p.getRecurso();
        jpa.operacao = p.getOperacao();
        jpa.permitido = p.isPermitido();
        return jpa;
    }

    public PermissaoPerfil toDomain() {
        return new PermissaoPerfil(id == null ? 0 : id, perfilId, recurso, operacao, permitido);
    }
}
