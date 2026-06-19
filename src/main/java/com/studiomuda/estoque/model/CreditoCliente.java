package com.studiomuda.estoque.model;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDateTime;

/**
 * Entidade de crédito gerado para o cliente após devolução aprovada.
 * Nível Tático DDD: Entity
 */
@Entity
@Table(name = "credito_cliente")
public class CreditoCliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "cliente_id", nullable = false)
    private int clienteId;

    @Transient
    private String clienteNome;

    @Column(name = "devolucao_id", nullable = false)
    private int devolucaoId;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private double valor;

    @Column(name = "saldo", nullable = false, precision = 10, scale = 2)
    private double saldo;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "validade", nullable = false)
    private Date validade;

    @Column(name = "data_geracao")
    private LocalDateTime dataGeracao;

    @Column(name = "data_utilizacao")
    private LocalDateTime dataUtilizacao;

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }
    public int getClienteId()                   { return clienteId; }
    public void setClienteId(int c)             { this.clienteId = c; }
    public String getClienteNome()              { return clienteNome; }
    public void setClienteNome(String n)        { this.clienteNome = n; }
    public int getDevolucaoId()                 { return devolucaoId; }
    public void setDevolucaoId(int d)           { this.devolucaoId = d; }
    public double getValor()                    { return valor; }
    public void setValor(double v)              { this.valor = v; }
    public double getSaldo()                    { return saldo; }
    public void setSaldo(double s)              { this.saldo = s; }
    public String getStatus()                   { return status; }
    public void setStatus(String s)             { this.status = s; }
    public Date getValidade()                   { return validade; }
    public void setValidade(Date v)             { this.validade = v; }
    public LocalDateTime getDataGeracao()       { return dataGeracao; }
    public void setDataGeracao(LocalDateTime d) { this.dataGeracao = d; }
    public LocalDateTime getDataUtilizacao()    { return dataUtilizacao; }
    public void setDataUtilizacao(LocalDateTime d) { this.dataUtilizacao = d; }
}