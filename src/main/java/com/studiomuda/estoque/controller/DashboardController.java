package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.dto.DashboardDTO;
import com.studiomuda.estoque.service.DashboardConsultaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    private final DashboardConsultaService dashboardConsultaService;

    public DashboardController(DashboardConsultaService dashboardConsultaService) {
        this.dashboardConsultaService = dashboardConsultaService;
    }

    // Página principal
    @GetMapping
    public String dashboard(Model model) {
        return "dashboard";
    }

    // Endpoint para categorias (tipos de produtos)
    @GetMapping("/api/categorias")
    @ResponseBody
    public List<Map<String, Object>> getCategorias() {
        return dashboardConsultaService.listarCategorias();
    }

    // Endpoint para clientes
    @GetMapping("/api/clientes")
    @ResponseBody
    public List<Map<String, Object>> getClientes() {
        return dashboardConsultaService.listarClientes();
    }

    // Endpoint para pedidos recentes com filtros
    @GetMapping("/api/recentes-pedidos")
    @ResponseBody
    public List<DashboardDTO.PedidoResumo> getRecentesPedidos(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String statusPedido,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) Integer clienteId) {

        return dashboardConsultaService.listarRecentesPedidos(
                dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    @GetMapping("/api/top-clientes")
    @ResponseBody
    public List<DashboardDTO.ClienteAtivo> getTopClientes() {
        return dashboardConsultaService.listarTopClientes();
    }

    @GetMapping("/api/clientes-ativos")
    @ResponseBody
    public List<DashboardDTO.ClienteAtivo> getClientesAtivos() {
        return dashboardConsultaService.listarTopClientes();
    }

    // Endpoint para top produtos
    @GetMapping("/api/top-produtos")
    @ResponseBody
    public List<Map<String, Object>> getTopProdutos(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String statusPedido,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) Integer clienteId) {

        return dashboardConsultaService.listarTopProdutos(
                dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    // Endpoint para top cidades
    @GetMapping("/api/top-cidades")
    @ResponseBody
    public List<Map<String, Object>> getTopCidades() {
        return dashboardConsultaService.listarTopCidades();
    }

    // Endpoint para vendas por dia da semana
    @GetMapping("/api/vendas-semana")
    @ResponseBody
    public List<Map<String, Object>> getVendasSemana() {
        return dashboardConsultaService.listarVendasSemana();
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

        return dashboardConsultaService.obterMetricasPrincipais(categoria, tipoCliente);
    }

    // Endpoint para evolução de vendas (últimos 12 meses)
    @GetMapping("/api/evolucao-vendas")
    @ResponseBody
    public List<Map<String, Object>> getEvolucaoVendas(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String statusPedido,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) Integer clienteId) {

        return dashboardConsultaService.listarEvolucaoVendas(
                dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    // Endpoint para produtos com baixo estoque
    @GetMapping("/api/produtos-baixo-estoque")
    @ResponseBody
    public List<Map<String, Object>> getProdutosBaixoEstoque(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String statusPedido,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) Integer clienteId) {

        return dashboardConsultaService.listarProdutosBaixoEstoque(
                dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    // Endpoint para vendas por categoria
    @GetMapping("/api/vendas-por-categoria")
    @ResponseBody
    public List<Map<String, Object>> getVendasPorCategoria(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String statusPedido,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) Integer clienteId) {

        return dashboardConsultaService.listarVendasPorCategoria(
                dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    // Endpoint para alertas e notificações
    @GetMapping("/api/alertas")
    @ResponseBody
    public Map<String, Object> getAlertas(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String statusPedido,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) Integer clienteId) {
        return dashboardConsultaService.obterAlertas(
                dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    private String normalizarTipoCliente(String tipoCliente) {
        if (tipoCliente == null || tipoCliente.isEmpty()) {
            return null;
        }
        if ("PESSOA_FISICA".equals(tipoCliente)) {
            return "PF";
        }
        if ("PESSOA_JURIDICA".equals(tipoCliente)) {
            return "PJ";
        }
        return tipoCliente;
    }

    // Endpoint para distribuição de clientes PF/PJ
    @GetMapping("/api/clientes-tipo")
    @ResponseBody
    public Map<String, Integer> getClientesTipo(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String statusPedido,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) Integer clienteId) {
        return dashboardConsultaService.obterClientesTipo(
                dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
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
        return dashboardConsultaService.listarPedidosStatus(
                dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    // Endpoint para entregas no prazo vs atrasadas (simulado)
    @GetMapping("/api/entregas-prazo")
    @ResponseBody
    public Map<String, Integer> getEntregasPrazo(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String statusPedido,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) Integer clienteId) {
        return dashboardConsultaService.obterEntregasPrazo(
                dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    // Endpoint para tempo médio de entrega por mês
    @GetMapping("/api/tempo-entrega-mes")
    @ResponseBody
    public List<Map<String, Object>> getTempoEntregaMes(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String statusPedido,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) Integer clienteId) {

        return dashboardConsultaService.listarTempoEntregaMes(
                dataInicio, dataFim, statusPedido, categoria, tipoCliente, clienteId);
    }

    // Endpoint para listar pedidos atrasados (simulado - mais de 7 dias)
    @GetMapping("/api/pedidos-atrasados")
    @ResponseBody
    public List<Map<String, Object>> getPedidosAtrasados() {
        return dashboardConsultaService.listarPedidosAtrasados();
    }

    // Endpoint para listar pedidos no prazo (simulado - até 7 dias)
    @GetMapping("/api/pedidos-no-prazo")
    @ResponseBody
    public List<Map<String, Object>> getPedidosNoPrazo() {
        return dashboardConsultaService.listarPedidosNoPrazo();
    }

    // Endpoint para movimentações de estoque recentes
    @GetMapping("/api/movimentacoes-estoque")
    @ResponseBody
    public List<Map<String, Object>> getMovimentacoesEstoque() {
        return dashboardConsultaService.listarMovimentacoesEstoque();
    }

    // Endpoint para top funcionários por vendas
    @GetMapping("/api/top-funcionarios")
    @ResponseBody
    public List<Map<String, Object>> getTopFuncionarios() {
        return dashboardConsultaService.listarTopFuncionarios();
    }

    // Endpoint para uso de cupons
    @GetMapping("/api/uso-cupons")
    @ResponseBody
    public List<Map<String, Object>> getUsoCupons() {
        return dashboardConsultaService.listarUsoCupons();
    }

    // Endpoint para produtos mais rentáveis
    @GetMapping("/api/produtos-rentaveis")
    @ResponseBody
    public List<Map<String, Object>> getProdutosRentaveis() {
        return dashboardConsultaService.listarProdutosRentaveis();
    }

    // Endpoint para análise de sazonalidade
    @GetMapping("/api/sazonalidade-vendas")
    @ResponseBody
    public List<Map<String, Object>> getSazonalidadeVendas() {
        return dashboardConsultaService.listarSazonalidadeVendas();
    }

    // Endpoint para análise geográfica de clientes
    @GetMapping("/api/clientes-geografico")
    @ResponseBody
    public List<Map<String, Object>> getClientesGeografico() {
        return dashboardConsultaService.listarClientesGeografico();
    }
}
