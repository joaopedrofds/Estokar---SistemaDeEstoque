package com.studiomuda.estoque.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidade principal da devolução.
 * Nível Tático DDD: Entity (possui ciclo de vida com status)
 */
@Entity
@Table(name = "devolucao")
public class Devolucao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "pedido_id", nullable = false)
    private int pedidoId;

    @Column(name = "cliente_id", nullable = false)
    private int clienteId;

    @Transient
    private String clienteNome;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDENTE";

    @Column(name = "motivo", length = 255)
    private String motivo;

    @Column(name = "tipo_restituicao", length = 50)
    private String tipoRestituicao = "CREDITO_LOJA";

    @Column(name = "observacao_gestor", columnDefinition = "TEXT")
    private String observacaoGestor;

    @Column(name = "data_solicitacao")
    private LocalDateTime dataSolicitacao;

    @Column(name = "data_resolucao")
    private LocalDateTime dataResolucao;

    @Transient
    private List<ItemDevolucao> itens;

    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }
    public int getPedidoId()                        { return pedidoId; }
    public void setPedidoId(int pedidoId)           { this.pedidoId = pedidoId; }
    public int getClienteId()                       { return clienteId; }
    public void setClienteId(int clienteId)         { this.clienteId = clienteId; }
    public String getClienteNome()                  { return clienteNome; }
    public void setClienteNome(String n)            { this.clienteNome = n; }
    public String getStatus()                       { return status; }
    public void setStatus(String status)            { this.status = status; }
    public String getMotivo()                       { return motivo; }
    public void setMotivo(String motivo)            { this.motivo = motivo; }
    public String getTipoRestituicao()              { return tipoRestituicao; }
    public void setTipoRestituicao(String t)        { this.tipoRestituicao = t; }
    public String getObservacaoGestor()             { return observacaoGestor; }
    public void setObservacaoGestor(String o)       { this.observacaoGestor = o; }
    public LocalDateTime getDataSolicitacao()       { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDateTime d) { this.dataSolicitacao = d; }
    public LocalDateTime getDataResolucao()         { return dataResolucao; }
    public void setDataResolucao(LocalDateTime d)   { this.dataResolucao = d; }
    public List<ItemDevolucao> getItens()           { return itens; }
    public void setItens(List<ItemDevolucao> itens) { this.itens = itens; }

    public boolean isPendente()  { return "PENDENTE".equals(status); }
    public boolean isAprovada()  { return "APROVADA".equals(status); }
    public boolean isRejeitada() { return "REJEITADA".equals(status); }

    public double getValorTotal() {
        if (itens == null) return 0;
        return itens.stream().mapToDouble(i -> i.getQuantidade() * i.getValorUnitario()).sum();
    }
}