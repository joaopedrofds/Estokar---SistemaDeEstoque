package com.studiomuda.estoque.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Modelo representando o uso de um cupom em um pedido.
 * Nível Tático DDD: Entity
 */
@Entity
@Table(name = "cupom_uso")
public class CupomUso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "cupom_id", nullable = false)
    private int cupomId;

    @Column(name = "pedido_id", nullable = false)
    private int pedidoId;

    @Column(name = "cliente_id", nullable = false)
    private int clienteId;

    @Column(name = "valor_desconto", nullable = false, precision = 10, scale = 2)
    private double valorDesconto;

    @Column(name = "data_uso", nullable = false)
    private LocalDateTime dataUso;

    // Campos auxiliares para exibição (não persistem no banco)
    @Transient
    private String cupomCodigo;

    @Transient
    private String clienteNome;

    @Transient
    private String pedidoInfo;

    @Transient
    private String tipoCupom;

    public CupomUso() {}

    public CupomUso(int id, int cupomId, int pedidoId, int clienteId, double valorDesconto, LocalDateTime dataUso) {
        this.id = id;
        this.cupomId = cupomId;
        this.pedidoId = pedidoId;
        this.clienteId = clienteId;
        this.valorDesconto = valorDesconto;
        this.dataUso = dataUso;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCupomId() { return cupomId; }
    public void setCupomId(int cupomId) { this.cupomId = cupomId; }

    public int getPedidoId() { return pedidoId; }
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public double getValorDesconto() { return valorDesconto; }
    public void setValorDesconto(double valorDesconto) { this.valorDesconto = valorDesconto; }

    public LocalDateTime getDataUso() { return dataUso; }
    public void setDataUso(LocalDateTime dataUso) { this.dataUso = dataUso; }

    public String getCupomCodigo() { return cupomCodigo; }
    public void setCupomCodigo(String cupomCodigo) { this.cupomCodigo = cupomCodigo; }

    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }

    public String getPedidoInfo() { return pedidoInfo; }
    public void setPedidoInfo(String pedidoInfo) { this.pedidoInfo = pedidoInfo; }

    public String getTipoCupom() { return tipoCupom; }
    public void setTipoCupom(String tipoCupom) { this.tipoCupom = tipoCupom; }

    @Override
    public String toString() {
        return String.format("CupomUso[id=%d, cupomId=%d, pedidoId=%d, clienteId=%d, desconto=%.2f, data=%s]",
                id, cupomId, pedidoId, clienteId, valorDesconto, dataUso);
    }
}