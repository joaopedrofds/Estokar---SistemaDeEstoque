package com.studiomuda.estoque.presentation.web.estoque;

import com.studiomuda.estoque.domain.estoque.MovimentacaoEstoque;
import com.studiomuda.estoque.domain.estoque.MovimentacaoEstoqueComProduto;

import java.sql.Date;

public class MovimentacaoView {
    private final int id;
    private final int idProduto;
    private final String tipo;
    private final int quantidade;
    private final String motivo;
    private final Date data;
    private final String produtoNome;

    public MovimentacaoView(MovimentacaoEstoque m, String produtoNome) {
        this.id = m.id();
        this.idProduto = m.produtoId();
        this.tipo = m.tipo().codigo();
        this.quantidade = m.quantidade();
        this.motivo = m.motivo();
        this.data = m.data() != null ? Date.valueOf(m.data()) : null;
        this.produtoNome = produtoNome;
    }

    public static MovimentacaoView desde(MovimentacaoEstoqueComProduto cp) {
        return new MovimentacaoView(cp.movimentacao(), cp.produtoNome());
    }

    public int getId() { return id; }
    public int getIdProduto() { return idProduto; }
    public String getTipo() { return tipo; }
    public int getQuantidade() { return quantidade; }
    public String getMotivo() { return motivo; }
    public Date getData() { return data; }
    public String getProdutoNome() { return produtoNome; }
}
