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
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordem_compra")
public class OrdemCompraJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo_ordem")
    private String codigoOrdem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id")
    private FornecedorJpaEntity fornecedor;

    @Column(name = "status")
    private String status;

    @Column(name = "valor_total")
    private BigDecimal valorTotal;

    @Column(name = "data_criacao")
    private Date dataCriacao;

    @Column(name = "data_aprovacao")
    private Date dataAprovacao;

    @OneToMany(mappedBy = "ordemCompra", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemOrdemCompraJpaEntity> itens = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public String getCodigoOrdem() {
        return codigoOrdem;
    }

    public FornecedorJpaEntity getFornecedor() {
        return fornecedor;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public Date getDataAprovacao() {
        return dataAprovacao;
    }

    public List<ItemOrdemCompraJpaEntity> getItens() {
        return itens;
    }

    public void setCodigoOrdem(String codigoOrdem) {
        this.codigoOrdem = codigoOrdem;
    }

    public void setFornecedor(FornecedorJpaEntity fornecedor) {
        this.fornecedor = fornecedor;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public void setDataAprovacao(Date dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }

    public void setItens(List<ItemOrdemCompraJpaEntity> itens) {
        this.itens = itens;
    }
}
