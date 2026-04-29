package com.studiomuda.estoque.application.dashboard;

import com.studiomuda.estoque.application.dashboard.ports.DashboardQueryPort;
import com.studiomuda.estoque.dto.DashboardDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ConsultarDashboardUseCase {
    private final DashboardQueryPort dashboardQuery;

    public ConsultarDashboardUseCase(DashboardQueryPort dashboardQuery) {
        this.dashboardQuery = dashboardQuery;
    }

    public List<Map<String, Object>> categorias() { return dashboardQuery.listarCategorias(); }

    public List<Map<String, Object>> clientesAtivos() { return dashboardQuery.listarClientesAtivos(); }

    public List<DashboardDTO.PedidoResumo> recentesPedidos(String dataInicio, String dataFim, String statusPedido,
                                                           String categoria, String tipoCliente, Integer clienteId) {
        return dashboardQuery.listarPedidosRecentes(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    public List<DashboardDTO.ClienteAtivo> topClientes() { return dashboardQuery.listarTopClientes(); }

    public List<Map<String, Object>> topProdutos(String dataInicio, String dataFim, String statusPedido,
                                                 String categoria, String tipoCliente, Integer clienteId) {
        return dashboardQuery.listarTopProdutos(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    public List<Map<String, Object>> topCidades() { return dashboardQuery.listarTopCidades(); }

    public List<Map<String, Object>> vendasSemana() { return dashboardQuery.listarVendasSemana(); }

    public Map<String, Object> metricasPrincipais(String dataInicio, String dataFim, String statusPedido,
                                                  String categoria, String tipoCliente, Integer clienteId) {
        return dashboardQuery.listarMetricasPrincipais(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    public List<Map<String, Object>> evolucaoVendas(String dataInicio, String dataFim, String statusPedido,
                                                    String categoria, String tipoCliente, Integer clienteId) {
        return dashboardQuery.listarEvolucaoVendas(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    public List<Map<String, Object>> produtosBaixoEstoque(String dataInicio, String dataFim, String statusPedido,
                                                          String categoria, String tipoCliente, Integer clienteId) {
        return dashboardQuery.listarProdutosBaixoEstoque(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    public List<Map<String, Object>> vendasPorCategoria(String dataInicio, String dataFim, String statusPedido,
                                                        String categoria, String tipoCliente, Integer clienteId) {
        return dashboardQuery.listarVendasPorCategoria(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    public Map<String, Object> alertas(String dataInicio, String dataFim, String statusPedido,
                                       String categoria, String tipoCliente, Integer clienteId) {
        return dashboardQuery.listarAlertas(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    public Map<String, Integer> clientesTipo(String dataInicio, String dataFim, String statusPedido,
                                             String categoria, String tipoCliente, Integer clienteId) {
        return dashboardQuery.listarClientesPorTipo(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    public List<Map<String, Object>> pedidosStatus(String dataInicio, String dataFim, String statusPedido,
                                                   String categoria, String tipoCliente, Integer clienteId) {
        return dashboardQuery.listarPedidosPorStatus(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    public Map<String, Integer> entregasPrazo(String dataInicio, String dataFim, String statusPedido,
                                              String categoria, String tipoCliente, Integer clienteId) {
        return dashboardQuery.listarEntregasPrazo(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    public List<Map<String, Object>> tempoEntregaMes(String dataInicio, String dataFim, String statusPedido,
                                                     String categoria, String tipoCliente, Integer clienteId) {
        return dashboardQuery.listarTempoEntregaMes(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    public List<Map<String, Object>> pedidosAtrasados() { return dashboardQuery.listarPedidosAtrasados(); }

    public List<Map<String, Object>> pedidosNoPrazo() { return dashboardQuery.listarPedidosNoPrazo(); }

    public List<Map<String, Object>> movimentacoesEstoque() { return dashboardQuery.listarMovimentacoesEstoque(); }

    public List<Map<String, Object>> topFuncionarios() { return dashboardQuery.listarTopFuncionarios(); }

    public List<Map<String, Object>> usoCupons() { return dashboardQuery.listarUsoCupons(); }

    public List<Map<String, Object>> produtosRentaveis() { return dashboardQuery.listarProdutosRentaveis(); }

    public List<Map<String, Object>> sazonalidadeVendas() { return dashboardQuery.listarSazonalidadeVendas(); }

    public List<Map<String, Object>> clientesGeografico() { return dashboardQuery.listarClientesGeografico(); }
}
