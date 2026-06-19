package com.studiomuda.estoque.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade JPA — registro imutável de cada simulação de preço.
 * Nível Tático DDD: Entity (auditoria)
 * Persistência: ORM via Spring Data JPA
 */
@Entity
@Table(name = "simulacao_preco")
public class SimulacaoPreco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "produto_id", nullable = false)
    private int produtoId;

    @Transient
    private String produtoNome;

    @Column(name = "preco_atual")
    private double precoAtual;

    @Column(name = "custo_produto")
    private double custoProduto;

    @Column(name = "tipo_estrategia")
    private String tipoEstrategia;

    @Column(name = "preco_sugerido")
    private double precoSugerido;

    @Column(name = "margem_calculada")
    private double margemCalculada;

    @Column(name = "status")
    private String status;

    @Column(name = "justificativa", columnDefinition = "TEXT")
    private String justificativa;

    @Column(name = "usuario_responsavel")
    private String usuarioResponsavel;

    @Column(name = "aplicado")
    private boolean aplicado = false;

    @Column(name = "data_simulacao")
    private LocalDateTime dataSimulacao;

    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }
    public int getProdutoId()                       { return produtoId; }
    public void setProdutoId(int p)                 { this.produtoId = p; }
    public String getProdutoNome()                  { return produtoNome; }
    public void setProdutoNome(String n)            { this.produtoNome = n; }
    public double getPrecoAtual()                   { return precoAtual; }
    public void setPrecoAtual(double v)             { this.precoAtual = v; }
    public double getCustoProduto()                 { return custoProduto; }
    public void setCustoProduto(double v)           { this.custoProduto = v; }
    public String getTipoEstrategia()               { return tipoEstrategia; }
    public void setTipoEstrategia(String t)         { this.tipoEstrategia = t; }
    public double getPrecoSugerido()                { return precoSugerido; }
    public void setPrecoSugerido(double v)          { this.precoSugerido = v; }
    public double getMargemCalculada()              { return margemCalculada; }
    public void setMargemCalculada(double v)        { this.margemCalculada = v; }
    public String getStatus()                       { return status; }
    public void setStatus(String s)                 { this.status = s; }
    public String getJustificativa()                { return justificativa; }
    public void setJustificativa(String j)          { this.justificativa = j; }
    public String getUsuarioResponsavel()           { return usuarioResponsavel; }
    public void setUsuarioResponsavel(String u)     { this.usuarioResponsavel = u; }
    public boolean isAplicado()                     { return aplicado; }
    public void setAplicado(boolean a)              { this.aplicado = a; }
    public LocalDateTime getDataSimulacao()         { return dataSimulacao; }
    public void setDataSimulacao(LocalDateTime d)   { this.dataSimulacao = d; }

    public boolean isAprovado()  { return "APROVADO".equals(status) || "APLICADO".equals(status); }
    public boolean isBloqueado() { return "BLOQUEADO".equals(status); }
}