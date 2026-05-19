package com.studiomuda.estoque.model;

import java.util.ArrayList;
import java.util.List;

public class UsuarioAcesso {
    private int id;
    private String username;
    private String nome;
    private String senha;
    private boolean ativo;
    private List<Integer> perfilIds;
    private List<String> perfilNomes;

    public UsuarioAcesso() {
        this.ativo = true;
        this.perfilIds = new ArrayList<>();
        this.perfilNomes = new ArrayList<>();
    }

    public UsuarioAcesso(int id, String username, String nome, String senha, boolean ativo) {
        this();
        this.id = id;
        this.username = username;
        this.nome = nome;
        this.senha = senha;
        this.ativo = ativo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public List<Integer> getPerfilIds() {
        return perfilIds;
    }

    public void setPerfilIds(List<Integer> perfilIds) {
        this.perfilIds = perfilIds != null ? perfilIds : new ArrayList<>();
    }

    public List<String> getPerfilNomes() {
        return perfilNomes;
    }

    public void setPerfilNomes(List<String> perfilNomes) {
        this.perfilNomes = perfilNomes != null ? perfilNomes : new ArrayList<>();
    }
}
