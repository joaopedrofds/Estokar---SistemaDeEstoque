package com.studiomuda.estoque.infrastructure.persistence.cliente;

import com.studiomuda.estoque.application.cliente.ports.PedidoQueryPort;
import com.studiomuda.estoque.dao.PedidoDAO;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
public class PedidoQueryAdapter implements PedidoQueryPort {
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    @Override
    public List<LocalDate> listarDatasCompraPorCliente(int clienteId) {
        try {
            return pedidoDAO.listarDatasCompraPorCliente(clienteId);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar datas de compra: " + e.getMessage(), e);
        }
    }
}
