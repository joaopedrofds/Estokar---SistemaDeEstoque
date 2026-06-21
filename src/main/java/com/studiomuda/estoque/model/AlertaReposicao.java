package com.studiomuda.estoque.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerta_reposicao")
public class AlertaReposicao {

    public static final String STATUS_ATIVO = "ATIVO";
    public static final String STATUS_RESOLVIDO = "RESOLVIDO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "produto_id", nullable = false)
    private Integer produtoId;

    @Column(name = "produto_nome", nullable = false, length = 120)
    private String produtoNome;

    @Column(name = "fornecedor_nome", nullable = false, length = 120)
    private String fornecedorNome;

    @Column(name = "estoque_atual", nullable = false)
    private Integer estoqueAtual;

    @Column(name = "ponto_pedido", nullable = false)
    private Integer pontoPedido;

    @Column(name = "quantidade_sugerida", nullable = false)
    private Integer quantidadeSugerida;

    @Column(name = "status", nullable = false, length = 20)
    private String status = STATUS_ATIVO;

    @Column(name = "observacao", length = 300)
    private String observacao;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @Column(name = "resolvido_em")
    private LocalDateTime resolvidoEm;

    @PrePersist
    void prePersist() {
        LocalDateTime agora = LocalDateTime.now();
        if (criadoEm == null) {
            criadoEm = agora;
        }
        atualizadoEm = agora;
        if (status == null || status.isBlank()) {
            status = STATUS_ATIVO;
        }
    }

    @PreUpdate
    void preUpdate() {
        atualizadoEm = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public Integer getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Integer produtoId) {
        this.produtoId = produtoId;
    }

    public String getProdutoNome() {
        return produtoNome;
    }

    public void setProdutoNome(String produtoNome) {
        this.produtoNome = produtoNome;
    }

    public String getFornecedorNome() {
        return fornecedorNome;
    }

    public void setFornecedorNome(String fornecedorNome) {
        this.fornecedorNome = fornecedorNome;
    }

    public Integer getEstoqueAtual() {
        return estoqueAtual;
    }

    public void setEstoqueAtual(Integer estoqueAtual) {
        this.estoqueAtual = estoqueAtual;
    }

    public Integer getPontoPedido() {
        return pontoPedido;
    }

    public void setPontoPedido(Integer pontoPedido) {
        this.pontoPedido = pontoPedido;
    }

    public Integer getQuantidadeSugerida() {
        return quantidadeSugerida;
    }

    public void setQuantidadeSugerida(Integer quantidadeSugerida) {
        this.quantidadeSugerida = quantidadeSugerida;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public LocalDateTime getResolvidoEm() {
        return resolvidoEm;
    }

    public void setResolvidoEm(LocalDateTime resolvidoEm) {
        this.resolvidoEm = resolvidoEm;
    }

    public boolean isAtivo() {
        return STATUS_ATIVO.equalsIgnoreCase(status);
    }
}
