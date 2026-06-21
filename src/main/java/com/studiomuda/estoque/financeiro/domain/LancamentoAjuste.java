package com.studiomuda.estoque.financeiro.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Lançamento de ajuste manual de valor em uma categoria financeira (E-12) —
 * agregado de domínio puro (padrão PetCollar). Identidade por
 * {@link LancamentoAjusteId}; referencia a categoria por {@link CategoriaId}.
 *
 * <p>{@code criadoEm} é atribuído pelo banco ({@code DEFAULT CURRENT_TIMESTAMP}),
 * logo é nulo num lançamento recém-criado e preenchido só na reconstrução.</p>
 */
public class LancamentoAjuste {

    private final LancamentoAjusteId id;
    private final CategoriaId categoriaId;
    private final LocalDate dataLancamento;
    private final double valor;
    private final String descricao;
    private final Integer usuarioId;
    private final String username;
    private final LocalDateTime criadoEm;

    public LancamentoAjuste(LancamentoAjusteId id, CategoriaId categoriaId, LocalDate dataLancamento,
                            double valor, String descricao, Integer usuarioId, String username) {
        this(id, categoriaId, dataLancamento, valor, descricao, usuarioId, username, null);
    }

    // Construtor de RECONSTRUÇÃO (infra → domínio); inclui criadoEm do banco.
    public LancamentoAjuste(LancamentoAjusteId id, CategoriaId categoriaId, LocalDate dataLancamento,
                            double valor, String descricao, Integer usuarioId, String username,
                            LocalDateTime criadoEm) {
        if (id == null) {
            throw new IllegalArgumentException("Id do lançamento não pode ser nulo.");
        }
        if (categoriaId == null) {
            throw new IllegalArgumentException("Categoria do lançamento não pode ser nula.");
        }
        if (dataLancamento == null) {
            throw new IllegalArgumentException("Data do lançamento não pode ser nula.");
        }
        this.id = id;
        this.categoriaId = categoriaId;
        this.dataLancamento = dataLancamento;
        this.valor = valor;
        this.descricao = descricao;
        this.usuarioId = usuarioId;
        this.username = username;
        this.criadoEm = criadoEm;
    }

    public LancamentoAjusteId getId() { return id; }
    public CategoriaId getCategoriaId() { return categoriaId; }
    public LocalDate getDataLancamento() { return dataLancamento; }
    public double getValor() { return valor; }
    public String getDescricao() { return descricao; }
    public Integer getUsuarioId() { return usuarioId; }
    public String getUsername() { return username; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
