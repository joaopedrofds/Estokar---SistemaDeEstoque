package com.studiomuda.estoque.jpa.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente")
public class ClienteJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;

    @Column(name = "cpf_cnpj")
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
    private Boolean ativo = true;

    @Column(name = "dataNascimento")
    private LocalDate dataNascimento;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    private List<FaturaJpaEntity> faturas = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    private List<AcordoPagamentoJpaEntity> acordos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    private List<HistoricoCobrancaJpaEntity> historicosCobranca = new ArrayList<>();

    public Integer getId() { return id; }
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
    public Boolean getAtivo() { return ativo; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public List<FaturaJpaEntity> getFaturas() { return faturas; }
    public List<AcordoPagamentoJpaEntity> getAcordos() { return acordos; }
    public List<HistoricoCobrancaJpaEntity> getHistoricosCobranca() { return historicosCobranca; }

    public void setId(Integer id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public void setEmail(String email) { this.email = email; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setCep(String cep) { this.cep = cep; }
    public void setRua(String rua) { this.rua = rua; }
    public void setNumero(String numero) { this.numero = numero; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
}
