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
import java.time.LocalDateTime;

@Entity
@Table(name = "acao_retencao")
public class AcaoRetencaoJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteJpaEntity cliente;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "faixa_fidelidade_id", nullable = false)
    private FaixaFidelidadeJpaEntity faixa;

    @Column(name = "codigo_cupom", nullable = false, unique = true)
    private String codigoCupom;

    @Column(name = "percentual_desconto", precision = 5, scale = 2, nullable = false)
    private BigDecimal percentualDesconto;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_validade", nullable = false)
    private LocalDate dataValidade;

    private Boolean ativa = true;

    public Integer getId() { return id; }
    public ClienteJpaEntity getCliente() { return cliente; }
    public FaixaFidelidadeJpaEntity getFaixa() { return faixa; }
    public String getCodigoCupom() { return codigoCupom; }
    public BigDecimal getPercentualDesconto() { return percentualDesconto; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDate getDataValidade() { return dataValidade; }
    public Boolean getAtiva() { return ativa; }

    public void setId(Integer id) { this.id = id; }
    public void setCliente(ClienteJpaEntity cliente) { this.cliente = cliente; }
    public void setFaixa(FaixaFidelidadeJpaEntity faixa) { this.faixa = faixa; }
    public void setCodigoCupom(String codigoCupom) { this.codigoCupom = codigoCupom; }
    public void setPercentualDesconto(BigDecimal percentualDesconto) { this.percentualDesconto = percentualDesconto; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }
    public void setAtiva(Boolean ativa) { this.ativa = ativa; }
}
