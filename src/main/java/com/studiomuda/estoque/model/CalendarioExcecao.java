package com.studiomuda.estoque.model;

import java.sql.Date;

public class CalendarioExcecao {
    private int id;
    private Date data;
    private String motivo;
    private boolean ativa;

    public CalendarioExcecao() {
        this.ativa = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }
}
