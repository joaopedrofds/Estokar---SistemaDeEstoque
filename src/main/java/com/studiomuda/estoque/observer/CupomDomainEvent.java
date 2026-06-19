package com.studiomuda.estoque.observer;

public class CupomDomainEvent {
    private final int cupomId;
    private final int pedidoId;
    private final int clienteId;
    private final double valorDesconto;

    public CupomDomainEvent(int cupomId, int pedidoId, int clienteId, double valorDesconto) {
        this.cupomId = cupomId;
        this.pedidoId = pedidoId;
        this.clienteId = clienteId;
        this.valorDesconto = valorDesconto;
    }

    public int getCupomId()          { return cupomId; }
    public int getPedidoId()         { return pedidoId; }
    public int getClienteId()        { return clienteId; }
    public double getValorDesconto() { return valorDesconto; }
}