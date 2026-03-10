package model;

import java.sql.Date;

public class MovimentacaoEstoque {
    private int id;
    private int idProduto;
    private String tipo; // entrada ou saida
    private int quantidade;
    private String motivo;
    private Date data;

    public MovimentacaoEstoque() {}

    public MovimentacaoEstoque(int id, int idProduto, String tipo, int quantidade, String motivo, Date data) {
        this.id = id;
        this.idProduto = idProduto;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.motivo = motivo;
        this.data = data;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdProduto() { return idProduto; }
    public void setIdProduto(int idProduto) { this.idProduto = idProduto; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }

    @Override
    public String toString() {
        return String.format("ID: %d | Produto ID: %d | Tipo: %s | Quantidade: %d | Motivo: %s | Data: %s",
                id, idProduto, tipo.toUpperCase(), quantidade, motivo, data.toString());
    }
}
