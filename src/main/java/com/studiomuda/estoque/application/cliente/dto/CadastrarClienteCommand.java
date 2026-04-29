package com.studiomuda.estoque.application.cliente.dto;

import java.time.LocalDate;

public class CadastrarClienteCommand {
    private final String nome;
    private final String cpfCnpj;
    private final String tipo;
    private final String telefone;
    private final String email;
    private final String cep;
    private final String rua;
    private final String numero;
    private final String bairro;
    private final String cidade;
    private final String estado;
    private final boolean ativo;
    private final LocalDate dataNascimento;

    public CadastrarClienteCommand(String nome, String cpfCnpj, String tipo, String telefone, String email,
                                   String cep, String rua, String numero, String bairro, String cidade,
                                   String estado, boolean ativo, LocalDate dataNascimento) {
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.tipo = tipo;
        this.telefone = telefone;
        this.email = email;
        this.cep = cep;
        this.rua = rua;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.ativo = ativo;
        this.dataNascimento = dataNascimento;
    }

    public String nome() { return nome; }
    public String cpfCnpj() { return cpfCnpj; }
    public String tipo() { return tipo; }
    public String telefone() { return telefone; }
    public String email() { return email; }
    public String cep() { return cep; }
    public String rua() { return rua; }
    public String numero() { return numero; }
    public String bairro() { return bairro; }
    public String cidade() { return cidade; }
    public String estado() { return estado; }
    public boolean ativo() { return ativo; }
    public LocalDate dataNascimento() { return dataNascimento; }
}
