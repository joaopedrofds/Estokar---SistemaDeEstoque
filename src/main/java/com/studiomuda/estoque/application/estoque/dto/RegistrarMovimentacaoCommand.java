package com.studiomuda.estoque.application.estoque.dto;

import java.time.LocalDate;

public class RegistrarMovimentacaoCommand {
    private final int produtoId;
    private final String tipo;
    private final int quantidade;
    private final String motivo;
    private final LocalDate data;

    public RegistrarMovimentacaoCommand(int produtoId, String tipo, int quantidade, String motivo, LocalDate data) {
        this.produtoId = produtoId;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.motivo = motivo;
        this.data = data;
    }

    public int produtoId() { return produtoId; }
    public String tipo() { return tipo; }
    public int quantidade() { return quantidade; }
    public String motivo() { return motivo; }
    public LocalDate data() { return data; }
}
