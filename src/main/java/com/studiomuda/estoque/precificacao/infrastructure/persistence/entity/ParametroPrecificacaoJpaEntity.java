package com.studiomuda.estoque.precificacao.infrastructure.persistence.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "precificacao_parametro")
public class ParametroPrecificacaoJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "margem_minima_global", precision = 8, scale = 2, nullable = false)
    private BigDecimal margemMinimaGlobal = BigDecimal.valueOf(30.00);

    @Column(name = "desconto_maximo_global", precision = 8, scale = 2, nullable = false)
    private BigDecimal descontoMaximoGlobal = BigDecimal.valueOf(20.00);

    @Column(name = "margem_padrao_lucro", precision = 8, scale = 2, nullable = false)
    private BigDecimal margemPadraoLucro = BigDecimal.valueOf(45.00);

    @Column(name = "imposto_padrao_percentual", precision = 8, scale = 2, nullable = false)
    private BigDecimal impostoPadraoPercentual = BigDecimal.valueOf(8.50);

    @Column(name = "despesa_operacional_padrao_percentual", precision = 8, scale = 2, nullable = false)
    private BigDecimal despesaOperacionalPadraoPercentual = BigDecimal.valueOf(12.00);

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getMargemMinimaGlobal() { return margemMinimaGlobal; }
    public void setMargemMinimaGlobal(BigDecimal margemMinimaGlobal) { this.margemMinimaGlobal = margemMinimaGlobal; }
    public BigDecimal getDescontoMaximoGlobal() { return descontoMaximoGlobal; }
    public void setDescontoMaximoGlobal(BigDecimal descontoMaximoGlobal) { this.descontoMaximoGlobal = descontoMaximoGlobal; }
    public BigDecimal getMargemPadraoLucro() { return margemPadraoLucro; }
    public void setMargemPadraoLucro(BigDecimal margemPadraoLucro) { this.margemPadraoLucro = margemPadraoLucro; }
    public BigDecimal getImpostoPadraoPercentual() { return impostoPadraoPercentual; }
    public void setImpostoPadraoPercentual(BigDecimal impostoPadraoPercentual) { this.impostoPadraoPercentual = impostoPadraoPercentual; }
    public BigDecimal getDespesaOperacionalPadraoPercentual() { return despesaOperacionalPadraoPercentual; }
    public void setDespesaOperacionalPadraoPercentual(BigDecimal despesaOperacionalPadraoPercentual) { this.despesaOperacionalPadraoPercentual = despesaOperacionalPadraoPercentual; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
