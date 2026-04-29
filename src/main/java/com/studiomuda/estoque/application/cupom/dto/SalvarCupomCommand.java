package com.studiomuda.estoque.application.cupom.dto;

import java.time.LocalDate;

public class SalvarCupomCommand {
    private final int id;
    private final String codigo;
    private final String descricao;
    private final double valor;
    private final LocalDate dataInicio;
    private final LocalDate validade;
    private final String condicoesUso;

    public SalvarCupomCommand(int id, String codigo, String descricao, double valor,
                              LocalDate dataInicio, LocalDate validade, String condicoesUso) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.valor = valor;
        this.dataInicio = dataInicio;
        this.validade = validade;
        this.condicoesUso = condicoesUso;
    }

    public int id() { return id; }
    public String codigo() { return codigo; }
    public String descricao() { return descricao; }
    public double valor() { return valor; }
    public LocalDate dataInicio() { return dataInicio; }
    public LocalDate validade() { return validade; }
    public String condicoesUso() { return condicoesUso; }
}
