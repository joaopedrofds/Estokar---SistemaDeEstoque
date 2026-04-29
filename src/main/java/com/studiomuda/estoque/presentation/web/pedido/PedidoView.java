package com.studiomuda.estoque.presentation.web.pedido;

import com.studiomuda.estoque.domain.pedido.Pedido;
import com.studiomuda.estoque.domain.pedido.PedidoComJoins;

import java.sql.Date;

public class PedidoView {
    private final int id;
    private final Date dataRequisicao;
    private final Date dataEntrega;
    private final int clienteId;
    private final Integer cupomId;
    private final int funcionarioId;
    private final double valorDesconto;
    private final String status;
    private final String statusPagamento;
    private final Date dataPagamento;
    private final int diasAtrasoPagamento;
    private final String clienteNome;
    private final String clienteCpfCnpj;
    private final String funcionarioNome;
    private final String funcionarioCargo;
    private final String cupomCodigo;

    public PedidoView(Pedido pedido) {
        this(pedido, null, null, null, null, null);
    }

    public PedidoView(PedidoComJoins pedidoComJoins) {
        this(
                pedidoComJoins.pedido(),
                pedidoComJoins.clienteNome(),
                pedidoComJoins.clienteCpfCnpj(),
                pedidoComJoins.funcionarioNome(),
                pedidoComJoins.funcionarioCargo(),
                pedidoComJoins.cupomCodigo()
        );
    }

    private PedidoView(Pedido pedido, String clienteNome, String clienteCpfCnpj,
                       String funcionarioNome, String funcionarioCargo, String cupomCodigo) {
        this.id = pedido.id();
        this.dataRequisicao = pedido.dataRequisicao() != null ? Date.valueOf(pedido.dataRequisicao()) : null;
        this.dataEntrega = pedido.dataEntrega() != null ? Date.valueOf(pedido.dataEntrega()) : null;
        this.clienteId = pedido.clienteId();
        this.cupomId = pedido.cupomId();
        this.funcionarioId = pedido.funcionarioId();
        this.valorDesconto = pedido.valorDesconto();
        this.status = pedido.status();
        this.statusPagamento = pedido.statusPagamento() != null ? pedido.statusPagamento().name() : "PENDENTE";
        this.dataPagamento = pedido.dataPagamento() != null ? Date.valueOf(pedido.dataPagamento()) : null;
        this.diasAtrasoPagamento = pedido.diasAtrasoPagamento();
        this.clienteNome = clienteNome;
        this.clienteCpfCnpj = clienteCpfCnpj;
        this.funcionarioNome = funcionarioNome;
        this.funcionarioCargo = funcionarioCargo;
        this.cupomCodigo = cupomCodigo;
    }

    public int getId() { return id; }
    public Date getDataRequisicao() { return dataRequisicao; }
    public Date getDataEntrega() { return dataEntrega; }
    public int getClienteId() { return clienteId; }
    public Integer getCupomId() { return cupomId; }
    public int getFuncionarioId() { return funcionarioId; }
    public double getValorDesconto() { return valorDesconto; }
    public String getStatus() { return status; }
    public String getStatusPagamento() { return statusPagamento; }
    public Date getDataPagamento() { return dataPagamento; }
    public Integer getDiasAtrasoPagamento() { return diasAtrasoPagamento; }
    public String getClienteNome() { return clienteNome; }
    public String getClienteCpfCnpj() { return clienteCpfCnpj; }
    public String getFuncionarioNome() { return funcionarioNome; }
    public String getFuncionarioCargo() { return funcionarioCargo; }
    public String getCupomCodigo() { return cupomCodigo; }
}
