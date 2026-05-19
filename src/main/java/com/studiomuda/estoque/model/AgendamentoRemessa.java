package com.studiomuda.estoque.model;

import java.sql.Date;

public class AgendamentoRemessa {
    public static final String STATUS_CONFIRMADO = "CONFIRMADO";
    public static final String STATUS_CANCELADO = "CANCELADO";

    private int id;
    private int docaId;
    private int distribuidoraId;
    private Date data;
    private String horario;
    private int volumePaletes;
    private String status;
    private String docaNome;
    private String distribuidoraNome;
    private String prioridadeDistribuidora;

    public AgendamentoRemessa() {
        this.status = STATUS_CONFIRMADO;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDocaId() { return docaId; }
    public void setDocaId(int docaId) { this.docaId = docaId; }

    public int getDistribuidoraId() { return distribuidoraId; }
    public void setDistribuidoraId(int distribuidoraId) { this.distribuidoraId = distribuidoraId; }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public int getVolumePaletes() { return volumePaletes; }
    public void setVolumePaletes(int volumePaletes) { this.volumePaletes = volumePaletes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDocaNome() { return docaNome; }
    public void setDocaNome(String docaNome) { this.docaNome = docaNome; }

    public String getDistribuidoraNome() { return distribuidoraNome; }
    public void setDistribuidoraNome(String distribuidoraNome) { this.distribuidoraNome = distribuidoraNome; }

    public String getPrioridadeDistribuidora() { return prioridadeDistribuidora; }
    public void setPrioridadeDistribuidora(String prioridadeDistribuidora) { this.prioridadeDistribuidora = prioridadeDistribuidora; }
}
