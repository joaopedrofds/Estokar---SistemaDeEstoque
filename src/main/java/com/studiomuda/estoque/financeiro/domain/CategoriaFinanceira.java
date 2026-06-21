package com.studiomuda.estoque.financeiro.domain;

/**
 * Categoria financeira (E-12) — agregado de domínio puro (padrão PetCollar).
 * Identidade por {@link CategoriaId}. {@code tipo} é RECEITA/DESPESA;
 * {@code origemSistema} indica a fonte automática do valor (ex.: PEDIDO_PAGO)
 * ou é nulo para categorias só de ajuste manual.
 */
public class CategoriaFinanceira {

    private final CategoriaId id;
    private final String nome;
    private final String tipo;
    private final String origemSistema;
    private final String descricao;
    private boolean ativo;

    public CategoriaFinanceira(CategoriaId id, String nome, String tipo, String origemSistema,
                               String descricao, boolean ativo) {
        if (id == null) {
            throw new IllegalArgumentException("Id da categoria não pode ser nulo.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome da categoria não pode ser vazio.");
        }
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("Tipo da categoria não pode ser vazio.");
        }
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.origemSistema = origemSistema;
        this.descricao = descricao;
        this.ativo = ativo;
    }

    /** Inativa a categoria (deixa de aparecer em seleções de novas operações). */
    public void inativar() {
        this.ativo = false;
    }

    public CategoriaId getId() { return id; }
    public String getNome() { return nome; }
    public String getTipo() { return tipo; }
    public String getOrigemSistema() { return origemSistema; }
    public String getDescricao() { return descricao; }
    public boolean isAtivo() { return ativo; }
}
