package com.studiomuda.estoque.financeiro.infrastructure.persistence;

import com.studiomuda.estoque.financeiro.domain.CategoriaFinanceira;
import com.studiomuda.estoque.financeiro.domain.CategoriaId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entidade JPA do agregado {@link CategoriaFinanceira} (E-12). Id como
 * {@code String} do {@link CategoriaId} (sem {@code @GeneratedValue});
 * mapeamento manual via {@code fromDomain}/{@code toDomain} — padrão PetCollar.
 */
@Entity
@Table(name = "categoria_financeira")
public class CategoriaFinanceiraJpa {

    @Id
    private String id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "origem_sistema")
    private String origemSistema;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "ativo")
    private boolean ativo;

    protected CategoriaFinanceiraJpa() {}

    public static CategoriaFinanceiraJpa fromDomain(CategoriaFinanceira c) {
        CategoriaFinanceiraJpa jpa = new CategoriaFinanceiraJpa();
        jpa.id = c.getId().getValor();
        jpa.nome = c.getNome();
        jpa.tipo = c.getTipo();
        jpa.origemSistema = c.getOrigemSistema();
        jpa.descricao = c.getDescricao();
        jpa.ativo = c.isAtivo();
        return jpa;
    }

    public CategoriaFinanceira toDomain() {
        return new CategoriaFinanceira(
                CategoriaId.de(id),
                nome,
                tipo,
                origemSistema,
                descricao,
                ativo);
    }
}
