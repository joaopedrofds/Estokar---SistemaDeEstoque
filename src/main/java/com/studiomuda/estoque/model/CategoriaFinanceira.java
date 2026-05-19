package com.studiomuda.estoque.model;

public class CategoriaFinanceira {
    private int id;
    private String nome;
    private String tipo;
    private String origemSistema;
    private String descricao;
    private boolean ativo;

    public CategoriaFinanceira() {
        this.ativo = true;
    }

    public CategoriaFinanceira(int id, String nome, String tipo, String origemSistema, String descricao, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.origemSistema = origemSistema;
        this.descricao = descricao;
        this.ativo = ativo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getOrigemSistema() {
        return origemSistema;
    }

    public void setOrigemSistema(String origemSistema) {
        this.origemSistema = origemSistema;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
