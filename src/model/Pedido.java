package model;

import java.sql.Date;

public class Pedido {
    private int id;
    private Date dataRequisicao;
    private Date dataEntrega;
    private int clienteId;

    public Pedido() {}

    public Pedido(int id, Date dataRequisicao, Date dataEntrega, int clienteId) {
        this.id = id;
        this.dataRequisicao = dataRequisicao;
        this.dataEntrega = dataEntrega;
        this.clienteId = clienteId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDataRequisicao() {
        return dataRequisicao;
    }

    public void setDataRequisicao(Date dataRequisicao) {
        this.dataRequisicao = dataRequisicao;
    }

    public Date getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(Date dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    @Override
    public String toString() {
        return "Pedido #" + id +
                " | Requisição: " + dataRequisicao +
                " | Entrega: " + dataEntrega +
                " | Cliente ID: " + clienteId;
    }
}
