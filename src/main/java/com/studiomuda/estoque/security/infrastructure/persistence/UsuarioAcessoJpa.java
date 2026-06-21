package com.studiomuda.estoque.security.infrastructure.persistence;

import com.studiomuda.estoque.security.dominio.UsuarioAcesso;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Entidade JPA do {@link UsuarioAcesso} (E-11). O vínculo com perfis usa a
 * tabela legada {@code usuario_perfil}; o ID do usuário permanece
 * auto-incremento inteiro.
 */
@Entity
@Table(name = "usuario_acesso")
public class UsuarioAcessoJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", nullable = false, unique = true, length = 60)
    private String username;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "senha", nullable = false, length = 255)
    private String senha;

    @Column(name = "ativo")
    private boolean ativo;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_perfil", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "perfil_id", nullable = false)
    private Set<Integer> perfilIds = new LinkedHashSet<>();

    protected UsuarioAcessoJpa() {}

    public static UsuarioAcessoJpa fromDomain(UsuarioAcesso usuario) {
        UsuarioAcessoJpa jpa = new UsuarioAcessoJpa();
        jpa.id = usuario.getId() == 0 ? null : usuario.getId();
        jpa.username = usuario.getUsername();
        jpa.nome = usuario.getNome();
        jpa.senha = usuario.getSenha();
        jpa.ativo = usuario.isAtivo();
        jpa.perfilIds = new LinkedHashSet<>(usuario.getPerfilIds());
        return jpa;
    }

    public UsuarioAcesso toDomain() {
        List<Integer> ids = new ArrayList<>(perfilIds);
        return new UsuarioAcesso(id == null ? 0 : id, username, nome, senha, ativo, ids);
    }
}
