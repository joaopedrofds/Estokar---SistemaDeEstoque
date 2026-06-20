package com.studiomuda.estoque.precificacao.infrastructure.persistence.entity;

import com.studiomuda.estoque.precificacao.domain.model.StatusPrecificacao;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "precificacao_simulacao",
       indexes = {
           @Index(name = "idx_precificacao_simulacao_produto", columnList = "produto_id, data_simulacao"),
           @Index(name = "idx_precificacao_simulacao_status", columnList = "status, data_simulacao")
       })
public class SimulacaoPrecificacaoJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "produto_id", nullable = false)
    private int produtoId;

    @Column(name = "produto_nome", nullable = false, length = 150)
    private String produtoNome;

    @Column(name = "preco_atual", precision = 12, scale = 2, nullable = false)
    private BigDecimal precoAtual;

    @Column(name = "custo_compra", precision = 12, scale = 2, nullable = false)
    private BigDecimal custoCompra;

    @Column(name = "valor_impostos", precision = 12, scale = 2, nullable = false)
    private BigDecimal valorImpostos;

    @Column(name = "valor_despesas_operacionais", precision = 12, scale = 2, nullable = false)
    private BigDecimal valorDespesasOperacionais;

    @Column(name = "custo_total", precision = 12, scale = 2, nullable = false)
    private BigDecimal custoTotal;

    @Column(name = "preco_sugerido", precision = 12, scale = 2, nullable = false)
    private BigDecimal precoSugerido;

    @Column(name = "preco_minimo_permitido", precision = 12, scale = 2, nullable = false)
    private BigDecimal precoMinimoPermitido;

    @Column(name = "margem_lucro_desejada", precision = 8, scale = 2, nullable = false)
    private BigDecimal margemLucroDesejada;

    @Column(name = "margem_minima_global", precision = 8, scale = 2, nullable = false)
    private BigDecimal margemMinimaGlobal;

    @Column(name = "margem_real", precision = 8, scale = 2, nullable = false)
    private BigDecimal margemReal;

    @Column(name = "desconto_maximo_solicitado", precision = 8, scale = 2, nullable = false)
    private BigDecimal descontoMaximoSolicitado;

    @Column(name = "desconto_maximo_efetivo", precision = 8, scale = 2, nullable = false)
    private BigDecimal descontoMaximoEfetivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private StatusPrecificacao status;

    @Column(name = "justificativa", columnDefinition = "TEXT")
    private String justificativa;

    @Column(name = "usuario_responsavel", length = 80, nullable = false)
    private String usuarioResponsavel;

    @Column(name = "aplicado", nullable = false)
    private boolean aplicado = false;

    @Column(name = "data_simulacao", nullable = false)
    private LocalDateTime dataSimulacao = LocalDateTime.now();

    @Column(name = "data_aplicacao")
    private LocalDateTime dataAplicacao;

    @OneToMany(mappedBy = "simulacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ComponentePrecificacaoJpaEntity> componentes = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }
    public String getProdutoNome() { return produtoNome; }
    public void setProdutoNome(String produtoNome) { this.produtoNome = produtoNome; }
    public BigDecimal getPrecoAtual() { return precoAtual; }
    public void setPrecoAtual(BigDecimal precoAtual) { this.precoAtual = precoAtual; }
    public BigDecimal getCustoCompra() { return custoCompra; }
    public void setCustoCompra(BigDecimal custoCompra) { this.custoCompra = custoCompra; }
    public BigDecimal getValorImpostos() { return valorImpostos; }
    public void setValorImpostos(BigDecimal valorImpostos) { this.valorImpostos = valorImpostos; }
    public BigDecimal getValorDespesasOperacionais() { return valorDespesasOperacionais; }
    public void setValorDespesasOperacionais(BigDecimal valorDespesasOperacionais) { this.valorDespesasOperacionais = valorDespesasOperacionais; }
    public BigDecimal getCustoTotal() { return custoTotal; }
    public void setCustoTotal(BigDecimal custoTotal) { this.custoTotal = custoTotal; }
    public BigDecimal getPrecoSugerido() { return precoSugerido; }
    public void setPrecoSugerido(BigDecimal precoSugerido) { this.precoSugerido = precoSugerido; }
    public BigDecimal getPrecoMinimoPermitido() { return precoMinimoPermitido; }
    public void setPrecoMinimoPermitido(BigDecimal precoMinimoPermitido) { this.precoMinimoPermitido = precoMinimoPermitido; }
    public BigDecimal getMargemLucroDesejada() { return margemLucroDesejada; }
    public void setMargemLucroDesejada(BigDecimal margemLucroDesejada) { this.margemLucroDesejada = margemLucroDesejada; }
    public BigDecimal getMargemMinimaGlobal() { return margemMinimaGlobal; }
    public void setMargemMinimaGlobal(BigDecimal margemMinimaGlobal) { this.margemMinimaGlobal = margemMinimaGlobal; }
    public BigDecimal getMargemReal() { return margemReal; }
    public void setMargemReal(BigDecimal margemReal) { this.margemReal = margemReal; }
    public BigDecimal getDescontoMaximoSolicitado() { return descontoMaximoSolicitado; }
    public void setDescontoMaximoSolicitado(BigDecimal descontoMaximoSolicitado) { this.descontoMaximoSolicitado = descontoMaximoSolicitado; }
    public BigDecimal getDescontoMaximoEfetivo() { return descontoMaximoEfetivo; }
    public void setDescontoMaximoEfetivo(BigDecimal descontoMaximoEfetivo) { this.descontoMaximoEfetivo = descontoMaximoEfetivo; }
    public StatusPrecificacao getStatus() { return status; }
    public void setStatus(StatusPrecificacao status) { this.status = status; }
    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
    public String getUsuarioResponsavel() { return usuarioResponsavel; }
    public void setUsuarioResponsavel(String usuarioResponsavel) { this.usuarioResponsavel = usuarioResponsavel; }
    public boolean isAplicado() { return aplicado; }
    public void setAplicado(boolean aplicado) { this.aplicado = aplicado; }
    public LocalDateTime getDataSimulacao() { return dataSimulacao; }
    public void setDataSimulacao(LocalDateTime dataSimulacao) { this.dataSimulacao = dataSimulacao; }
    public LocalDateTime getDataAplicacao() { return dataAplicacao; }
    public void setDataAplicacao(LocalDateTime dataAplicacao) { this.dataAplicacao = dataAplicacao; }
    public List<ComponentePrecificacaoJpaEntity> getComponentes() { return componentes; }
    public void setComponentes(List<ComponentePrecificacaoJpaEntity> componentes) { this.componentes = componentes; }

    public boolean isAprovado() {
        return status == StatusPrecificacao.APROVADO || status == StatusPrecificacao.APLICADO;
    }

    public boolean isBloqueado() {
        return status == StatusPrecificacao.BLOQUEADO_DESCONTO || status == StatusPrecificacao.BLOQUEADO_MARGEM;
    }
}
