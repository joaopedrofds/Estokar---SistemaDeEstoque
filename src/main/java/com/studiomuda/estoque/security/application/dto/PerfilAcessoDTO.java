package com.studiomuda.estoque.security.application.dto;

import com.studiomuda.estoque.security.dominio.PerfilAcesso;

/**
 * DTO de formulário do perfil de acesso (E-11) — bean mutável para binding
 * {@code @ModelAttribute} do Thymeleaf. Id {@code int} (0 = novo).
 */
public class PerfilAcessoDTO {

    private int id;
    private String nome;
    private String descricao;
    private boolean ativo = true;

    public PerfilAcessoDTO() {}

    public static PerfilAcessoDTO de(PerfilAcesso p) {
        PerfilAcessoDTO dto = new PerfilAcessoDTO();
        dto.id = p.getId();
        dto.nome = p.getNome();
        dto.descricao = p.getDescricao();
        dto.ativo = p.isAtivo();
        return dto;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
