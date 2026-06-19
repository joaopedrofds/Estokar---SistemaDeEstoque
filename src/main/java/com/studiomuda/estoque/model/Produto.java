package com.studiomuda.estoque.model;

import javax.persistence.*;

@Entity
@Table(name = "produto")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "quantidade")
    private int quantidade;

    @Column(name = "valor")
    private double valor;

    @Column(name = "custo", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private Double custo = 0.0;

    public Produto() {}

    public Produto(int id, String nome, String descricao, String tipo, int quantidade, double valor) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.valor = valor;
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

    public Double getCusto() { return custo != null ? custo : 0.0; }
    public void setCusto(Double custo) { this.custo = custo != null ? custo : 0.0; }

    @Override
    public String toString() {
        return String.format("ID: %d | Nome: %s | Tipo: %s | Valor: R$ %.2f | Quantidade: %d | Descrição: %s",
                id, nome, tipo, valor, quantidade, (descricao == null || descricao.isBlank() ? "Sem descrição" : descricao));
    }
}