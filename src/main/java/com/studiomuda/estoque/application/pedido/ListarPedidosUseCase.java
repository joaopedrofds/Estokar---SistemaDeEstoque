package com.studiomuda.estoque.application.pedido;

import com.studiomuda.estoque.domain.cliente.Cliente;
import com.studiomuda.estoque.domain.cliente.ClienteRepository;
import com.studiomuda.estoque.domain.cliente.CpfCnpj;
import com.studiomuda.estoque.domain.cliente.TipoPessoa;
import com.studiomuda.estoque.domain.pedido.PedidoComJoins;
import com.studiomuda.estoque.domain.pedido.PedidoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ListarPedidosUseCase {
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;

    public ListarPedidosUseCase(PedidoRepository pedidoRepository, ClienteRepository clienteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
    }

    public List<PedidoComJoins> listarTodos() {
        return pedidoRepository.listarTodos();
    }

    public ResultadoBuscaPorCpf listarPorCpfCnpj(String cpfCnpj) {
        String digitos = cpfCnpj.replaceAll("[^0-9]", "");
        Optional<Cliente> cliente = tentarBuscarCliente(digitos);
        if (cliente.isEmpty()) {
            return new ResultadoBuscaPorCpf(Collections.emptyList(), null);
        }
        return new ResultadoBuscaPorCpf(pedidoRepository.listarPorCliente(cliente.get().id()), cliente.get());
    }

    public List<PedidoComJoins> buscarComFiltros(String cpfCnpj, String status, LocalDate dataInicio,
                                                   LocalDate dataFim, Integer funcionarioId,
                                                   Integer clienteId, Integer cupomId) {
        return pedidoRepository.buscarComFiltros(cpfCnpj, status, dataInicio, dataFim,
                funcionarioId, clienteId, cupomId);
    }

    private Optional<Cliente> tentarBuscarCliente(String digitos) {
        try {
            TipoPessoa tipo = digitos.length() == 14 ? TipoPessoa.PJ : TipoPessoa.PF;
            return clienteRepository.buscarPorCpfCnpj(CpfCnpj.of(digitos, tipo));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public static class ResultadoBuscaPorCpf {
        private final List<PedidoComJoins> pedidos;
        private final Cliente cliente;

        public ResultadoBuscaPorCpf(List<PedidoComJoins> pedidos, Cliente cliente) {
            this.pedidos = pedidos;
            this.cliente = cliente;
        }

        public List<PedidoComJoins> pedidos() { return pedidos; }
        public Cliente cliente() { return cliente; }
    }
}
