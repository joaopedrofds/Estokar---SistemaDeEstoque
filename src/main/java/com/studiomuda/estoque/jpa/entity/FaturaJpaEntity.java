package com.studiomuda.estoque.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fatura")
public class FaturaJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id")
    private ClienteJpaEntity cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "acordo_pagamento_id")
    private AcordoPagamentoJpaEntity acordoPagamento;

    @Column(name = "pedido_id")
    private Integer pedidoId;

    @Column(name = "numero")
    private String numero;

    @Column(name = "data_emissao")
    private LocalDate dataEmissao = LocalDate.now();

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    private BigDecimal valor;
    private String status = "PENDENTE";

    public Integer getId() { return id; }
    public ClienteJpaEntity getCliente() { return cliente; }
    public AcordoPagamentoJpaEntity getAcordoPagamento() { return acordoPagamento; }
    public Integer getPedidoId() { return pedidoId; }
    public String getNumero() { return numero; }
    public LocalDate getDataEmissao() { return dataEmissao; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public LocalDate getDataPagamento() { return dataPagamento; }
    public BigDecimal getValor() { return valor; }
    public String getStatus() { return status; }

    public void setId(Integer id) { this.id = id; }
    public void setCliente(ClienteJpaEntity cliente) { this.cliente = cliente; }
    public void setAcordoPagamento(AcordoPagamentoJpaEntity acordoPagamento) { this.acordoPagamento = acordoPagamento; }
    public void setPedidoId(Integer pedidoId) { this.pedidoId = pedidoId; }
    public void setNumero(String numero) { this.numero = numero; }
    public void setDataEmissao(LocalDate dataEmissao) { this.dataEmissao = dataEmissao; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public void setStatus(String status) { this.status = status; }
}
