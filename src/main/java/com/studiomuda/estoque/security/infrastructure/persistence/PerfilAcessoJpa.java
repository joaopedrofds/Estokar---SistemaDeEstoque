package com.studiomuda.estoque.security.infrastructure.persistence;

import com.studiomuda.estoque.security.dominio.PerfilAcesso;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entidade JPA do {@link PerfilAcesso} (E-11). Id {@code int} auto-increment
 * legado (mantido). Mapeamento manual via {@code fromDomain}/{@code toDomain}.
 */
@Entity
@Table(name = "perfil_acesso")
public class PerfilAcessoJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "ativo")
    private boolean ativo;

    protected PerfilAcessoJpa() {}

    public static PerfilAcessoJpa fromDomain(PerfilAcesso p) {
        PerfilAcessoJpa jpa = new PerfilAcessoJpa();
        jpa.id = p.getId() == 0 ? null : p.getId(); // 0 = novo → deixa o banco gerar
        jpa.nome = p.getNome();
        jpa.descricao = p.getDescricao();
        jpa.ativo = p.isAtivo();
        return jpa;
    }

    public PerfilAcesso toDomain() {
        return new PerfilAcesso(id == null ? 0 : id, nome, descricao, ativo);
    }
}
