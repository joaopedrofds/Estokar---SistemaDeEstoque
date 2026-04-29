package com.studiomuda.estoque.domain.cupom;

import java.time.LocalDate;

public class Cupom {
    private final int id;
    private String codigo;
    private String descricao;
    private double valor;
    private LocalDate dataInicio;
    private LocalDate validade;
    private String condicoesUso;

    public Cupom(int id, String codigo, String descricao, double valor,
                 LocalDate dataInicio, LocalDate validade, String condicoesUso) {
        this.id = id;
        atualizarCodigo(codigo);
        this.descricao = descricao;
        atualizarValor(valor);
        atualizarVigencia(dataInicio, validade);
        this.condicoesUso = condicoesUso;
    }

    public static Cupom novo(String codigo, String descricao, double valor,
                              LocalDate dataInicio, LocalDate validade, String condicoesUso) {
        return new Cupom(0, codigo, descricao, valor, dataInicio, validade, condicoesUso);
    }

    public void atualizarDados(String codigo, String descricao, double valor,
                                LocalDate dataInicio, LocalDate validade, String condicoesUso) {
        atualizarCodigo(codigo);
        this.descricao = descricao;
        atualizarValor(valor);
        atualizarVigencia(dataInicio, validade);
        this.condicoesUso = condicoesUso;
    }

    public boolean valido() {
        return validoEm(LocalDate.now());
    }

    public boolean validoEm(LocalDate data) {
        if (dataInicio == null || validade == null) return false;
        return !data.isBefore(dataInicio) && !data.isAfter(validade);
    }

    public double aplicarDesconto(double total) {
        if (!valido()) return total;
        return Math.max(0, total - valor);
    }

    private void atualizarCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("O código é obrigatório.");
        }
        this.codigo = codigo.trim();
    }

    private void atualizarValor(double valor) {
        if (valor < 0) throw new IllegalArgumentException("Valor do cupom não pode ser negativo.");
        this.valor = valor;
    }

    private void atualizarVigencia(LocalDate dataInicio, LocalDate validade) {
        if (dataInicio != null && validade != null && validade.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Validade não pode ser anterior à data de início.");
        }
        this.dataInicio = dataInicio;
        this.validade = validade;
    }

    public int id() { return id; }
    public String codigo() { return codigo; }
    public String descricao() { return descricao; }
    public double valor() { return valor; }
    public LocalDate dataInicio() { return dataInicio; }
    public LocalDate validade() { return validade; }
    public String condicoesUso() { return condicoesUso; }
}
