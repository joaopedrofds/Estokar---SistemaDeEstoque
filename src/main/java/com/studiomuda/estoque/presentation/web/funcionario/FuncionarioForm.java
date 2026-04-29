package com.studiomuda.estoque.presentation.web.funcionario;

import com.studiomuda.estoque.application.funcionario.dto.AtualizarFuncionarioCommand;
import com.studiomuda.estoque.application.funcionario.dto.CadastrarFuncionarioCommand;
import com.studiomuda.estoque.domain.funcionario.Endereco;
import com.studiomuda.estoque.domain.funcionario.Funcionario;

import java.sql.Date;
import java.time.LocalDate;

public class FuncionarioForm {
    private int id;
    private String nome;
    private String cpf;
    private String cargo;
    private Date data_nasc;
    private String telefone;
    private String cep;
    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private boolean ativo = true;

    public FuncionarioForm() {}

    public static FuncionarioForm desde(Funcionario f) {
        FuncionarioForm form = new FuncionarioForm();
        form.id = f.id();
        form.nome = f.nome();
        form.cpf = f.cpf().digitos();
        form.cargo = f.cargo().rotulo();
        form.data_nasc = f.dataNascimento() != null ? Date.valueOf(f.dataNascimento()) : null;
        form.telefone = f.telefone();
        Endereco e = f.endereco();
        if (e != null) {
            form.cep = e.cep();
            form.rua = e.rua();
            form.numero = e.numero();
            form.bairro = e.bairro();
            form.cidade = e.cidade();
            form.estado = e.estado();
        }
        form.ativo = f.ativo();
        return form;
    }

    public CadastrarFuncionarioCommand toCadastrarCommand(LocalDate dataNascimentoOverride) {
        LocalDate data = dataNascimentoOverride != null ? dataNascimentoOverride
                : (data_nasc != null ? data_nasc.toLocalDate() : null);
        return new CadastrarFuncionarioCommand(nome, cpf, cargo, data, telefone,
                cep, rua, numero, bairro, cidade, estado, ativo);
    }

    public AtualizarFuncionarioCommand toAtualizarCommand() {
        return new AtualizarFuncionarioCommand(id, nome, cargo, telefone,
                cep, rua, numero, bairro, cidade, estado, ativo);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public Date getData_nasc() { return data_nasc; }
    public void setData_nasc(Date data_nasc) { this.data_nasc = data_nasc; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
