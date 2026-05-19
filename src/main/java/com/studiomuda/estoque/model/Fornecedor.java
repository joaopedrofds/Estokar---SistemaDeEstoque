package com.studiomuda.estoque.model;

public class Fornecedor {
    private int id;
    private String nome;
    private int leadTimeDias;
    private boolean ativo;

    public Fornecedor() {
        this.ativo = true;
    }

    public Fornecedor(int id, String nome, int leadTimeDias, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.leadTimeDias = leadTimeDias;
        this.ativo = ativo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getLeadTimeDias() { return leadTimeDias; }
    public void setLeadTimeDias(int leadTimeDias) { this.leadTimeDias = leadTimeDias; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
