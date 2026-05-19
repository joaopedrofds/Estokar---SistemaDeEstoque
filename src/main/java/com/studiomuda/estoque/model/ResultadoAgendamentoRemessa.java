package com.studiomuda.estoque.model;

import java.util.ArrayList;
import java.util.List;

public class ResultadoAgendamentoRemessa {
    private final boolean sucesso;
    private final String mensagem;
    private final List<SugestaoJanelaRemessa> sugestoes;

    private ResultadoAgendamentoRemessa(boolean sucesso, String mensagem, List<SugestaoJanelaRemessa> sugestoes) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.sugestoes = sugestoes;
    }

    public static ResultadoAgendamentoRemessa sucesso(String mensagem) {
        return new ResultadoAgendamentoRemessa(true, mensagem, new ArrayList<>());
    }

    public static ResultadoAgendamentoRemessa conflito(String mensagem, List<SugestaoJanelaRemessa> sugestoes) {
        return new ResultadoAgendamentoRemessa(false, mensagem, sugestoes);
    }

    public boolean isSucesso() { return sucesso; }
    public String getMensagem() { return mensagem; }
    public List<SugestaoJanelaRemessa> getSugestoes() { return sugestoes; }
}
