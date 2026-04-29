package com.studiomuda.estoque.domain.pedido;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Pedido {
    private final int id;
    private LocalDate dataRequisicao;
    private LocalDate dataEntrega;
    private final int clienteId;
    private final int funcionarioId;
    private Integer cupomId;
    private double valorDesconto;
    private String status;
    private StatusPagamento statusPagamento;
    private LocalDate dataPagamento;

    public Pedido(int id, LocalDate dataRequisicao, LocalDate dataEntrega,
                  int clienteId, int funcionarioId, Integer cupomId, double valorDesconto,
                  String status, StatusPagamento statusPagamento, LocalDate dataPagamento) {
        if (clienteId <= 0) throw new IllegalArgumentException("Cliente é obrigatório.");
        this.id = id;
        this.dataRequisicao = dataRequisicao;
        this.dataEntrega = dataEntrega;
        this.clienteId = clienteId;
        this.funcionarioId = funcionarioId;
        atribuirCupom(cupomId, valorDesconto);
        this.status = status;
        this.statusPagamento = statusPagamento != null ? statusPagamento : StatusPagamento.PENDENTE;
        atualizarDataPagamento(dataPagamento);
    }

    public static Pedido novo(LocalDate dataRequisicao, LocalDate dataEntrega,
                               int clienteId, int funcionarioId,
                               Integer cupomId, double valorDesconto,
                               StatusPagamento statusPagamento, LocalDate dataPagamento) {
        return new Pedido(0, dataRequisicao, dataEntrega, clienteId, funcionarioId,
                cupomId, valorDesconto, null, statusPagamento, dataPagamento);
    }

    public void atribuirCupom(Integer cupomId, double valorDesconto) {
        if (cupomId != null && cupomId > 0) {
            if (valorDesconto < 0) throw new IllegalArgumentException("Desconto não pode ser negativo.");
            this.cupomId = cupomId;
            this.valorDesconto = valorDesconto;
        } else {
            this.cupomId = null;
            this.valorDesconto = 0.0;
        }
    }

    public void marcarComoPago(LocalDate data) {
        this.statusPagamento = StatusPagamento.PAGO;
        this.dataPagamento = data != null ? data : LocalDate.now();
    }

    public void marcarComoPendente() {
        this.statusPagamento = StatusPagamento.PENDENTE;
        this.dataPagamento = null;
    }

    public void atualizarDatas(LocalDate dataRequisicao, LocalDate dataEntrega) {
        this.dataRequisicao = dataRequisicao;
        this.dataEntrega = dataEntrega;
    }

    public void atualizarStatus(String status) {
        this.status = status;
    }

    private void atualizarDataPagamento(LocalDate dataPagamento) {
        if (statusPagamento == StatusPagamento.PAGO) {
            this.dataPagamento = dataPagamento != null ? dataPagamento : LocalDate.now();
        } else {
            this.dataPagamento = null;
        }
    }

    public int diasAtrasoPagamento() {
        if (dataRequisicao == null || statusPagamento == StatusPagamento.PAGO) return 0;
        long dias = ChronoUnit.DAYS.between(dataRequisicao, LocalDate.now());
        return (int) Math.max(dias, 0);
    }

    public double calcularTotal(double subtotalItens) {
        double total = subtotalItens - valorDesconto;
        return Math.max(0, total);
    }

    public int id() { return id; }
    public LocalDate dataRequisicao() { return dataRequisicao; }
    public LocalDate dataEntrega() { return dataEntrega; }
    public int clienteId() { return clienteId; }
    public int funcionarioId() { return funcionarioId; }
    public Integer cupomId() { return cupomId; }
    public double valorDesconto() { return valorDesconto; }
    public String status() { return status; }
    public StatusPagamento statusPagamento() { return statusPagamento; }
    public LocalDate dataPagamento() { return dataPagamento; }
}
