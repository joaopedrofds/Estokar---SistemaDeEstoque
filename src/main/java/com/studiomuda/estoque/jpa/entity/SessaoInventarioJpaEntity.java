package com.studiomuda.estoque.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "sessao_inventario")
public class SessaoInventarioJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "setor")
    private String setor;

    @Column(name = "data_abertura")
    private Date dataAbertura;

    @Column(name = "gerente_id")
    private Integer gerenteId;

    @Column(name = "gerente_nome")
    private String gerenteNome;

    @Column(name = "status")
    private String status;

    @Column(name = "bloqueia_saidas")
    private Boolean bloqueiaSaidas;

    @Column(name = "tolerancia_quantidade")
    private Integer toleranciaQuantidade;

    @Column(name = "aprovador_id")
    private Integer aprovadorId;

    @Column(name = "aprovador_nome")
    private String aprovadorNome;

    @Column(name = "data_aprovacao")
    private Timestamp dataAprovacao;

    @Column(name = "data_fechamento")
    private Timestamp dataFechamento;

    @Column(name = "observacao")
    private String observacao;

    public Integer getId() { return id; }
    public String getSetor() { return setor; }
    public Date getDataAbertura() { return dataAbertura; }
    public Integer getGerenteId() { return gerenteId; }
    public String getGerenteNome() { return gerenteNome; }
    public String getStatus() { return status; }
    public Boolean getBloqueiaSaidas() { return bloqueiaSaidas; }
    public Integer getToleranciaQuantidade() { return toleranciaQuantidade; }
    public Integer getAprovadorId() { return aprovadorId; }
    public String getAprovadorNome() { return aprovadorNome; }
    public Timestamp getDataAprovacao() { return dataAprovacao; }
    public Timestamp getDataFechamento() { return dataFechamento; }
    public String getObservacao() { return observacao; }

    public void setSetor(String setor) { this.setor = setor; }
    public void setDataAbertura(Date dataAbertura) { this.dataAbertura = dataAbertura; }
    public void setGerenteId(Integer gerenteId) { this.gerenteId = gerenteId; }
    public void setGerenteNome(String gerenteNome) { this.gerenteNome = gerenteNome; }
    public void setStatus(String status) { this.status = status; }
    public void setBloqueiaSaidas(Boolean bloqueiaSaidas) { this.bloqueiaSaidas = bloqueiaSaidas; }
    public void setToleranciaQuantidade(Integer toleranciaQuantidade) { this.toleranciaQuantidade = toleranciaQuantidade; }
    public void setAprovadorId(Integer aprovadorId) { this.aprovadorId = aprovadorId; }
    public void setAprovadorNome(String aprovadorNome) { this.aprovadorNome = aprovadorNome; }
    public void setDataAprovacao(Timestamp dataAprovacao) { this.dataAprovacao = dataAprovacao; }
    public void setDataFechamento(Timestamp dataFechamento) { this.dataFechamento = dataFechamento; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
