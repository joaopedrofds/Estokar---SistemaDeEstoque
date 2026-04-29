package com.studiomuda.estoque.domain.estoque;

import java.time.LocalDate;

public class MovimentacaoEstoque {
    private final int id;
    private final int produtoId;
    private final TipoMovimentacao tipo;
    private final int quantidade;
    private String motivo;
    private LocalDate data;

    public MovimentacaoEstoque(int id, int produtoId, TipoMovimentacao tipo, int quantidade,
                               String motivo, LocalDate data) {
        this.id = id;
        if (produtoId <= 0) {
            throw new IllegalArgumentException("Produto é obrigatório.");
        }
        this.produtoId = produtoId;
        this.tipo = exigir(tipo);
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
        this.quantidade = quantidade;
        this.motivo = motivo;
        this.data = data != null ? data : LocalDate.now();
    }

    public static MovimentacaoEstoque novaEntrada(int produtoId, int quantidade, String motivo, LocalDate data) {
        return new MovimentacaoEstoque(0, produtoId, TipoMovimentacao.ENTRADA, quantidade, motivo, data);
    }

    public static MovimentacaoEstoque novaSaida(int produtoId, int quantidade, String motivo, LocalDate data) {
        return new MovimentacaoEstoque(0, produtoId, TipoMovimentacao.SAIDA, quantidade, motivo, data);
    }

    public void atualizarMetadados(String motivo, LocalDate data) {
        this.motivo = motivo;
        if (data != null) this.data = data;
    }

    public int deltaEstoque() {
        return tipo.sinalEstoque() * quantidade;
    }

    public int deltaEstorno() {
        return -deltaEstoque();
    }

    private static TipoMovimentacao exigir(TipoMovimentacao tipo) {
        if (tipo == null) throw new IllegalArgumentException("O tipo da movimentação é obrigatório.");
        return tipo;
    }

    public int id() { return id; }
    public int produtoId() { return produtoId; }
    public TipoMovimentacao tipo() { return tipo; }
    public int quantidade() { return quantidade; }
    public String motivo() { return motivo; }
    public LocalDate data() { return data; }
}
