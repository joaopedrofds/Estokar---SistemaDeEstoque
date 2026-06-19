package com.studiomuda.estoque.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade JPA — regra de precificação vinculada a um produto.
 * Nível Tático DDD: Entity
 * Persistência: ORM via Spring Data JPA
 */
@Entity
@Table(name = "regra_precificacao")
public class RegraPrecificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "produto_id", nullable = false)
    private int produtoId;

    @Transient
    private String produtoNome;

    @Column(name = "tipo_estrategia", nullable = false)
    private String tipoEstrategia;

    @Column(name = "valor_parametro", nullable = false)
    private double valorParametro;

    @Column(name = "ativo")
    private boolean ativo = true;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }
    public int getProdutoId()                   { return produtoId; }
    public void setProdutoId(int p)             { this.produtoId = p; }
    public String getProdutoNome()              { return produtoNome; }
    public void setProdutoNome(String n)        { this.produtoNome = n; }
    public String getTipoEstrategia()           { return tipoEstrategia; }
    public void setTipoEstrategia(String t)     { this.tipoEstrategia = t; }
    public double getValorParametro()           { return valorParametro; }
    public void setValorParametro(double v)     { this.valorParametro = v; }
    public boolean isAtivo()                    { return ativo; }
    public void setAtivo(boolean a)             { this.ativo = a; }
    public LocalDateTime getDataCriacao()       { return dataCriacao; }
    public void setDataCriacao(LocalDateTime d) { this.dataCriacao = d; }

    public String getDescricaoEstrategia() {
        switch (tipoEstrategia) {
            case "MARGEM_FIXA":      return "Margem Fixa (" + valorParametro + "%)";
            case "DESCONTO_VOLUME":  return "Desconto por Volume (" + valorParametro + "%)";
            case "SAZONALIDADE":     return "Sazonalidade (±" + valorParametro + "%)";
            default:                 return tipoEstrategia;
        }
    }
}