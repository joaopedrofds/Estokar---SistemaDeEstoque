package com.studiomuda.estoque.domain.funcionario;

import java.time.LocalDate;

public class Funcionario {
    private final int id;
    private String nome;
    private final Cpf cpf;
    private Cargo cargo;
    private LocalDate dataNascimento;
    private String telefone;
    private Endereco endereco;
    private boolean ativo;

    public Funcionario(int id, String nome, Cpf cpf, Cargo cargo, LocalDate dataNascimento,
                       String telefone, Endereco endereco, boolean ativo) {
        this.id = id;
        atualizarNome(nome);
        this.cpf = exigir(cpf, "CPF");
        this.cargo = exigir(cargo, "Cargo");
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.endereco = endereco;
        this.ativo = ativo;
    }

    public static Funcionario novo(String nome, Cpf cpf, Cargo cargo, LocalDate dataNascimento,
                                    String telefone, Endereco endereco) {
        return new Funcionario(0, nome, cpf, cargo, dataNascimento, telefone, endereco, true);
    }

    public void atualizarDados(String nome, Cargo cargo, String telefone, Endereco endereco) {
        atualizarNome(nome);
        this.cargo = exigir(cargo, "Cargo");
        this.telefone = telefone;
        this.endereco = endereco;
    }

    public void desativar() { this.ativo = false; }
    public void ativar() { this.ativo = true; }

    private void atualizarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        this.nome = nome.trim();
    }

    private static <T> T exigir(T valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException("O " + campo + " é obrigatório.");
        }
        return valor;
    }

    public int id() { return id; }
    public String nome() { return nome; }
    public Cpf cpf() { return cpf; }
    public Cargo cargo() { return cargo; }
    public LocalDate dataNascimento() { return dataNascimento; }
    public String telefone() { return telefone; }
    public Endereco endereco() { return endereco; }
    public boolean ativo() { return ativo; }
}
