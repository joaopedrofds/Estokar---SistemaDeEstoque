package com.studiomuda.estoque.model;

import java.sql.Date;

public class SugestaoJanelaRemessa {
    private int docaId;
    private String docaNome;
    private Date data;
    private String horario;
    private int capacidadeDisponivel;

    public SugestaoJanelaRemessa(int docaId, String docaNome, Date data, String horario, int capacidadeDisponivel) {
        this.docaId = docaId;
        this.docaNome = docaNome;
        this.data = data;
        this.horario = horario;
        this.capacidadeDisponivel = capacidadeDisponivel;
    }

    public int getDocaId() { return docaId; }
    public String getDocaNome() { return docaNome; }
    public Date getData() { return data; }
    public String getHorario() { return horario; }
    public int getCapacidadeDisponivel() { return capacidadeDisponivel; }
}
