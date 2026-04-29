package com.studiomuda.estoque.domain.pedido;

public class PedidoComJoins {
    private final Pedido pedido;
    private final String clienteNome;
    private final String clienteCpfCnpj;
    private final String funcionarioNome;
    private final String funcionarioCargo;
    private final String cupomCodigo;

    public PedidoComJoins(Pedido pedido, String clienteNome, String clienteCpfCnpj,
                          String funcionarioNome, String funcionarioCargo, String cupomCodigo) {
        this.pedido = pedido;
        this.clienteNome = clienteNome;
        this.clienteCpfCnpj = clienteCpfCnpj;
        this.funcionarioNome = funcionarioNome;
        this.funcionarioCargo = funcionarioCargo;
        this.cupomCodigo = cupomCodigo;
    }

    public Pedido pedido() { return pedido; }
    public String clienteNome() { return clienteNome; }
    public String clienteCpfCnpj() { return clienteCpfCnpj; }
    public String funcionarioNome() { return funcionarioNome; }
    public String funcionarioCargo() { return funcionarioCargo; }
    public String cupomCodigo() { return cupomCodigo; }
}
