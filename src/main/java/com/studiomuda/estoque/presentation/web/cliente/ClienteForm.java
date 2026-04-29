package com.studiomuda.estoque.presentation.web.cliente;

import com.studiomuda.estoque.application.cliente.dto.AtualizarClienteCommand;
import com.studiomuda.estoque.application.cliente.dto.CadastrarClienteCommand;
import com.studiomuda.estoque.domain.cliente.Cliente;
import com.studiomuda.estoque.domain.cliente.Endereco;

import java.time.LocalDate;

public class ClienteForm {
    private int id;
    private String nome;
    private String cpfCnpj;
    private String telefone;
    private String email;
    private String tipo;
    private String cep;
    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private boolean ativo = true;
    private LocalDate dataNascimento;

    public ClienteForm() {}

    public static ClienteForm desde(Cliente cliente) {
        ClienteForm f = new ClienteForm();
        f.id = cliente.id();
        f.nome = cliente.nome();
        f.cpfCnpj = cliente.cpfCnpj().digitos();
        f.telefone = cliente.telefone();
        f.email = cliente.email();
        f.tipo = cliente.tipo().name();
        Endereco e = cliente.endereco();
        if (e != null) {
            f.cep = e.cep();
            f.rua = e.rua();
            f.numero = e.numero();
            f.bairro = e.bairro();
            f.cidade = e.cidade();
            f.estado = e.estado();
        }
        f.ativo = cliente.ativo();
        f.dataNascimento = cliente.dataNascimento();
        return f;
    }

    public CadastrarClienteCommand toCadastrarCommand() {
        return new CadastrarClienteCommand(nome, cpfCnpj, tipo, telefone, email,
                cep, rua, numero, bairro, cidade, estado, ativo, dataNascimento);
    }

    public AtualizarClienteCommand toAtualizarCommand() {
        return new AtualizarClienteCommand(id, nome, cpfCnpj, tipo, telefone, email,
                cep, rua, numero, bairro, cidade, estado, ativo, dataNascimento);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
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
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
}
