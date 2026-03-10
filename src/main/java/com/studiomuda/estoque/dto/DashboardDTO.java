package com.studiomuda.estoque.dto;

import java.sql.Date;

public class DashboardDTO {
    // Pedido
    public static class PedidoResumo {
        public int id;
        public String clienteNome;
        public Date dataRequisicao;
        public Date dataEntrega;
        public double valorTotal;
        public String status;
    }

    // Cliente
    public static class ClienteResumo {
        public int id;
        public String nome;
        public int pedidos;
        public Date ultimaCompra;
    }

    // Cliente mais ativo (para dashboard)
    public static class ClienteAtivo {
        public String nome;
        public int pedidos;
        public double faturamento;
    }
}
