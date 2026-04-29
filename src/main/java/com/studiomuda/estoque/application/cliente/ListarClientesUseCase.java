package com.studiomuda.estoque.application.cliente;

import com.studiomuda.estoque.application.cliente.dto.ClienteComFrequencia;
import com.studiomuda.estoque.application.cliente.ports.PedidoQueryPort;
import com.studiomuda.estoque.domain.cliente.AnaliseFrequencia;
import com.studiomuda.estoque.domain.cliente.Cliente;
import com.studiomuda.estoque.domain.cliente.ClienteRepository;
import com.studiomuda.estoque.domain.cliente.TipoPessoa;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ListarClientesUseCase {
    private final ClienteRepository clienteRepository;
    private final PedidoQueryPort pedidoQuery;

    public ListarClientesUseCase(ClienteRepository clienteRepository, PedidoQueryPort pedidoQuery) {
        this.clienteRepository = clienteRepository;
        this.pedidoQuery = pedidoQuery;
    }

    public List<ClienteComFrequencia> listarTodos() {
        return enriquecer(clienteRepository.listarTodos());
    }

    public List<ClienteComFrequencia> buscarComFiltros(String nome, String tipo, String status) {
        TipoPessoa tipoPessoa = (tipo == null || tipo.trim().isEmpty()) ? null
                : TipoPessoa.fromCodigo(tipo);
        Boolean ativoFiltro = null;
        if (status != null && !status.trim().isEmpty()) {
            if ("ativo".equalsIgnoreCase(status)) ativoFiltro = Boolean.TRUE;
            else if ("inativo".equalsIgnoreCase(status)) ativoFiltro = Boolean.FALSE;
        }
        return enriquecer(clienteRepository.buscarComFiltros(nome, tipoPessoa, ativoFiltro));
    }

    private List<ClienteComFrequencia> enriquecer(List<Cliente> clientes) {
        List<ClienteComFrequencia> resultado = new ArrayList<>(clientes.size());
        LocalDate hoje = LocalDate.now();
        for (Cliente cliente : clientes) {
            List<LocalDate> datas = pedidoQuery.listarDatasCompraPorCliente(cliente.id());
            AnaliseFrequencia analise = AnaliseFrequencia.calcular(datas, hoje);
            resultado.add(new ClienteComFrequencia(cliente, analise));
        }
        return resultado;
    }
}
