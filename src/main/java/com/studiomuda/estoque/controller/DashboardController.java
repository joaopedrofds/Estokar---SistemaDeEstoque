package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.application.dashboard.ConsultarDashboardUseCase;
import com.studiomuda.estoque.dto.DashboardDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    private final ConsultarDashboardUseCase consultarDashboard;

    public DashboardController(ConsultarDashboardUseCase consultarDashboard) {
        this.consultarDashboard = consultarDashboard;
    }

    @GetMapping
    public String dashboard(Model model) {
        return "dashboard";
    }

    @GetMapping("/api/categorias")
    @ResponseBody
    public List<Map<String, Object>> getCategorias() {
        return consultarDashboard.categorias();
    }

    @GetMapping("/api/clientes")
    @ResponseBody
    public List<Map<String, Object>> getClientes() {
        return consultarDashboard.clientesAtivos();
    }

    @GetMapping("/api/recentes-pedidos")
    @ResponseBody
    public List<DashboardDTO.PedidoResumo> getRecentesPedidos(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String statusPedido,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) Integer clienteId) {
        return consultarDashboard.recentesPedidos(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    @GetMapping("/api/top-clientes")
    @ResponseBody
    public List<DashboardDTO.ClienteAtivo> getTopClientes() {
        return consultarDashboard.topClientes();
    }

    @GetMapping("/api/clientes-ativos")
    @ResponseBody
    public List<DashboardDTO.ClienteAtivo> getClientesAtivos() {
        return consultarDashboard.topClientes();
    }

    @GetMapping("/api/top-produtos")
    @ResponseBody
    public List<Map<String, Object>> getTopProdutos(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String statusPedido,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) Integer clienteId) {
        return consultarDashboard.topProdutos(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    @GetMapping("/api/top-cidades")
    @ResponseBody
    public List<Map<String, Object>> getTopCidades() {
        return consultarDashboard.topCidades();
    }

    @GetMapping("/api/vendas-semana")
    @ResponseBody
    public List<Map<String, Object>> getVendasSemana() {
        return consultarDashboard.vendasSemana();
    }

    @GetMapping("/api/metricas-principais")
    @ResponseBody
    public Map<String, Object> getMetricasPrincipais(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String statusPedido,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) Integer clienteId) {
        return consultarDashboard.metricasPrincipais(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    @GetMapping("/api/evolucao-vendas")
    @ResponseBody
    public List<Map<String, Object>> getEvolucaoVendas(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String statusPedido,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) Integer clienteId) {
        return consultarDashboard.evolucaoVendas(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    @GetMapping("/api/produtos-baixo-estoque")
    @ResponseBody
    public List<Map<String, Object>> getProdutosBaixoEstoque(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String statusPedido,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) Integer clienteId) {
        return consultarDashboard.produtosBaixoEstoque(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    @GetMapping("/api/vendas-por-categoria")
    @ResponseBody
    public List<Map<String, Object>> getVendasPorCategoria(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String statusPedido,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) Integer clienteId) {
        return consultarDashboard.vendasPorCategoria(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    @GetMapping("/api/alertas")
    @ResponseBody
    public Map<String, Object> getAlertas(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String statusPedido,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) Integer clienteId) {
        return consultarDashboard.alertas(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    @GetMapping("/api/clientes-tipo")
    @ResponseBody
    public Map<String, Integer> getClientesTipo(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String statusPedido,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) Integer clienteId) {
        return consultarDashboard.clientesTipo(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    @GetMapping("/api/pedidos-status")
    @ResponseBody
    public List<Map<String, Object>> getPedidosStatus(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String statusPedido,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) Integer clienteId) {
        return consultarDashboard.pedidosStatus(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    @GetMapping("/api/entregas-prazo")
    @ResponseBody
    public Map<String, Integer> getEntregasPrazo(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String statusPedido,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) Integer clienteId) {
        return consultarDashboard.entregasPrazo(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    @GetMapping("/api/tempo-entrega-mes")
    @ResponseBody
    public List<Map<String, Object>> getTempoEntregaMes(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String statusPedido,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) Integer clienteId) {
        return consultarDashboard.tempoEntregaMes(dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    @GetMapping("/api/pedidos-atrasados")
    @ResponseBody
    public List<Map<String, Object>> getPedidosAtrasados() {
        return consultarDashboard.pedidosAtrasados();
    }

    @GetMapping("/api/pedidos-no-prazo")
    @ResponseBody
    public List<Map<String, Object>> getPedidosNoPrazo() {
        return consultarDashboard.pedidosNoPrazo();
    }

    @GetMapping("/api/movimentacoes-estoque")
    @ResponseBody
    public List<Map<String, Object>> getMovimentacoesEstoque() {
        return consultarDashboard.movimentacoesEstoque();
    }

    @GetMapping("/api/top-funcionarios")
    @ResponseBody
    public List<Map<String, Object>> getTopFuncionarios() {
        return consultarDashboard.topFuncionarios();
    }

    @GetMapping("/api/uso-cupons")
    @ResponseBody
    public List<Map<String, Object>> getUsoCupons() {
        return consultarDashboard.usoCupons();
    }

    @GetMapping("/api/produtos-rentaveis")
    @ResponseBody
    public List<Map<String, Object>> getProdutosRentaveis() {
        return consultarDashboard.produtosRentaveis();
    }

    @GetMapping("/api/sazonalidade-vendas")
    @ResponseBody
    public List<Map<String, Object>> getSazonalidadeVendas() {
        return consultarDashboard.sazonalidadeVendas();
    }

    @GetMapping("/api/clientes-geografico")
    @ResponseBody
    public List<Map<String, Object>> getClientesGeografico() {
        return consultarDashboard.clientesGeografico();
    }
}
