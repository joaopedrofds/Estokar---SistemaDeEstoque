package com.studiomuda.estoque.presentation.web.produto;

import com.studiomuda.estoque.application.produto.dto.SalvarProdutoCommand;
import com.studiomuda.estoque.domain.produto.Produto;

public class ProdutoForm {
    private int id;
    private String nome;
    private String descricao;
    private String tipo;
    private int quantidade;
    private double valor;

    public ProdutoForm() {}

    public static ProdutoForm desde(Produto p) {
        ProdutoForm f = new ProdutoForm();
        f.id = p.id();
        f.nome = p.nome();
        f.descricao = p.descricao();
        f.tipo = p.tipo().name();
        f.quantidade = p.quantidade();
        f.valor = p.valor();
        return f;
    }

    public SalvarProdutoCommand toCommand() {
        return new SalvarProdutoCommand(id, nome, descricao, tipo, quantidade, valor);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
}
