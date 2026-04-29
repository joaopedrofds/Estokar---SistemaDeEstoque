package com.studiomuda.estoque.application.funcionario.dto;

import java.time.LocalDate;

public class CadastrarFuncionarioCommand {
    private final String nome;
    private final String cpf;
    private final String cargo;
    private final LocalDate dataNascimento;
    private final String telefone;
    private final String cep;
    private final String rua;
    private final String numero;
    private final String bairro;
    private final String cidade;
    private final String estado;
    private final boolean ativo;

    public CadastrarFuncionarioCommand(String nome, String cpf, String cargo, LocalDate dataNascimento,
                                        String telefone, String cep, String rua, String numero,
                                        String bairro, String cidade, String estado, boolean ativo) {
        this.nome = nome;
        this.cpf = cpf;
        this.cargo = cargo;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.cep = cep;
        this.rua = rua;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.ativo = ativo;
    }

    public String nome() { return nome; }
    public String cpf() { return cpf; }
    public String cargo() { return cargo; }
    public LocalDate dataNascimento() { return dataNascimento; }
    public String telefone() { return telefone; }
    public String cep() { return cep; }
    public String rua() { return rua; }
    public String numero() { return numero; }
    public String bairro() { return bairro; }
    public String cidade() { return cidade; }
    public String estado() { return estado; }
    public boolean ativo() { return ativo; }
}
