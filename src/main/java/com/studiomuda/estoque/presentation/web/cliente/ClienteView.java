package com.studiomuda.estoque.presentation.web.cliente;

import com.studiomuda.estoque.application.cliente.dto.ClienteComFrequencia;
import com.studiomuda.estoque.domain.cliente.Cliente;
import com.studiomuda.estoque.domain.cliente.Endereco;

import java.time.LocalDate;

public class ClienteView {
    private final int id;
    private final String nome;
    private final String cpfCnpj;
    private final String telefone;
    private final String email;
    private final String tipo;
    private final String cep;
    private final String rua;
    private final String numero;
    private final String bairro;
    private final String cidade;
    private final String estado;
    private final boolean ativo;
    private final LocalDate dataNascimento;
    private final Integer totalPedidos;
    private final Double mediaDiasEntreCompras;
    private final String classificacaoFrequencia;

    public ClienteView(Cliente c, Integer totalPedidos, Double mediaDias, String classificacao) {
        this.id = c.id();
        this.nome = c.nome();
        this.cpfCnpj = c.cpfCnpj().digitos();
        this.telefone = c.telefone();
        this.email = c.email();
        this.tipo = c.tipo().name();
        Endereco e = c.endereco();
        this.cep = e != null ? e.cep() : null;
        this.rua = e != null ? e.rua() : null;
        this.numero = e != null ? e.numero() : null;
        this.bairro = e != null ? e.bairro() : null;
        this.cidade = e != null ? e.cidade() : null;
        this.estado = e != null ? e.estado() : null;
        this.ativo = c.ativo();
        this.dataNascimento = c.dataNascimento();
        this.totalPedidos = totalPedidos;
        this.mediaDiasEntreCompras = mediaDias;
        this.classificacaoFrequencia = classificacao;
    }

    public static ClienteView desde(ClienteComFrequencia cf) {
        return new ClienteView(
                cf.cliente(),
                cf.analise().totalPedidos(),
                cf.analise().mediaDiasEntreCompras(),
                cf.analise().classificacao().descricao());
    }

    public static ClienteView semFrequencia(Cliente c) {
        return new ClienteView(c, 0, null, "Sem Compras");
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCpfCnpj() { return cpfCnpj; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public String getTipo() { return tipo; }
    public String getCep() { return cep; }
    public String getRua() { return rua; }
    public String getNumero() { return numero; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public boolean isAtivo() { return ativo; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public Integer getTotalPedidos() { return totalPedidos; }
    public Double getMediaDiasEntreCompras() { return mediaDiasEntreCompras; }
    public String getClassificacaoFrequencia() { return classificacaoFrequencia; }
}
