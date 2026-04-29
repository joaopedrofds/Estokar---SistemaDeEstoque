package com.studiomuda.estoque.domain.pedido.exceptions;

public class PedidoNaoEncontradoException extends RuntimeException {
    public PedidoNaoEncontradoException(int id) {
        super("Pedido não encontrado: " + id);
    }
}
