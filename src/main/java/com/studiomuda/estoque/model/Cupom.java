package com.studiomuda.estoque.model;

import java.time.LocalDate;

public class Cupom {
    private int id;
    private String codigo;
    private String descricao;
    private double valor;
    private LocalDate dataInicio;
    private LocalDate validade;
    private String condicoesUso;

    public Cupom() {}

    public Cupom(int id, String codigo, String descricao, double valor, 
                LocalDate dataInicio, LocalDate validade, String condicoesUso) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.valor = valor;
        this.dataInicio = dataInicio;
        this.validade = validade;
        this.condicoesUso = condicoesUso;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getValidade() { return validade; }
    public void setValidade(LocalDate validade) { this.validade = validade; }

    public String getCondicoesUso() { return condicoesUso; }
    public void setCondicoesUso(String condicoesUso) { this.condicoesUso = condicoesUso; }

    public boolean isValido() {
        LocalDate hoje = LocalDate.now();
        return hoje.isAfter(dataInicio) && hoje.isBefore(validade.plusDays(1));
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Código: %s | Valor: R$ %.2f | Válido: %s a %s | Descrição: %s",
                id, codigo, valor, 
                dataInicio != null ? dataInicio.toString() : "N/A", 
                validade != null ? validade.toString() : "N/A", 
                descricao != null ? descricao : "Sem descrição");
    }
}
