package com.studiomuda.estoque.model;

import java.sql.Timestamp;

public class LogAcesso {
    private int id;
    private Integer usuarioId;
    private String username;
    private String recurso;
    private String operacao;
    private String resultado;
    private String detalhe;
    private Timestamp dataHora;

    public LogAcesso() {
    }

    public LogAcesso(int id, Integer usuarioId, String username, String recurso, String operacao,
                     String resultado, String detalhe, Timestamp dataHora) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.username = username;
        this.recurso = recurso;
        this.operacao = operacao;
        this.resultado = resultado;
        this.detalhe = detalhe;
        this.dataHora = dataHora;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRecurso() {
        return recurso;
    }

    public void setRecurso(String recurso) {
        this.recurso = recurso;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public void setDetalhe(String detalhe) {
        this.detalhe = detalhe;
    }

    public Timestamp getDataHora() {
        return dataHora;
    }

    public void setDataHora(Timestamp dataHora) {
        this.dataHora = dataHora;
    }
}
