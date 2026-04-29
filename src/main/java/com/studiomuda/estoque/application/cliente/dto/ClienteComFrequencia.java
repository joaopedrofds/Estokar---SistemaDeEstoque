package com.studiomuda.estoque.application.cliente.dto;

import com.studiomuda.estoque.domain.cliente.AnaliseFrequencia;
import com.studiomuda.estoque.domain.cliente.Cliente;

public class ClienteComFrequencia {
    private final Cliente cliente;
    private final AnaliseFrequencia analise;

    public ClienteComFrequencia(Cliente cliente, AnaliseFrequencia analise) {
        this.cliente = cliente;
        this.analise = analise;
    }

    public Cliente cliente() { return cliente; }
    public AnaliseFrequencia analise() { return analise; }
}
