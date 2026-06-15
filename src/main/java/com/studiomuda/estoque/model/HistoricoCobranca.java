package com.studiomuda.estoque.model;

import java.time.LocalDateTime;

public class HistoricoCobranca {
    private int id;
    private int clienteId;
    private Integer faturaId;
    private Integer acordoId;
    private Integer registroOriginalId;
    private String tipo;
    private String descricao;
    private String usuario;
    private LocalDateTime criadoEm;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public Integer getFaturaId() {
        return faturaId;
    }

    public void setFaturaId(Integer faturaId) {
        this.faturaId = faturaId;
    }

    public Integer getAcordoId() {
        return acordoId;
    }

    public void setAcordoId(Integer acordoId) {
        this.acordoId = acordoId;
    }

    public Integer getRegistroOriginalId() {
        return registroOriginalId;
    }

    public void setRegistroOriginalId(Integer registroOriginalId) {
        this.registroOriginalId = registroOriginalId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
