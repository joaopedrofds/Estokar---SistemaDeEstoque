package com.studiomuda.estoque.presentation.web.estoque;

import com.studiomuda.estoque.application.estoque.dto.RegistrarMovimentacaoCommand;

import java.time.LocalDate;

public class MovimentacaoForm {
    private Integer id;
    private int idProduto;
    private String tipo = "entrada";
    private int quantidade;
    private String motivo;
    private LocalDate data;

    public MovimentacaoForm() {}

    public RegistrarMovimentacaoCommand toCommand(LocalDate dataOverride) {
        LocalDate dataFinal = dataOverride != null ? dataOverride : (data != null ? data : LocalDate.now());
        return new RegistrarMovimentacaoCommand(idProduto, tipo, quantidade, motivo, dataFinal);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public int getIdProduto() { return idProduto; }
    public void setIdProduto(int idProduto) { this.idProduto = idProduto; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
}
