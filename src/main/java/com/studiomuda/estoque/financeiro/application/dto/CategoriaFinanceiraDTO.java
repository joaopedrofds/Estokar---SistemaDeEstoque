package com.studiomuda.estoque.financeiro.application.dto;

import com.studiomuda.estoque.financeiro.domain.CategoriaFinanceira;

/**
 * DTO de apresentação da categoria financeira (E-12): serve tanto à listagem
 * quanto ao formulário (Thymeleaf). Expõe o id como {@code String} ({@code null}
 * num formulário de criação) — padrão View/DTO do PetCollar ({@code de(...)}).
 */
public class CategoriaFinanceiraDTO {

    private final String id;
    private final String nome;
    private final String tipo;
    private final String origemSistema;
    private final String descricao;
    private final boolean ativo;

    public CategoriaFinanceiraDTO(String id, String nome, String tipo, String origemSistema,
                                  String descricao, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.origemSistema = origemSistema;
        this.descricao = descricao;
        this.ativo = ativo;
    }

    public static CategoriaFinanceiraDTO de(CategoriaFinanceira c) {
        return new CategoriaFinanceiraDTO(c.getId().getValor(), c.getNome(), c.getTipo(),
                c.getOrigemSistema(), c.getDescricao(), c.isAtivo());
    }

    /** Formulário em branco para criação (id nulo, ativa por padrão). */
    public static CategoriaFinanceiraDTO vazia() {
        return new CategoriaFinanceiraDTO(null, null, null, null, null, true);
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getTipo() { return tipo; }
    public String getOrigemSistema() { return origemSistema; }
    public String getDescricao() { return descricao; }
    public boolean isAtivo() { return ativo; }
}
