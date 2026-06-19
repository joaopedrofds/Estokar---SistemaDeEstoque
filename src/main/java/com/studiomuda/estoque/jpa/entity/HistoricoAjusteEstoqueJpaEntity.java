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
@Table(name = "historico_ajuste_estoque")
public class HistoricoAjusteEstoqueJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "solicitacao_id")
    private Integer solicitacaoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitacao_id", insertable = false, updatable = false)
    private SolicitacaoAjusteEstoqueJpaEntity solicitacao;

    @Column(name = "status_anterior")
    private String statusAnterior;

    @Column(name = "status_novo")
    private String statusNovo;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "usuario_nome")
    private String usuarioNome;

    @Column(name = "data_evento")
    private Timestamp dataEvento;

    public Integer getId() { return id; }
    public Integer getSolicitacaoId() { return solicitacaoId; }
    public SolicitacaoAjusteEstoqueJpaEntity getSolicitacao() { return solicitacao; }
    public String getStatusAnterior() { return statusAnterior; }
    public String getStatusNovo() { return statusNovo; }
    public String getDescricao() { return descricao; }
    public Integer getUsuarioId() { return usuarioId; }
    public String getUsuarioNome() { return usuarioNome; }
    public Timestamp getDataEvento() { return dataEvento; }

    public void setSolicitacaoId(Integer solicitacaoId) { this.solicitacaoId = solicitacaoId; }
    public void setStatusAnterior(String statusAnterior) { this.statusAnterior = statusAnterior; }
    public void setStatusNovo(String statusNovo) { this.statusNovo = statusNovo; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public void setUsuarioNome(String usuarioNome) { this.usuarioNome = usuarioNome; }
    public void setDataEvento(Timestamp dataEvento) { this.dataEvento = dataEvento; }
}
