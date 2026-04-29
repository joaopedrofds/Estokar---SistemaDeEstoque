package com.studiomuda.estoque.domain.cliente;

import java.time.LocalDate;

public class Cliente {
    private static final java.util.regex.Pattern EMAIL_PATTERN =
            java.util.regex.Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final int id;
    private String nome;
    private CpfCnpj cpfCnpj;
    private String telefone;
    private String email;
    private Endereco endereco;
    private boolean ativo;
    private LocalDate dataNascimento;

    public Cliente(int id, String nome, CpfCnpj cpfCnpj, String telefone, String email,
                   Endereco endereco, boolean ativo, LocalDate dataNascimento) {
        this.id = id;
        atualizarNome(nome);
        this.cpfCnpj = exigirCpfCnpj(cpfCnpj);
        this.telefone = telefone;
        atualizarEmail(email);
        this.endereco = endereco;
        this.ativo = ativo;
        this.dataNascimento = dataNascimento;
    }

    public static Cliente novo(String nome, CpfCnpj cpfCnpj, String telefone, String email,
                                Endereco endereco, LocalDate dataNascimento) {
        return new Cliente(0, nome, cpfCnpj, telefone, email, endereco, true, dataNascimento);
    }

    public void atualizarDados(String nome, String telefone, String email, Endereco endereco,
                                LocalDate dataNascimento) {
        atualizarNome(nome);
        this.telefone = telefone;
        atualizarEmail(email);
        this.endereco = endereco;
        this.dataNascimento = dataNascimento;
    }

    public void desativar() {
        this.ativo = false;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void bloquearPorInadimplencia() {
        this.ativo = false;
    }

    private void atualizarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        this.nome = nome.trim();
    }

    private void atualizarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("O e-mail é obrigatório.");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("E-mail em formato inválido.");
        }
        this.email = email.trim();
    }

    private static CpfCnpj exigirCpfCnpj(CpfCnpj cpfCnpj) {
        if (cpfCnpj == null) {
            throw new IllegalArgumentException("O CPF/CNPJ é obrigatório.");
        }
        return cpfCnpj;
    }

    public int id() { return id; }
    public String nome() { return nome; }
    public CpfCnpj cpfCnpj() { return cpfCnpj; }
    public String telefone() { return telefone; }
    public String email() { return email; }
    public Endereco endereco() { return endereco; }
    public boolean ativo() { return ativo; }
    public LocalDate dataNascimento() { return dataNascimento; }
    public TipoPessoa tipo() { return cpfCnpj.tipo(); }
}
