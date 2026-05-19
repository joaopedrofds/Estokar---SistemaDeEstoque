package com.studiomuda.estoque.model;

public class Distribuidora {
    private int id;
    private String nome;
    private String nivelPrioridade;
    private boolean ativa;

    public Distribuidora() {
        this.ativa = true;
    }

    public Distribuidora(int id, String nome, String nivelPrioridade, boolean ativa) {
        this.id = id;
        this.nome = nome;
        this.nivelPrioridade = nivelPrioridade;
        this.ativa = ativa;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getNivelPrioridade() { return nivelPrioridade; }
    public void setNivelPrioridade(String nivelPrioridade) { this.nivelPrioridade = nivelPrioridade; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    public int getPesoPrioridade() {
        if ("ALTA".equalsIgnoreCase(nivelPrioridade)) {
            return 1;
        }
        if ("MEDIA".equalsIgnoreCase(nivelPrioridade)) {
            return 2;
        }
        return 3;
    }
}
