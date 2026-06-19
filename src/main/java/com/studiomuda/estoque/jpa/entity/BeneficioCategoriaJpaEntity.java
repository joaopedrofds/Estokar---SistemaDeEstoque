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

@Entity
@Table(name = "beneficio_categoria")
public class BeneficioCategoriaJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "faixa_fidelidade_id", nullable = false)
    private FaixaFidelidadeJpaEntity faixa;

    @Column(nullable = false)
    private String tipo = "PERCENTUAL_DESCONTO";

    @Column(name = "percentual_desconto", precision = 5, scale = 2)
    private BigDecimal percentualDesconto;

    private String descricao;
    private Boolean ativo = true;

    public Integer getId() { return id; }
    public FaixaFidelidadeJpaEntity getFaixa() { return faixa; }
    public String getTipo() { return tipo; }
    public BigDecimal getPercentualDesconto() { return percentualDesconto; }
    public String getDescricao() { return descricao; }
    public Boolean getAtivo() { return ativo; }

    public void setId(Integer id) { this.id = id; }
    public void setFaixa(FaixaFidelidadeJpaEntity faixa) { this.faixa = faixa; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setPercentualDesconto(BigDecimal percentualDesconto) { this.percentualDesconto = percentualDesconto; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
