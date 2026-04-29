package com.studiomuda.estoque.application.funcionario.dto;

public class AtualizarFuncionarioCommand {
    private final int id;
    private final String nome;
    private final String cargo;
    private final String telefone;
    private final String cep;
    private final String rua;
    private final String numero;
    private final String bairro;
    private final String cidade;
    private final String estado;
    private final boolean ativo;

    public AtualizarFuncionarioCommand(int id, String nome, String cargo, String telefone,
                                        String cep, String rua, String numero, String bairro,
                                        String cidade, String estado, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.cargo = cargo;
        this.telefone = telefone;
        this.cep = cep;
        this.rua = rua;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.ativo = ativo;
    }

    public int id() { return id; }
    public String nome() { return nome; }
    public String cargo() { return cargo; }
    public String telefone() { return telefone; }
    public String cep() { return cep; }
    public String rua() { return rua; }
    public String numero() { return numero; }
    public String bairro() { return bairro; }
    public String cidade() { return cidade; }
    public String estado() { return estado; }
    public boolean ativo() { return ativo; }
}
