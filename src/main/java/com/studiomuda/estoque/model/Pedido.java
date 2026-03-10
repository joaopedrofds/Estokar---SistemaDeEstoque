package com.studiomuda.estoque.model;

import java.sql.Date;

public class Pedido {
    private int id;
    private Date dataRequisicao;
    private Date dataEntrega;
    private int clienteId;
    private String clienteNome; // Campo auxiliar para exibição
    private int cupomId; // ID do cupom de desconto
    private int funcionarioId; // ID do funcionário que realizou a venda
    private String funcionarioNome; // Campo auxiliar para exibição
    private String funcionarioCargo; // Campo auxiliar para exibição
    private double valorDesconto; // Valor do desconto aplicado pelo cupom
    private String status; // Novo campo para status do pedido
    private String clienteCpfCnpj; // Campo auxiliar para exibição do CPF/CNPJ do cliente
    private String cupomCodigo;    // Campo auxiliar para exibição do código do cupom

    public Pedido() {}

    public Pedido(int id, Date dataRequisicao, Date dataEntrega, int clienteId, int cupomId, int funcionarioId, double valorDesconto) {
        this.id = id;
        this.dataRequisicao = dataRequisicao;
        this.dataEntrega = dataEntrega;
        this.clienteId = clienteId;
        this.cupomId = cupomId;
        this.funcionarioId = funcionarioId;
        this.valorDesconto = valorDesconto;
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
    
    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }
    
    public int getCupomId() {
        return cupomId;
    }

    public void setCupomId(int cupomId) {
        this.cupomId = cupomId;
    }

    public int getFuncionarioId() {
        return funcionarioId;
    }

    public void setFuncionarioId(int funcionarioId) {
        this.funcionarioId = funcionarioId;
    }

    public String getFuncionarioNome() {
        return funcionarioNome;
    }

    public void setFuncionarioNome(String funcionarioNome) {
        this.funcionarioNome = funcionarioNome;
    }
    
    public String getFuncionarioCargo() {
        return funcionarioCargo;
    }

    public void setFuncionarioCargo(String funcionarioCargo) {
        this.funcionarioCargo = funcionarioCargo;
    }
    
    public double getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClienteCpfCnpj() {
        return clienteCpfCnpj;
    }

    public void setClienteCpfCnpj(String clienteCpfCnpj) {
        this.clienteCpfCnpj = clienteCpfCnpj;
    }

    public String getCupomCodigo() {
        return cupomCodigo;
    }

    public void setCupomCodigo(String cupomCodigo) {
        this.cupomCodigo = cupomCodigo;
    }

    @Override
    public String toString() {
        return "Pedido #" + id +
                " | Requisição: " + dataRequisicao +
                " | Entrega: " + dataEntrega +
                " | Cliente ID: " + clienteId +
                " | Funcionário ID: " + funcionarioId +
                " | Cupom ID: " + cupomId +
                " | Status: " + status;
    }
}
