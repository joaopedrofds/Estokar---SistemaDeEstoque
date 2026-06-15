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
import java.sql.Timestamp;

@Entity
@Table(name = "solicitacao_ajuste_estoque")
public class SolicitacaoAjusteEstoqueJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "produto_id")
    private Integer produtoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", insertable = false, updatable = false)
    private ProdutoJpaEntity produto;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "quantidade")
    private Integer quantidade;

    @Column(name = "justificativa")
    private String justificativa;

    @Column(name = "status")
    private String status;

    @Column(name = "risco")
    private String risco;

    @Column(name = "saldo_antes")
    private Integer saldoAntes;

    @Column(name = "saldo_depois")
    private Integer saldoDepois;

    @Column(name = "exige_aprovacao")
    private Boolean exigeAprovacao;

    @Column(name = "solicitante_id")
    private Integer solicitanteId;

    @Column(name = "solicitante_nome")
    private String solicitanteNome;

    @Column(name = "aprovador_id")
    private Integer aprovadorId;

    @Column(name = "aprovador_nome")
    private String aprovadorNome;

    @Column(name = "motivo_decisao")
    private String motivoDecisao;

    @Column(name = "data_solicitacao")
    private Timestamp dataSolicitacao;

    @Column(name = "data_decisao")
    private Timestamp dataDecisao;

    public Integer getId() { return id; }
    public Integer getProdutoId() { return produtoId; }
    public ProdutoJpaEntity getProduto() { return produto; }
    public String getTipo() { return tipo; }
    public Integer getQuantidade() { return quantidade; }
    public String getJustificativa() { return justificativa; }
    public String getStatus() { return status; }
    public String getRisco() { return risco; }
    public Integer getSaldoAntes() { return saldoAntes; }
    public Integer getSaldoDepois() { return saldoDepois; }
    public Boolean getExigeAprovacao() { return exigeAprovacao; }
    public Integer getSolicitanteId() { return solicitanteId; }
    public String getSolicitanteNome() { return solicitanteNome; }
    public Integer getAprovadorId() { return aprovadorId; }
    public String getAprovadorNome() { return aprovadorNome; }
    public String getMotivoDecisao() { return motivoDecisao; }
    public Timestamp getDataSolicitacao() { return dataSolicitacao; }
    public Timestamp getDataDecisao() { return dataDecisao; }

    public void setProdutoId(Integer produtoId) { this.produtoId = produtoId; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
    public void setStatus(String status) { this.status = status; }
    public void setRisco(String risco) { this.risco = risco; }
    public void setSaldoAntes(Integer saldoAntes) { this.saldoAntes = saldoAntes; }
    public void setSaldoDepois(Integer saldoDepois) { this.saldoDepois = saldoDepois; }
    public void setExigeAprovacao(Boolean exigeAprovacao) { this.exigeAprovacao = exigeAprovacao; }
    public void setSolicitanteId(Integer solicitanteId) { this.solicitanteId = solicitanteId; }
    public void setSolicitanteNome(String solicitanteNome) { this.solicitanteNome = solicitanteNome; }
    public void setAprovadorId(Integer aprovadorId) { this.aprovadorId = aprovadorId; }
    public void setAprovadorNome(String aprovadorNome) { this.aprovadorNome = aprovadorNome; }
    public void setMotivoDecisao(String motivoDecisao) { this.motivoDecisao = motivoDecisao; }
    public void setDataSolicitacao(Timestamp dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }
    public void setDataDecisao(Timestamp dataDecisao) { this.dataDecisao = dataDecisao; }
}
