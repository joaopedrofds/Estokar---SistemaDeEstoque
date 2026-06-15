package com.studiomuda.estoque.service;

public class ResultadoAvaliacaoCredito {
    private final boolean bloqueado;
    private final Integer faturaId;
    private final Integer pedidoId;
    private final int diasAtraso;
    private final String mensagem;

    private ResultadoAvaliacaoCredito(boolean bloqueado, Integer faturaId, Integer pedidoId, int diasAtraso, String mensagem) {
        this.bloqueado = bloqueado;
        this.faturaId = faturaId;
        this.pedidoId = pedidoId;
        this.diasAtraso = diasAtraso;
        this.mensagem = mensagem;
    }

    public static ResultadoAvaliacaoCredito liberado() {
        return new ResultadoAvaliacaoCredito(false, null, null, 0, "Venda liberada.");
    }

    public static ResultadoAvaliacaoCredito bloqueado(Integer faturaId, Integer pedidoId, int diasAtraso, String mensagem) {
        return new ResultadoAvaliacaoCredito(true, faturaId, pedidoId, diasAtraso, mensagem);
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public Integer getFaturaId() {
        return faturaId;
    }

    public Integer getPedidoId() {
        return pedidoId;
    }

    public int getDiasAtraso() {
        return diasAtraso;
    }

    public String getMensagem() {
        return mensagem;
    }
}
