<<<<<<< HEAD
package com.studiomuda.estoque.model;

import java.sql.Date;

public class OrdemCompra {
    public static final String STATUS_RASCUNHO = "RASCUNHO";
    public static final String STATUS_APROVADA = "APROVADA";
    public static final String STATUS_REJEITADA = "REJEITADA";

    private int id;
    private int fornecedorId;
    private String fornecedorNome;
    private String status;
    private double valorTotal;
    private Date dataCriacao;
    private Date dataAprovacao;
    private int itemId;
    private int produtoId;
    private String produtoNome;
    private int quantidade;
    private double valorUnitario;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFornecedorId() { return fornecedorId; }
    public void setFornecedorId(int fornecedorId) { this.fornecedorId = fornecedorId; }

    public String getFornecedorNome() { return fornecedorNome; }
    public void setFornecedorNome(String fornecedorNome) { this.fornecedorNome = fornecedorNome; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    public Date getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Date dataCriacao) { this.dataCriacao = dataCriacao; }

    public Date getDataAprovacao() { return dataAprovacao; }
    public void setDataAprovacao(Date dataAprovacao) { this.dataAprovacao = dataAprovacao; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public String getProdutoNome() { return produtoNome; }
    public void setProdutoNome(String produtoNome) { this.produtoNome = produtoNome; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(double valorUnitario) { this.valorUnitario = valorUnitario; }

    public boolean isRascunho() {
        return STATUS_RASCUNHO.equals(status);
    }

    public void ajustarRascunho(int quantidade, double valorUnitario) {
        if (!isRascunho()) {
            throw new IllegalStateException("Apenas ordens em rascunho podem ser ajustadas.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        if (valorUnitario < 0) {
            throw new IllegalArgumentException("O valor unitario nao pode ser negativo.");
        }
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.valorTotal = quantidade * valorUnitario;
    }
}
=======
package com.studiomuda.estoque.model;

import java.sql.Date;

public class OrdemCompra {
    public static final String STATUS_RASCUNHO = "RASCUNHO";
    public static final String STATUS_APROVADA = "APROVADA";
    public static final String STATUS_REJEITADA = "REJEITADA";

    private int id;
    private String codigoOrdem;
    private int fornecedorId;
    private String fornecedorNome;
    private String status;
    private double valorTotal;
    private Date dataCriacao;
    private Date dataAprovacao;
    private int itemId;
    private int produtoId;
    private String produtoNome;
    private int quantidade;
    private double valorUnitario;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigoOrdem() { return codigoOrdem; }
    public void setCodigoOrdem(String codigoOrdem) { this.codigoOrdem = codigoOrdem; }

    public int getFornecedorId() { return fornecedorId; }
    public void setFornecedorId(int fornecedorId) { this.fornecedorId = fornecedorId; }

    public String getFornecedorNome() { return fornecedorNome; }
    public void setFornecedorNome(String fornecedorNome) { this.fornecedorNome = fornecedorNome; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    public Date getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Date dataCriacao) { this.dataCriacao = dataCriacao; }

    public Date getDataAprovacao() { return dataAprovacao; }
    public void setDataAprovacao(Date dataAprovacao) { this.dataAprovacao = dataAprovacao; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public String getProdutoNome() { return produtoNome; }
    public void setProdutoNome(String produtoNome) { this.produtoNome = produtoNome; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(double valorUnitario) { this.valorUnitario = valorUnitario; }

    public boolean isRascunho() {
        return STATUS_RASCUNHO.equals(status);
    }

    public void ajustarRascunho(int quantidade, double valorUnitario) {
        if (!isRascunho()) {
            throw new IllegalStateException("Apenas ordens em rascunho podem ser ajustadas.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        if (valorUnitario < 0) {
            throw new IllegalArgumentException("O valor unitario nao pode ser negativo.");
        }
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.valorTotal = quantidade * valorUnitario;
    }
}
>>>>>>> ad03230 (Refatorando e atualizando Suprimentos e Remessas)
