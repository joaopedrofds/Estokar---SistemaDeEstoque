package com.studiomuda.estoque.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade — registro imutável de alteração de preço.
 * Nível Tático DDD: Entity
 * Persistência: ORM via Spring Data JPA
 */
@Entity
@Table(name = "historico_preco")
public class HistoricoPreco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "produto_id")
    private int produtoId;

    @Column(name = "preco_anterior")
    private double precoAnterior;

    @Column(name = "preco_novo")
    private double precoNovo;

    @Column(name = "percentual_variacao")
    private Double percentualVariacao;

    @Column(name = "usuario_responsavel")
    private String usuarioResponsavel;

    @Column(name = "data_alteracao")
    private LocalDateTime dataAlteracao;

    @Transient
    private String produtoNome;

    public HistoricoPreco() {}

    public HistoricoPreco(int produtoId, double precoAnterior, double precoNovo,
                          Double percentualVariacao, String usuarioResponsavel) {
        this.produtoId = produtoId;
        this.precoAnterior = precoAnterior;
        this.precoNovo = precoNovo;
        this.percentualVariacao = percentualVariacao;
        this.usuarioResponsavel = usuarioResponsavel;
    }

    public int getId()                            { return id; }
    public void setId(int id)                     { this.id = id; }
    public int getProdutoId()                     { return produtoId; }
    public void setProdutoId(int produtoId)       { this.produtoId = produtoId; }
    public String getProdutoNome()                { return produtoNome; }
    public void setProdutoNome(String nome)       { this.produtoNome = nome; }
    public double getPrecoAnterior()              { return precoAnterior; }
    public void setPrecoAnterior(double v)        { this.precoAnterior = v; }
    public double getPrecoNovo()                  { return precoNovo; }
    public void setPrecoNovo(double v)            { this.precoNovo = v; }
    public Double getPercentualVariacao()         { return percentualVariacao; }
    public void setPercentualVariacao(Double v)   { this.percentualVariacao = v; }
    public String getUsuarioResponsavel()         { return usuarioResponsavel; }
    public void setUsuarioResponsavel(String u)   { this.usuarioResponsavel = u; }
    public LocalDateTime getDataAlteracao()       { return dataAlteracao; }
    public void setDataAlteracao(LocalDateTime d) { this.dataAlteracao = d; }
}