package model;

public class Produto {
    private int id;
    private String nome;
    private String descricao;
    private String tipo;
    private int quantidade;
    private double valor;

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

    @Override
    public String toString() {
        return String.format("ID: %d | Nome: %s | Tipo: %s | Valor: R$ %.2f | Quantidade: %d | Descrição: %s",
                id, nome, tipo, valor, quantidade, (descricao == null || descricao.isBlank() ? "Sem descrição" : descricao));
    }
}
