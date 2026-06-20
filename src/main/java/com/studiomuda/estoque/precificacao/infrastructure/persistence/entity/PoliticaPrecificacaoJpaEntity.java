package com.studiomuda.estoque.precificacao.infrastructure.persistence.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "precificacao_politica",
       indexes = {
           @Index(name = "idx_precificacao_politica_produto", columnList = "produto_id, ativa"),
           @Index(name = "idx_precificacao_politica_atualizado", columnList = "atualizado_em")
       })
public class PoliticaPrecificacaoJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "produto_id", nullable = false)
    private int produtoId;

    @Column(name = "margem_lucro_desejada", precision = 8, scale = 2, nullable = false)
    private BigDecimal margemLucroDesejada;

    @Column(name = "aliquota_impostos", precision = 8, scale = 2, nullable = false)
    private BigDecimal aliquotaImpostos;

    @Column(name = "percentual_despesas_operacionais", precision = 8, scale = 2, nullable = false)
    private BigDecimal percentualDespesasOperacionais;

    @Column(name = "desconto_maximo_permitido", precision = 8, scale = 2, nullable = false)
    private BigDecimal descontoMaximoPermitido;

    @Column(name = "ativa", nullable = false)
    private boolean ativa = true;

    @Column(name = "observacao", length = 300)
    private String observacao;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }
    public BigDecimal getMargemLucroDesejada() { return margemLucroDesejada; }
    public void setMargemLucroDesejada(BigDecimal margemLucroDesejada) { this.margemLucroDesejada = margemLucroDesejada; }
    public BigDecimal getAliquotaImpostos() { return aliquotaImpostos; }
    public void setAliquotaImpostos(BigDecimal aliquotaImpostos) { this.aliquotaImpostos = aliquotaImpostos; }
    public BigDecimal getPercentualDespesasOperacionais() { return percentualDespesasOperacionais; }
    public void setPercentualDespesasOperacionais(BigDecimal percentualDespesasOperacionais) { this.percentualDespesasOperacionais = percentualDespesasOperacionais; }
    public BigDecimal getDescontoMaximoPermitido() { return descontoMaximoPermitido; }
    public void setDescontoMaximoPermitido(BigDecimal descontoMaximoPermitido) { this.descontoMaximoPermitido = descontoMaximoPermitido; }
    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
