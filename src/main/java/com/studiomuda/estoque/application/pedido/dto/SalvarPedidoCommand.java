package com.studiomuda.estoque.application.pedido.dto;

import java.time.LocalDate;

public class SalvarPedidoCommand {
    private final int id;
    private final LocalDate dataRequisicao;
    private final LocalDate dataEntrega;
    private final int clienteId;
    private final int funcionarioId;
    private final Integer cupomId;
    private final String statusPagamento;
    private final LocalDate dataPagamento;
    private final String status;

    public SalvarPedidoCommand(int id, LocalDate dataRequisicao, LocalDate dataEntrega, int clienteId,
                                int funcionarioId, Integer cupomId, String statusPagamento,
                                LocalDate dataPagamento, String status) {
        this.id = id;
        this.dataRequisicao = dataRequisicao;
        this.dataEntrega = dataEntrega;
        this.clienteId = clienteId;
        this.funcionarioId = funcionarioId;
        this.cupomId = cupomId;
        this.statusPagamento = statusPagamento;
        this.dataPagamento = dataPagamento;
        this.status = status;
    }

    public int id() { return id; }
    public LocalDate dataRequisicao() { return dataRequisicao; }
    public LocalDate dataEntrega() { return dataEntrega; }
    public int clienteId() { return clienteId; }
    public int funcionarioId() { return funcionarioId; }
    public Integer cupomId() { return cupomId; }
    public String statusPagamento() { return statusPagamento; }
    public LocalDate dataPagamento() { return dataPagamento; }
    public String status() { return status; }
}
