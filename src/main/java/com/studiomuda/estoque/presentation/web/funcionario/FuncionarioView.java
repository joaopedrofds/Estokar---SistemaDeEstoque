package com.studiomuda.estoque.presentation.web.funcionario;

import com.studiomuda.estoque.domain.funcionario.Endereco;
import com.studiomuda.estoque.domain.funcionario.Funcionario;

import java.sql.Date;

public class FuncionarioView {
    private final int id;
    private final String nome;
    private final String cpf;
    private final String cargo;
    private final Date data_nasc;
    private final String telefone;
    private final String cep;
    private final String rua;
    private final String numero;
    private final String bairro;
    private final String cidade;
    private final String estado;
    private final boolean ativo;

    public FuncionarioView(Funcionario f) {
        this.id = f.id();
        this.nome = f.nome();
        this.cpf = f.cpf().digitos();
        this.cargo = f.cargo().rotulo();
        this.data_nasc = f.dataNascimento() != null ? Date.valueOf(f.dataNascimento()) : null;
        this.telefone = f.telefone();
        Endereco e = f.endereco();
        this.cep = e != null ? e.cep() : null;
        this.rua = e != null ? e.rua() : null;
        this.numero = e != null ? e.numero() : null;
        this.bairro = e != null ? e.bairro() : null;
        this.cidade = e != null ? e.cidade() : null;
        this.estado = e != null ? e.estado() : null;
        this.ativo = f.ativo();
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getCargo() { return cargo; }
    public Date getData_nasc() { return data_nasc; }
    public String getTelefone() { return telefone; }
    public String getCep() { return cep; }
    public String getRua() { return rua; }
    public String getNumero() { return numero; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public boolean isAtivo() { return ativo; }
}
