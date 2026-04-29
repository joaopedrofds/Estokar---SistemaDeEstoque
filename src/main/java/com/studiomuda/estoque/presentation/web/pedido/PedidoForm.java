package com.studiomuda.estoque.presentation.web.pedido;

import com.studiomuda.estoque.application.pedido.dto.SalvarPedidoCommand;
import com.studiomuda.estoque.domain.pedido.Pedido;

import java.sql.Date;
import java.time.LocalDate;

public class PedidoForm {
    private int id;
    private Date dataRequisicao;
    private Date dataEntrega;
    private int clienteId;
    private int funcionarioId;
    private Integer cupomId;
    private String statusPagamento = "PENDENTE";
    private Date dataPagamento;
    private String status;

    public static PedidoForm desde(Pedido pedido) {
        PedidoForm form = new PedidoForm();
        form.id = pedido.id();
        form.dataRequisicao = pedido.dataRequisicao() != null ? Date.valueOf(pedido.dataRequisicao()) : null;
        form.dataEntrega = pedido.dataEntrega() != null ? Date.valueOf(pedido.dataEntrega()) : null;
        form.clienteId = pedido.clienteId();
        form.funcionarioId = pedido.funcionarioId();
        form.cupomId = pedido.cupomId();
        form.statusPagamento = pedido.statusPagamento() != null ? pedido.statusPagamento().name() : "PENDENTE";
        form.dataPagamento = pedido.dataPagamento() != null ? Date.valueOf(pedido.dataPagamento()) : null;
        form.status = pedido.status();
        return form;
    }

    public SalvarPedidoCommand toCommand(LocalDate dataRequisicao, LocalDate dataEntrega, LocalDate dataPagamento, Integer cupomId) {
        return new SalvarPedidoCommand(
                id,
                dataRequisicao,
                dataEntrega,
                clienteId,
                funcionarioId,
                cupomId,
                statusPagamento,
                dataPagamento,
                status
        );
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Date getDataRequisicao() { return dataRequisicao; }
    public void setDataRequisicao(Date dataRequisicao) { this.dataRequisicao = dataRequisicao; }
    public Date getDataEntrega() { return dataEntrega; }
    public void setDataEntrega(Date dataEntrega) { this.dataEntrega = dataEntrega; }
    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }
    public int getFuncionarioId() { return funcionarioId; }
    public void setFuncionarioId(int funcionarioId) { this.funcionarioId = funcionarioId; }
    public Integer getCupomId() { return cupomId; }
    public void setCupomId(Integer cupomId) { this.cupomId = cupomId; }
    public String getStatusPagamento() { return statusPagamento; }
    public void setStatusPagamento(String statusPagamento) { this.statusPagamento = statusPagamento; }
    public Date getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(Date dataPagamento) { this.dataPagamento = dataPagamento; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
