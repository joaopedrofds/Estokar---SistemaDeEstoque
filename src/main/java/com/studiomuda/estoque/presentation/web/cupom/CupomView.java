package com.studiomuda.estoque.presentation.web.cupom;

import com.studiomuda.estoque.domain.cupom.Cupom;

import java.time.LocalDate;

public class CupomView {
    private final int id;
    private final String codigo;
    private final String descricao;
    private final double valor;
    private final LocalDate dataInicio;
    private final LocalDate validade;
    private final String condicoesUso;
    private final boolean valido;

    public CupomView(Cupom c) {
        this.id = c.id();
        this.codigo = c.codigo();
        this.descricao = c.descricao();
        this.valor = c.valor();
        this.dataInicio = c.dataInicio();
        this.validade = c.validade();
        this.condicoesUso = c.condicoesUso();
        this.valido = c.valido();
    }

    public int getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getDescricao() { return descricao; }
    public double getValor() { return valor; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getValidade() { return validade; }
    public String getCondicoesUso() { return condicoesUso; }
    public boolean isValido() { return valido; }
}
