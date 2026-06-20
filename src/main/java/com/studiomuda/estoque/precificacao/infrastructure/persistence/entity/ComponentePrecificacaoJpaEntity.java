package com.studiomuda.estoque.precificacao.infrastructure.persistence.entity;

import com.studiomuda.estoque.precificacao.domain.model.TipoComponenteCusto;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "precificacao_componente",
       indexes = @Index(name = "idx_precificacao_componente_simulacao", columnList = "simulacao_id, ordem"))
public class ComponentePrecificacaoJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulacao_id", nullable = false)
    private SimulacaoPrecificacaoJpaEntity simulacao;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 40)
    private TipoComponenteCusto tipo;

    @Column(name = "percentual", precision = 8, scale = 2, nullable = false)
    private BigDecimal percentual;

    @Column(name = "valor", precision = 12, scale = 2, nullable = false)
    private BigDecimal valor;

    @Column(name = "base_calculo", precision = 12, scale = 2, nullable = false)
    private BigDecimal baseCalculo;

    @Column(name = "ordem", nullable = false)
    private int ordem;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public SimulacaoPrecificacaoJpaEntity getSimulacao() { return simulacao; }
    public void setSimulacao(SimulacaoPrecificacaoJpaEntity simulacao) { this.simulacao = simulacao; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public TipoComponenteCusto getTipo() { return tipo; }
    public void setTipo(TipoComponenteCusto tipo) { this.tipo = tipo; }
    public BigDecimal getPercentual() { return percentual; }
    public void setPercentual(BigDecimal percentual) { this.percentual = percentual; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public BigDecimal getBaseCalculo() { return baseCalculo; }
    public void setBaseCalculo(BigDecimal baseCalculo) { this.baseCalculo = baseCalculo; }
    public int getOrdem() { return ordem; }
    public void setOrdem(int ordem) { this.ordem = ordem; }
}
