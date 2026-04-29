package com.studiomuda.estoque.application.cliente.ports;

import java.time.LocalDate;
import java.util.List;

public interface PedidoQueryPort {
    List<LocalDate> listarDatasCompraPorCliente(int clienteId);
}
