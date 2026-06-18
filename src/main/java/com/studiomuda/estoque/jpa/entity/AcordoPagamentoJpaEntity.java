package com.studiomuda.estoque.jpa.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "acordo_pagamento")
public class AcordoPagamentoJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id")
    private ClienteJpaEntity cliente;

    @Column(name = "data_acordo")
    private LocalDate dataAcordo = LocalDate.now();

    @Column(name = "data_inicio")
    private LocalDate dataInicio = LocalDate.now();

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "valor_total")
    private BigDecimal valorTotal;

    private String status = "ATIVO";

    @OneToMany(mappedBy = "acordoPagamento", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    private List<FaturaJpaEntity> parcelas = new ArrayList<>();

    public Integer getId() { return id; }
    public ClienteJpaEntity getCliente() { return cliente; }
    public LocalDate getDataAcordo() { return dataAcordo; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public String getStatus() { return status; }
    public List<FaturaJpaEntity> getParcelas() { return parcelas; }

    public void setId(Integer id) { this.id = id; }
    public void setCliente(ClienteJpaEntity cliente) { this.cliente = cliente; }
    public void setDataAcordo(LocalDate dataAcordo) { this.dataAcordo = dataAcordo; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public void setStatus(String status) { this.status = status; }
}
