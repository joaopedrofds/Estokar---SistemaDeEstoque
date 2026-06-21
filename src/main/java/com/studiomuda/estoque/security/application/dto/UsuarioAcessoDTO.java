package com.studiomuda.estoque.security.application.dto;

import com.studiomuda.estoque.security.dominio.UsuarioAcesso;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO de formulário e apresentação do usuário. Mantém {@code perfilNomes}
 * somente para exibição; a senha nunca é exposta à camada web.
 */
public class UsuarioAcessoDTO {

    private int id;
    private String username;
    private String nome;
    private boolean ativo = true;
    private List<Integer> perfilIds = new ArrayList<>();
    private List<String> perfilNomes = new ArrayList<>();

    public UsuarioAcessoDTO() {}

    public static UsuarioAcessoDTO de(UsuarioAcesso usuario, List<String> perfilNomes) {
        UsuarioAcessoDTO dto = new UsuarioAcessoDTO();
        dto.id = usuario.getId();
        dto.username = usuario.getUsername();
        dto.nome = usuario.getNome();
        dto.ativo = usuario.isAtivo();
        dto.perfilIds = new ArrayList<>(usuario.getPerfilIds());
        dto.perfilNomes = perfilNomes == null ? new ArrayList<>() : new ArrayList<>(perfilNomes);
        return dto;
    }

    public static UsuarioAcessoDTO de(UsuarioAcesso usuario) {
        return de(usuario, new ArrayList<>());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public List<Integer> getPerfilIds() { return perfilIds; }
    public void setPerfilIds(List<Integer> perfilIds) {
        this.perfilIds = perfilIds == null ? new ArrayList<>() : new ArrayList<>(perfilIds);
    }
    public List<String> getPerfilNomes() { return perfilNomes; }
    public void setPerfilNomes(List<String> perfilNomes) {
        this.perfilNomes = perfilNomes == null ? new ArrayList<>() : new ArrayList<>(perfilNomes);
    }
}
