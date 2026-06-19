package com.studiomuda.estoque.model;

public class Fornecedor {
    private int id;
    private String nome;
    private String cnpj;
    private String email;
    private String telefone;
    private String endereco;
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

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public int getLeadTimeDias() { return leadTimeDias; }
    public void setLeadTimeDias(int leadTimeDias) { this.leadTimeDias = leadTimeDias; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
