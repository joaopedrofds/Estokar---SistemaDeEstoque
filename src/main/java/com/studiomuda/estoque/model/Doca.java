package com.studiomuda.estoque.model;

public class Doca {
    private int id;
    private String nome;
    private int capacidadePaletesDiaria;
    private boolean ativa;

    public Doca() {
        this.ativa = true;
    }

    public Doca(int id, String nome, int capacidadePaletesDiaria, boolean ativa) {
        this.id = id;
        this.nome = nome;
        this.capacidadePaletesDiaria = capacidadePaletesDiaria;
        this.ativa = ativa;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getCapacidadePaletesDiaria() { return capacidadePaletesDiaria; }
    public void setCapacidadePaletesDiaria(int capacidadePaletesDiaria) { this.capacidadePaletesDiaria = capacidadePaletesDiaria; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }
}
