package com.studiomuda.estoque.application.dashboard.ports;

import com.studiomuda.estoque.dto.DashboardDTO;

import java.util.List;
import java.util.Map;

public interface DashboardQueryPort {
    List<Map<String, Object>> listarCategorias();

    List<Map<String, Object>> listarClientesAtivos();

    List<DashboardDTO.PedidoResumo> listarPedidosRecentes(String dataInicio, String dataFim, String statusPedido,
                                                          String categoria, String tipoCliente, Integer clienteId);

    List<DashboardDTO.ClienteAtivo> listarTopClientes();

    List<Map<String, Object>> listarTopProdutos(String dataInicio, String dataFim, String statusPedido,
                                                String categoria, String tipoCliente, Integer clienteId);

    List<Map<String, Object>> listarTopCidades();

    List<Map<String, Object>> listarVendasSemana();

    Map<String, Object> listarMetricasPrincipais(String dataInicio, String dataFim, String statusPedido,
                                                 String categoria, String tipoCliente, Integer clienteId);

    List<Map<String, Object>> listarEvolucaoVendas(String dataInicio, String dataFim, String statusPedido,
                                                   String categoria, String tipoCliente, Integer clienteId);

    List<Map<String, Object>> listarProdutosBaixoEstoque(String dataInicio, String dataFim, String statusPedido,
                                                         String categoria, String tipoCliente, Integer clienteId);

    List<Map<String, Object>> listarVendasPorCategoria(String dataInicio, String dataFim, String statusPedido,
                                                       String categoria, String tipoCliente, Integer clienteId);

    Map<String, Object> listarAlertas(String dataInicio, String dataFim, String statusPedido,
                                      String categoria, String tipoCliente, Integer clienteId);

    Map<String, Integer> listarClientesPorTipo(String dataInicio, String dataFim, String statusPedido,
                                               String categoria, String tipoCliente, Integer clienteId);

    List<Map<String, Object>> listarPedidosPorStatus(String dataInicio, String dataFim, String statusPedido,
                                                     String categoria, String tipoCliente, Integer clienteId);

    Map<String, Integer> listarEntregasPrazo(String dataInicio, String dataFim, String statusPedido,
                                             String categoria, String tipoCliente, Integer clienteId);

    List<Map<String, Object>> listarTempoEntregaMes(String dataInicio, String dataFim, String statusPedido,
                                                    String categoria, String tipoCliente, Integer clienteId);

    List<Map<String, Object>> listarPedidosAtrasados();

    List<Map<String, Object>> listarPedidosNoPrazo();

    List<Map<String, Object>> listarMovimentacoesEstoque();

    List<Map<String, Object>> listarTopFuncionarios();

    List<Map<String, Object>> listarUsoCupons();

    List<Map<String, Object>> listarProdutosRentaveis();

    List<Map<String, Object>> listarSazonalidadeVendas();

    List<Map<String, Object>> listarClientesGeografico();
}
