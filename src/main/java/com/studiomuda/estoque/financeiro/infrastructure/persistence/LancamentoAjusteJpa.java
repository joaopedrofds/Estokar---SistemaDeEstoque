package com.studiomuda.estoque.financeiro.infrastructure.persistence;

import com.studiomuda.estoque.financeiro.domain.CategoriaId;
import com.studiomuda.estoque.financeiro.domain.LancamentoAjuste;
import com.studiomuda.estoque.financeiro.domain.LancamentoAjusteId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade JPA do agregado {@link LancamentoAjuste} (E-12). Id como {@code String}
 * do {@link LancamentoAjusteId}; {@code criado_em} é gerado pelo banco
 * ({@code insertable=false, updatable=false}). Mapeamento manual via
 * {@code fromDomain}/{@code toDomain} — padrão PetCollar.
 */
@Entity
@Table(name = "lancamento_ajuste")
public class LancamentoAjusteJpa {

    @Id
    private String id;

    @Column(name = "categoria_id", nullable = false)
    private String categoriaId;

    @Column(name = "data_lancamento")
    private LocalDate dataLancamento;

    @Column(name = "valor")
    private double valor;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "username")
    private String username;

    @Column(name = "criado_em", insertable = false, updatable = false)
    private LocalDateTime criadoEm;

    protected LancamentoAjusteJpa() {}

    public static LancamentoAjusteJpa fromDomain(LancamentoAjuste l) {
        LancamentoAjusteJpa jpa = new LancamentoAjusteJpa();
        jpa.id = l.getId().getValor();
        jpa.categoriaId = l.getCategoriaId().getValor();
        jpa.dataLancamento = l.getDataLancamento();
        jpa.valor = l.getValor();
        jpa.descricao = l.getDescricao();
        jpa.usuarioId = l.getUsuarioId();
        jpa.username = l.getUsername();
        return jpa;
    }

    public LancamentoAjuste toDomain() {
        return new LancamentoAjuste(
                LancamentoAjusteId.de(id),
                CategoriaId.de(categoriaId),
                dataLancamento,
                valor,
                descricao,
                usuarioId,
                username,
                criadoEm);
    }
}
