package com.studiomuda.estoque.model;

public class ParametroEstoque {
    private int id;
    private int produtoId;
    private int fornecedorId;
    private int margemSeguranca;
    private String produtoNome;
    private String fornecedorNome;
    private int leadTimeDias;
    private int estoqueAtual;
    private double consumoMedioDiario;
    private int pontoPedido;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public int getFornecedorId() { return fornecedorId; }
    public void setFornecedorId(int fornecedorId) { this.fornecedorId = fornecedorId; }

    public int getMargemSeguranca() { return margemSeguranca; }
    public void setMargemSeguranca(int margemSeguranca) { this.margemSeguranca = margemSeguranca; }

    public String getProdutoNome() { return produtoNome; }
    public void setProdutoNome(String produtoNome) { this.produtoNome = produtoNome; }

    public String getFornecedorNome() { return fornecedorNome; }
    public void setFornecedorNome(String fornecedorNome) { this.fornecedorNome = fornecedorNome; }

    public int getLeadTimeDias() { return leadTimeDias; }
    public void setLeadTimeDias(int leadTimeDias) { this.leadTimeDias = leadTimeDias; }

    public int getEstoqueAtual() { return estoqueAtual; }
    public void setEstoqueAtual(int estoqueAtual) { this.estoqueAtual = estoqueAtual; }

    public double getConsumoMedioDiario() { return consumoMedioDiario; }
    public void setConsumoMedioDiario(double consumoMedioDiario) { this.consumoMedioDiario = consumoMedioDiario; }

    public int getPontoPedido() { return pontoPedido; }
    public void setPontoPedido(int pontoPedido) { this.pontoPedido = pontoPedido; }

    public void calcularPontoPedido(double consumoMedioDiario) {
        this.consumoMedioDiario = consumoMedioDiario;
        this.pontoPedido = (int) Math.ceil((consumoMedioDiario * leadTimeDias) + margemSeguranca);
    }

    public int calcularQuantidadeSugerida() {
        return Math.max(pontoPedido - estoqueAtual + margemSeguranca, 1);
    }

    public boolean isReposicaoNecessaria() {
        return estoqueAtual <= pontoPedido;
    }
}
