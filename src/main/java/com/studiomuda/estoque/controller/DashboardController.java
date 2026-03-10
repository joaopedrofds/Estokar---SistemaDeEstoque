package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.dao.ItemPedidoDAO;
import com.studiomuda.estoque.model.ItemPedido;
import com.studiomuda.estoque.dto.DashboardDTO;
import com.studiomuda.estoque.conexao.Conexao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.*;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    private final ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO();

    // Página principal
    @GetMapping
    public String dashboard(Model model) {
        return "dashboard";
    }

    // Endpoint para categorias (tipos de produtos)
    @GetMapping("/api/categorias")
    @ResponseBody
    public List<Map<String, Object>> getCategorias() {
        List<Map<String, Object>> categorias = new ArrayList<>();
        String sql = "SELECT DISTINCT tipo FROM produto WHERE tipo IS NOT NULL AND tipo != '' ORDER BY tipo";

        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> categoria = new HashMap<>();
                categoria.put("id", rs.getString("tipo"));
                categoria.put("nome", rs.getString("tipo"));
                categorias.add(categoria);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categorias;
    }

    // Endpoint para clientes
    @GetMapping("/api/clientes")
    @ResponseBody
    public List<Map<String, Object>> getClientes() {
        List<Map<String, Object>> clientes = new ArrayList<>();
        String sql = "SELECT id, nome FROM cliente WHERE ativo = true ORDER BY nome";

        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> cliente = new HashMap<>();
                cliente.put("id", rs.getInt("id"));
                cliente.put("nome", rs.getString("nome"));
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
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

        List<DashboardDTO.PedidoResumo> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT p.id, p.data_requisicao, p.data_entrega, c.nome as cliente_nome " +
                        "FROM pedido p " +
                        "LEFT JOIN cliente c ON p.cliente_id = c.id " +
                        "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        // Aplicar filtros
        if (dataInicio != null && !dataInicio.isEmpty()) {
            sql.append("AND p.data_requisicao >= ? ");
            params.add(java.sql.Date.valueOf(dataInicio));
        }

        if (dataFim != null && !dataFim.isEmpty()) {
            sql.append("AND p.data_requisicao <= ? ");
            params.add(java.sql.Date.valueOf(dataFim));
        }

        if (tipoCliente != null && !tipoCliente.isEmpty()) {
            String tipo = tipoCliente.equals("PESSOA_FISICA") ? "PF"
                    : tipoCliente.equals("PESSOA_JURIDICA") ? "PJ" : tipoCliente;
            sql.append("AND c.tipo = ? ");
            params.add(tipo);
        }

        if (clienteId != null && clienteId > 0) {
            sql.append("AND p.cliente_id = ? ");
            params.add(clienteId);
        }

        // Se categoria foi especificada, filtrar por produtos na categoria
        if (categoria != null && !categoria.isEmpty()) {
            sql.append(
                    "AND EXISTS (SELECT 1 FROM item_pedido ip JOIN produto prod ON ip.id_produto = prod.id WHERE ip.id_pedido = p.id AND prod.tipo = ?) ");
            params.add(categoria);
        }

        sql.append("ORDER BY p.data_requisicao DESC LIMIT 10");

        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                DashboardDTO.PedidoResumo dto = new DashboardDTO.PedidoResumo();
                dto.id = rs.getInt("id");
                dto.clienteNome = rs.getString("cliente_nome");
                dto.dataRequisicao = rs.getDate("data_requisicao");
                dto.dataEntrega = rs.getDate("data_entrega");

                // Calcular valor total
                double valorTotal = 0.0;
                List<ItemPedido> itens = itemPedidoDAO.listarPorPedido(dto.id);
                for (ItemPedido item : itens) {
                    valorTotal += item.getProdutoValor() * item.getQuantidade();
                }
                dto.valorTotal = valorTotal;
                lista.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @GetMapping("/api/top-clientes")
    @ResponseBody
    public List<DashboardDTO.ClienteAtivo> getTopClientes() {
        List<DashboardDTO.ClienteAtivo> lista = new ArrayList<>();
        String sql = "SELECT c.nome, " +
                "COUNT(DISTINCT p.id) as pedidos, " +
                "COALESCE(SUM(ip.quantidade * prod.valor), 0) as faturamento " +
                "FROM cliente c " +
                "LEFT JOIN pedido p ON c.id = p.cliente_id " +
                "LEFT JOIN item_pedido ip ON p.id = ip.id_pedido " +
                "LEFT JOIN produto prod ON ip.id_produto = prod.id " +
                "WHERE c.ativo = true AND p.id IS NOT NULL " +
                "GROUP BY c.id, c.nome " +
                "ORDER BY pedidos DESC, faturamento DESC " +
                "LIMIT 10";
        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                DashboardDTO.ClienteAtivo dto = new DashboardDTO.ClienteAtivo();
                dto.nome = rs.getString("nome");
                dto.pedidos = rs.getInt("pedidos");
                dto.faturamento = rs.getDouble("faturamento");
                lista.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @GetMapping("/api/clientes-ativos")
    @ResponseBody
    public List<DashboardDTO.ClienteAtivo> getClientesAtivos() {
        return getTopClientes();
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

        List<Map<String, Object>> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT p.nome as produtoNome, SUM(ip.quantidade) as quantidadeVendida " +
                        "FROM item_pedido ip " +
                        "JOIN produto p ON ip.id_produto = p.id " +
                        "JOIN pedido ped ON ip.id_pedido = ped.id ");

        List<Object> params = new ArrayList<>();

        if (tipoCliente != null && !tipoCliente.isEmpty()) {
            sql.append("JOIN cliente c ON ped.cliente_id = c.id ");
        }

        sql.append("WHERE 1=1 ");

        if (dataInicio != null && !dataInicio.isEmpty()) {
            sql.append("AND ped.data_requisicao >= ? ");
            params.add(java.sql.Date.valueOf(dataInicio));
        }
        if (dataFim != null && !dataFim.isEmpty()) {
            sql.append("AND ped.data_requisicao <= ? ");
            params.add(java.sql.Date.valueOf(dataFim));
        }
        if (tipoCliente != null && !tipoCliente.isEmpty()) {
            String tipo = tipoCliente.equals("PESSOA_FISICA") ? "PF"
                    : tipoCliente.equals("PESSOA_JURIDICA") ? "PJ" : tipoCliente;
            sql.append("AND c.tipo = ? ");
            params.add(tipo);
        }
        if (clienteId != null && clienteId > 0) {
            sql.append("AND ped.cliente_id = ? ");
            params.add(clienteId);
        }
        if (categoria != null && !categoria.isEmpty()) {
            sql.append("AND p.tipo = ? ");
            params.add(categoria);
        }

        sql.append("GROUP BY p.nome ORDER BY quantidadeVendida DESC LIMIT 10");

        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("produtoNome", rs.getString("produtoNome"));
                map.put("quantidadeVendida", rs.getInt("quantidadeVendida"));
                lista.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Endpoint para top cidades
    @GetMapping("/api/top-cidades")
    @ResponseBody
    public List<Map<String, Object>> getTopCidades() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT c.cidade, COUNT(p.id) as vendas " +
                "FROM cliente c " +
                "JOIN pedido p ON c.id = p.cliente_id " +
                "GROUP BY c.cidade " +
                "ORDER BY vendas DESC " +
                "LIMIT 10";
        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("cidade", rs.getString("cidade"));
                map.put("vendas", rs.getInt("vendas"));
                lista.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Endpoint para vendas por dia da semana
    @GetMapping("/api/vendas-semana")
    @ResponseBody
    public List<Map<String, Object>> getVendasSemana() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT DAYNAME(data_requisicao) as dia, " +
                "COUNT(*) as pedidos, " +
                "COALESCE(SUM(prod.valor * ip.quantidade), 0) as faturamento " +
                "FROM pedido p " +
                "LEFT JOIN item_pedido ip ON p.id = ip.id_pedido " +
                "LEFT JOIN produto prod ON ip.id_produto = prod.id " +
                "GROUP BY DAYOFWEEK(data_requisicao), DAYNAME(data_requisicao) " +
                "ORDER BY DAYOFWEEK(data_requisicao)";

        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("dia", rs.getString("dia"));
                map.put("pedidos", rs.getInt("pedidos"));
                map.put("faturamento", rs.getDouble("faturamento"));
                lista.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
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

        Map<String, Object> metricas = new HashMap<>();
        try (Connection conn = Conexao.getConnection()) {
            // Total de pedidos ABSOLUTO (fixo, sem filtro)
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) as total FROM pedido")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next())
                    metricas.put("totalPedidos", rs.getInt("total"));
            }

            // Total de clientes ativos (com filtro de tipo se aplicável)
            StringBuilder sqlClientes = new StringBuilder("SELECT COUNT(*) as total FROM cliente WHERE ativo = 1 ");
            List<Object> paramsClientes = new ArrayList<>();
            if (tipoCliente != null && !tipoCliente.isEmpty()) {
                String tipo = tipoCliente.equals("PESSOA_FISICA") ? "PF"
                        : tipoCliente.equals("PESSOA_JURIDICA") ? "PJ" : tipoCliente;
                sqlClientes.append("AND tipo = ? ");
                paramsClientes.add(tipo);
            }
            try (PreparedStatement stmt = conn.prepareStatement(sqlClientes.toString())) {
                for (int i = 0; i < paramsClientes.size(); i++) {
                    stmt.setObject(i + 1, paramsClientes.get(i));
                }
                ResultSet rs = stmt.executeQuery();
                if (rs.next())
                    metricas.put("clientesAtivos", rs.getInt("total"));
            }

            // Total de funcionários ativos (não filtrado)
            try (PreparedStatement stmt = conn
                    .prepareStatement("SELECT COUNT(*) as total FROM funcionario WHERE ativo = 1");
                    ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    metricas.put("funcionariosAtivos", rs.getInt("total"));
            }

            // Total de produtos cadastrados (com filtro de categoria se aplicável)
            StringBuilder sqlProdutos = new StringBuilder("SELECT COUNT(*) as total FROM produto WHERE 1=1 ");
            List<Object> paramsProdutos = new ArrayList<>();
            if (categoria != null && !categoria.isEmpty()) {
                sqlProdutos.append("AND tipo = ? ");
                paramsProdutos.add(categoria);
            }
            try (PreparedStatement stmt = conn.prepareStatement(sqlProdutos.toString())) {
                for (int i = 0; i < paramsProdutos.size(); i++) {
                    stmt.setObject(i + 1, paramsProdutos.get(i));
                }
                ResultSet rs = stmt.executeQuery();
                if (rs.next())
                    metricas.put("totalProdutos", rs.getInt("total"));
            }

            // Total de produtos em estoque (>0) com filtro de categoria se aplicável
            StringBuilder sqlEstoque = new StringBuilder("SELECT COUNT(*) as total FROM produto WHERE quantidade > 0 ");
            List<Object> paramsEstoque = new ArrayList<>();
            if (categoria != null && !categoria.isEmpty()) {
                sqlEstoque.append("AND tipo = ? ");
                paramsEstoque.add(categoria);
            }
            try (PreparedStatement stmt = conn.prepareStatement(sqlEstoque.toString())) {
                for (int i = 0; i < paramsEstoque.size(); i++) {
                    stmt.setObject(i + 1, paramsEstoque.get(i));
                }
                ResultSet rs = stmt.executeQuery();
                if (rs.next())
                    metricas.put("produtosEstoque", rs.getInt("total"));
            }

            // Total de cupons cadastrados
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) as total FROM cupom");
                    ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    metricas.put("totalCupons", rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return metricas;
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

        List<Map<String, Object>> evolucao = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        "YEAR(p.data_requisicao) as ano, " +
                        "MONTH(p.data_requisicao) as mes, " +
                        "MONTHNAME(p.data_requisicao) as nome_mes, " +
                        "COUNT(DISTINCT p.id) as pedidos, " +
                        "COALESCE(SUM(prod.valor * ip.quantidade), 0) as faturamento " +
                        "FROM pedido p " +
                        "LEFT JOIN item_pedido ip ON p.id = ip.id_pedido " +
                        "LEFT JOIN produto prod ON ip.id_produto = prod.id ");

        List<Object> params = new ArrayList<>();

        if (tipoCliente != null && !tipoCliente.isEmpty()) {
            sql.append("LEFT JOIN cliente c ON p.cliente_id = c.id ");
        }

        sql.append("WHERE p.data_requisicao >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH) ");

        // Aplicar filtros de data personalizados se fornecidos
        if (dataInicio != null && !dataInicio.isEmpty()) {
            sql.append("AND p.data_requisicao >= ? ");
            params.add(java.sql.Date.valueOf(dataInicio));
        }
        if (dataFim != null && !dataFim.isEmpty()) {
            sql.append("AND p.data_requisicao <= ? ");
            params.add(java.sql.Date.valueOf(dataFim));
        }
        if (tipoCliente != null && !tipoCliente.isEmpty()) {
            String tipo = tipoCliente.equals("PESSOA_FISICA") ? "PF"
                    : tipoCliente.equals("PESSOA_JURIDICA") ? "PJ" : tipoCliente;
            sql.append("AND c.tipo = ? ");
            params.add(tipo);
        }
        if (clienteId != null && clienteId > 0) {
            sql.append("AND p.cliente_id = ? ");
            params.add(clienteId);
        }
        if (categoria != null && !categoria.isEmpty()) {
            sql.append("AND prod.tipo = ? ");
            params.add(categoria);
        }

        sql.append("GROUP BY YEAR(p.data_requisicao), MONTH(p.data_requisicao), MONTHNAME(p.data_requisicao) ");
        sql.append("ORDER BY ano, mes");

        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("ano", rs.getInt("ano"));
                map.put("mes", rs.getInt("mes"));
                map.put("nomeMes", rs.getString("nome_mes"));
                map.put("pedidos", rs.getInt("pedidos"));
                map.put("faturamento", rs.getDouble("faturamento"));
                evolucao.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evolucao;
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

        List<Map<String, Object>> produtos = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT DISTINCT p.id, p.nome, p.quantidade, p.valor, p.tipo, " +
                "CASE " +
                "WHEN p.quantidade = 0 THEN 'ESGOTADO' " +
                "WHEN p.quantidade <= 5 THEN 'CRITICO' " +
                "WHEN p.quantidade <= 10 THEN 'BAIXO' " +
                "ELSE 'NORMAL' " +
                "END as status_estoque " +
                "FROM produto p " +
                "WHERE p.quantidade <= 10 ");

        List<Object> params = new ArrayList<>();

        // Filtro por categoria
        if (categoria != null && !categoria.isEmpty()) {
            sql.append("AND p.tipo = ? ");
            params.add(categoria);
        }

        // Se há filtros relacionados a pedidos, fazemos JOIN
        if ((dataInicio != null && !dataInicio.isEmpty()) ||
                (dataFim != null && !dataFim.isEmpty()) ||
                (tipoCliente != null && !tipoCliente.isEmpty()) ||
                (clienteId != null)) {

            // Modificar query para incluir produtos que foram vendidos dentro dos filtros
            sql = new StringBuilder("SELECT DISTINCT p.id, p.nome, p.quantidade, p.valor, p.tipo, " +
                    "CASE " +
                    "WHEN p.quantidade = 0 THEN 'ESGOTADO' " +
                    "WHEN p.quantidade <= 5 THEN 'CRITICO' " +
                    "WHEN p.quantidade <= 10 THEN 'BAIXO' " +
                    "ELSE 'NORMAL' " +
                    "END as status_estoque " +
                    "FROM produto p " +
                    "LEFT JOIN item_pedido ip ON p.id = ip.id_produto " +
                    "LEFT JOIN pedido ped ON ip.id_pedido = ped.id " +
                    "LEFT JOIN cliente c ON ped.cliente_id = c.id " +
                    "WHERE p.quantidade <= 10 ");

            // Reparametrizar com categoria se existe
            params.clear();
            if (categoria != null && !categoria.isEmpty()) {
                sql.append("AND p.tipo = ? ");
                params.add(categoria);
            }

            // Filtro por data de início
            if (dataInicio != null && !dataInicio.isEmpty()) {
                sql.append("AND (ped.data_requisicao IS NULL OR ped.data_requisicao >= ?) ");
                params.add(dataInicio);
            }

            // Filtro por data de fim
            if (dataFim != null && !dataFim.isEmpty()) {
                sql.append("AND (ped.data_requisicao IS NULL OR ped.data_requisicao <= ?) ");
                params.add(dataFim);
            }

            // Filtro por tipo de cliente
            if (tipoCliente != null && !tipoCliente.isEmpty()) {
                sql.append("AND (c.tipo IS NULL OR c.tipo = ?) ");
                params.add(tipoCliente);
            }

            // Filtro por cliente específico
            if (clienteId != null) {
                sql.append("AND (ped.cliente_id IS NULL OR ped.cliente_id = ?) ");
                params.add(clienteId);
            }
        }

        sql.append("ORDER BY p.quantidade ASC");

        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", rs.getInt("id"));
                    map.put("nome", rs.getString("nome"));
                    map.put("quantidade", rs.getInt("quantidade"));
                    map.put("preco", rs.getDouble("valor"));
                    map.put("categoria", rs.getString("tipo"));
                    map.put("statusEstoque", rs.getString("status_estoque"));
                    produtos.add(map);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produtos;
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

        List<Map<String, Object>> vendas = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        "COALESCE(p.tipo, 'Sem Categoria') as categoria, " +
                        "COUNT(ip.id) as itens_vendidos, " +
                        "SUM(ip.quantidade) as quantidade_total, " +
                        "SUM(p.valor * ip.quantidade) as faturamento " +
                        "FROM produto p " +
                        "JOIN item_pedido ip ON p.id = ip.id_produto " +
                        "JOIN pedido ped ON ip.id_pedido = ped.id ");

        List<Object> params = new ArrayList<>();

        if (tipoCliente != null && !tipoCliente.isEmpty()) {
            sql.append("JOIN cliente c ON ped.cliente_id = c.id ");
        }

        sql.append("WHERE 1=1 ");

        if (dataInicio != null && !dataInicio.isEmpty()) {
            sql.append("AND ped.data_requisicao >= ? ");
            params.add(java.sql.Date.valueOf(dataInicio));
        }
        if (dataFim != null && !dataFim.isEmpty()) {
            sql.append("AND ped.data_requisicao <= ? ");
            params.add(java.sql.Date.valueOf(dataFim));
        }
        if (tipoCliente != null && !tipoCliente.isEmpty()) {
            String tipo = tipoCliente.equals("PESSOA_FISICA") ? "PF"
                    : tipoCliente.equals("PESSOA_JURIDICA") ? "PJ" : tipoCliente;
            sql.append("AND c.tipo = ? ");
            params.add(tipo);
        }
        if (clienteId != null && clienteId > 0) {
            sql.append("AND ped.cliente_id = ? ");
            params.add(clienteId);
        }
        if (categoria != null && !categoria.isEmpty()) {
            sql.append("AND p.tipo = ? ");
            params.add(categoria);
        }

        sql.append("GROUP BY p.tipo ORDER BY faturamento DESC");

        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("categoria", rs.getString("categoria"));
                map.put("itensVendidos", rs.getInt("itens_vendidos"));
                map.put("quantidadeTotal", rs.getInt("quantidade_total"));
                map.put("faturamento", rs.getDouble("faturamento"));
                vendas.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vendas;
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

        Map<String, Object> alertas = new HashMap<>();
        try {
            // Produtos em falta (com filtro de categoria se aplicável)
            StringBuilder sqlProdutosFalta = new StringBuilder(
                    "SELECT COUNT(*) as total FROM produto WHERE quantidade = 0 ");
            List<Object> paramsProdutosFalta = new ArrayList<>();

            if (categoria != null && !categoria.isEmpty()) {
                sqlProdutosFalta.append("AND tipo = ? ");
                paramsProdutosFalta.add(categoria);
            }

            try (Connection conn = Conexao.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sqlProdutosFalta.toString())) {
                for (int i = 0; i < paramsProdutosFalta.size(); i++) {
                    stmt.setObject(i + 1, paramsProdutosFalta.get(i));
                }
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    alertas.put("produtosFalta", rs.getInt("total"));
                }
            }

            // Pedidos pendentes com filtros
            StringBuilder sqlPedidosPendentes = new StringBuilder(
                    "SELECT COUNT(*) as total FROM pedido p ");
            List<Object> paramsPendentes = new ArrayList<>();

            if (tipoCliente != null && !tipoCliente.isEmpty()) {
                sqlPedidosPendentes.append("LEFT JOIN cliente c ON p.cliente_id = c.id ");
            }
            if (categoria != null && !categoria.isEmpty()) {
                sqlPedidosPendentes.append(
                        "LEFT JOIN item_pedido ip ON p.id = ip.id_pedido LEFT JOIN produto prod ON ip.id_produto = prod.id ");
            }

            sqlPedidosPendentes.append("WHERE (data_entrega IS NULL OR data_entrega > CURDATE()) ");

            if (dataInicio != null && !dataInicio.isEmpty()) {
                sqlPedidosPendentes.append("AND p.data_requisicao >= ? ");
                paramsPendentes.add(java.sql.Date.valueOf(dataInicio));
            }
            if (dataFim != null && !dataFim.isEmpty()) {
                sqlPedidosPendentes.append("AND p.data_requisicao <= ? ");
                paramsPendentes.add(java.sql.Date.valueOf(dataFim));
            }
            if (tipoCliente != null && !tipoCliente.isEmpty()) {
                String tipo = tipoCliente.equals("PESSOA_FISICA") ? "PF"
                        : tipoCliente.equals("PESSOA_JURIDICA") ? "PJ" : tipoCliente;
                sqlPedidosPendentes.append("AND c.tipo = ? ");
                paramsPendentes.add(tipo);
            }
            if (clienteId != null && clienteId > 0) {
                sqlPedidosPendentes.append("AND p.cliente_id = ? ");
                paramsPendentes.add(clienteId);
            }
            if (categoria != null && !categoria.isEmpty()) {
                sqlPedidosPendentes.append("AND prod.tipo = ? ");
                paramsPendentes.add(categoria);
            }

            try (Connection conn = Conexao.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sqlPedidosPendentes.toString())) {
                for (int i = 0; i < paramsPendentes.size(); i++) {
                    stmt.setObject(i + 1, paramsPendentes.get(i));
                }
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    alertas.put("pedidosPendentes", rs.getInt("total"));
                }
            }

            // Pedidos atrasados com filtros
            StringBuilder sqlPedidosAtrasados = new StringBuilder(
                    "SELECT COUNT(*) as total FROM pedido p ");
            List<Object> paramsAtrasados = new ArrayList<>();

            if (tipoCliente != null && !tipoCliente.isEmpty()) {
                sqlPedidosAtrasados.append("LEFT JOIN cliente c ON p.cliente_id = c.id ");
            }
            if (categoria != null && !categoria.isEmpty()) {
                sqlPedidosAtrasados.append(
                        "LEFT JOIN item_pedido ip ON p.id = ip.id_pedido LEFT JOIN produto prod ON ip.id_produto = prod.id ");
            }

            sqlPedidosAtrasados.append("WHERE data_entrega IS NOT NULL AND data_entrega < CURDATE() ");

            if (dataInicio != null && !dataInicio.isEmpty()) {
                sqlPedidosAtrasados.append("AND p.data_requisicao >= ? ");
                paramsAtrasados.add(java.sql.Date.valueOf(dataInicio));
            }
            if (dataFim != null && !dataFim.isEmpty()) {
                sqlPedidosAtrasados.append("AND p.data_requisicao <= ? ");
                paramsAtrasados.add(java.sql.Date.valueOf(dataFim));
            }
            if (tipoCliente != null && !tipoCliente.isEmpty()) {
                String tipo = tipoCliente.equals("PESSOA_FISICA") ? "PF"
                        : tipoCliente.equals("PESSOA_JURIDICA") ? "PJ" : tipoCliente;
                sqlPedidosAtrasados.append("AND c.tipo = ? ");
                paramsAtrasados.add(tipo);
            }
            if (clienteId != null && clienteId > 0) {
                sqlPedidosAtrasados.append("AND p.cliente_id = ? ");
                paramsAtrasados.add(clienteId);
            }
            if (categoria != null && !categoria.isEmpty()) {
                sqlPedidosAtrasados.append("AND prod.tipo = ? ");
                paramsAtrasados.add(categoria);
            }

            try (Connection conn = Conexao.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sqlPedidosAtrasados.toString())) {
                for (int i = 0; i < paramsAtrasados.size(); i++) {
                    stmt.setObject(i + 1, paramsAtrasados.get(i));
                }
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    alertas.put("pedidosAtrasados", rs.getInt("total"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alertas;
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

        Map<String, Integer> tipos = new HashMap<>();
        StringBuilder sql = new StringBuilder("SELECT c.tipo, COUNT(DISTINCT c.id) as total FROM cliente c ");
        List<Object> params = new ArrayList<>();

        boolean hasJoinPedido = dataInicio != null || dataFim != null || categoria != null;
        if (hasJoinPedido) {
            sql.append("LEFT JOIN pedido p ON c.id = p.cliente_id ");
            if (categoria != null && !categoria.isEmpty()) {
                sql.append(
                        "LEFT JOIN item_pedido ip ON p.id = ip.id_pedido LEFT JOIN produto prod ON ip.id_produto = prod.id ");
            }
        }

        sql.append("WHERE c.ativo = true ");

        if (dataInicio != null && !dataInicio.isEmpty()) {
            sql.append("AND p.data_requisicao >= ? ");
            params.add(java.sql.Date.valueOf(dataInicio));
        }
        if (dataFim != null && !dataFim.isEmpty()) {
            sql.append("AND p.data_requisicao <= ? ");
            params.add(java.sql.Date.valueOf(dataFim));
        }
        if (tipoCliente != null && !tipoCliente.isEmpty()) {
            String tipo = tipoCliente.equals("PESSOA_FISICA") ? "PF"
                    : tipoCliente.equals("PESSOA_JURIDICA") ? "PJ" : tipoCliente;
            sql.append("AND c.tipo = ? ");
            params.add(tipo);
        }
        if (clienteId != null && clienteId > 0) {
            sql.append("AND c.id = ? ");
            params.add(clienteId);
        }
        if (categoria != null && !categoria.isEmpty()) {
            sql.append("AND prod.tipo = ? ");
            params.add(categoria);
        }

        sql.append("GROUP BY c.tipo");

        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            int pf = 0, pj = 0;
            while (rs.next()) {
                String tipo = rs.getString("tipo");
                int total = rs.getInt("total");
                if ("PF".equalsIgnoreCase(tipo))
                    pf = total;
                else if ("PJ".equalsIgnoreCase(tipo))
                    pj = total;
            }
            tipos.put("pf", pf);
            tipos.put("pj", pj);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tipos;
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

        List<Map<String, Object>> statusList = new ArrayList<>();
        Map<String, Integer> statusMap = new HashMap<>();
        statusMap.put("PENDENTE", 0);
        statusMap.put("EM_ANDAMENTO", 0);
        statusMap.put("ENTREGUE", 0);

        // Query para contar status (com filtros)
        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        "CASE " +
                        "WHEN p.data_entrega IS NULL THEN 'PENDENTE' " +
                        "WHEN p.data_entrega > CURDATE() THEN 'EM_ANDAMENTO' " +
                        "WHEN p.data_entrega <= CURDATE() THEN 'ENTREGUE' " +
                        "END as status, " +
                        "COUNT(*) as total " +
                        "FROM pedido p ");

        List<Object> params = new ArrayList<>();
        boolean whereAdded = false;

        if (tipoCliente != null && !tipoCliente.isEmpty()) {
            sql.append("JOIN cliente c ON p.cliente_id = c.id ");
            sql.append(whereAdded ? "AND " : "WHERE ");
            sql.append("c.tipo = ? ");
            params.add(tipoCliente.equals("PESSOA_FISICA") ? "PF" : "PJ");
            whereAdded = true;
        }
        if (clienteId != null && clienteId > 0) {
            sql.append(whereAdded ? "AND " : "WHERE ");
            sql.append("p.cliente_id = ? ");
            params.add(clienteId);
            whereAdded = true;
        }
        if (dataInicio != null && !dataInicio.isEmpty()) {
            sql.append(whereAdded ? "AND " : "WHERE ");
            sql.append("p.data_requisicao >= ? ");
            params.add(java.sql.Date.valueOf(dataInicio));
            whereAdded = true;
        }
        if (dataFim != null && !dataFim.isEmpty()) {
            sql.append(whereAdded ? "AND " : "WHERE ");
            sql.append("p.data_requisicao <= ? ");
            params.add(java.sql.Date.valueOf(dataFim));
            whereAdded = true;
        }
        if (statusPedido != null && !statusPedido.isEmpty()) {
            sql.append(whereAdded ? "AND " : "WHERE ");
            sql.append(
                    "CASE " +
                            "WHEN p.data_entrega IS NULL THEN 'PENDENTE' " +
                            "WHEN p.data_entrega > CURDATE() THEN 'EM_ANDAMENTO' " +
                            "WHEN p.data_entrega <= CURDATE() THEN 'ENTREGUE' " +
                            "END = ? ");
            params.add(statusPedido);
            whereAdded = true;
        }

        sql.append("GROUP BY status");

        int pendente = 0;
        int andamento = 0;

        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String status = rs.getString("status");
                int total = rs.getInt("total");
                if ("PENDENTE".equals(status))
                    pendente = total;
                if ("EM_ANDAMENTO".equals(status))
                    andamento = total;
                if (statusMap.containsKey(status)) {
                    statusMap.put(status, total);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Busca o total absoluto de pedidos (SEM FILTRO)
        int totalPedidos = 0;
        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) as total FROM pedido")) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalPedidos = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Só calcula "ENTREGUE" como total - pendente - andamento se NÃO houver filtro
        // de status
        if (statusPedido == null || statusPedido.isEmpty()) {
            int entregue = totalPedidos - pendente - andamento;
            statusMap.put("ENTREGUE", Math.max(entregue, 0));
        }
        // Se houver filtro de status, mantém o valor retornado do banco

        // Monta a lista final apenas com os 3 status
        for (String status : new String[] { "PENDENTE", "EM_ANDAMENTO", "ENTREGUE" }) {
            Map<String, Object> map = new HashMap<>();
            map.put("status", status);
            map.put("total", statusMap.get(status));
            statusList.add(map);
        }

        return statusList;
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

        Map<String, Integer> map = new HashMap<>();

        // Query base para entregas no prazo
        StringBuilder sqlNoPrazo = new StringBuilder("SELECT COUNT(*) as total " +
                "FROM pedido p " +
                "JOIN cliente c ON p.cliente_id = c.id " +
                "LEFT JOIN item_pedido ip ON p.id = ip.id_pedido " +
                "LEFT JOIN produto prod ON ip.id_produto = prod.id " +
                "WHERE p.data_entrega IS NOT NULL AND DATEDIFF(p.data_entrega, p.data_requisicao) <= 7 ");

        // Query base para entregas atrasadas
        StringBuilder sqlAtrasados = new StringBuilder("SELECT COUNT(*) as total " +
                "FROM pedido p " +
                "JOIN cliente c ON p.cliente_id = c.id " +
                "LEFT JOIN item_pedido ip ON p.id = ip.id_pedido " +
                "LEFT JOIN produto prod ON ip.id_produto = prod.id " +
                "WHERE p.data_entrega IS NOT NULL AND DATEDIFF(p.data_entrega, p.data_requisicao) > 7 ");

        List<Object> params = new ArrayList<>();

        // Construir filtros que serão aplicados às duas queries
        StringBuilder whereClause = new StringBuilder();

        // Filtro por data de início
        if (dataInicio != null && !dataInicio.isEmpty()) {
            whereClause.append("AND p.data_requisicao >= ? ");
            params.add(dataInicio);
        }

        // Filtro por data de fim
        if (dataFim != null && !dataFim.isEmpty()) {
            whereClause.append("AND p.data_requisicao <= ? ");
            params.add(dataFim);
        }

        // Filtro por tipo de cliente
        if (tipoCliente != null && !tipoCliente.isEmpty()) {
            whereClause.append("AND c.tipo = ? ");
            params.add(tipoCliente);
        }

        // Filtro por categoria
        if (categoria != null && !categoria.isEmpty()) {
            whereClause.append("AND prod.tipo = ? ");
            params.add(categoria);
        }

        // Filtro por cliente específico
        if (clienteId != null) {
            whereClause.append("AND p.cliente_id = ? ");
            params.add(clienteId);
        }

        // Aplicar filtros às duas queries
        sqlNoPrazo.append(whereClause);
        sqlAtrasados.append(whereClause);

        try (Connection conn = Conexao.getConnection()) {
            // Query para entregas no prazo
            try (PreparedStatement stmt1 = conn.prepareStatement(sqlNoPrazo.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    stmt1.setObject(i + 1, params.get(i));
                }
                try (ResultSet rs1 = stmt1.executeQuery()) {
                    if (rs1.next())
                        map.put("noPrazo", rs1.getInt("total"));
                }
            }

            // Query para entregas atrasadas
            try (PreparedStatement stmt2 = conn.prepareStatement(sqlAtrasados.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    stmt2.setObject(i + 1, params.get(i));
                }
                try (ResultSet rs2 = stmt2.executeQuery()) {
                    if (rs2.next())
                        map.put("atrasados", rs2.getInt("total"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
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

        List<Map<String, Object>> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT YEAR(p.data_requisicao) as ano, " +
                "MONTH(p.data_requisicao) as mes, " +
                "AVG(DATEDIFF(p.data_entrega, p.data_requisicao)) as tempoMedio " +
                "FROM pedido p " +
                "JOIN cliente c ON p.cliente_id = c.id " +
                "LEFT JOIN item_pedido ip ON p.id = ip.id_pedido " +
                "LEFT JOIN produto prod ON ip.id_produto = prod.id " +
                "WHERE p.data_entrega IS NOT NULL ");

        List<Object> params = new ArrayList<>();

        // Filtro por data de início
        if (dataInicio != null && !dataInicio.isEmpty()) {
            sql.append("AND p.data_requisicao >= ? ");
            params.add(dataInicio);
        }

        // Filtro por data de fim
        if (dataFim != null && !dataFim.isEmpty()) {
            sql.append("AND p.data_requisicao <= ? ");
            params.add(dataFim);
        }

        // Filtro por tipo de cliente
        if (tipoCliente != null && !tipoCliente.isEmpty()) {
            sql.append("AND c.tipo = ? ");
            params.add(tipoCliente);
        }

        // Filtro por categoria
        if (categoria != null && !categoria.isEmpty()) {
            sql.append("AND prod.tipo = ? ");
            params.add(categoria);
        }

        // Filtro por cliente específico
        if (clienteId != null) {
            sql.append("AND p.cliente_id = ? ");
            params.add(clienteId);
        }

        sql.append("GROUP BY ano, mes ORDER BY ano, mes");

        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("ano", rs.getInt("ano"));
                    map.put("mes", rs.getInt("mes"));
                    map.put("tempoMedio", rs.getDouble("tempoMedio"));
                    lista.add(map);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Endpoint para listar pedidos atrasados (simulado - mais de 7 dias)
    @GetMapping("/api/pedidos-atrasados")
    @ResponseBody
    public List<Map<String, Object>> getPedidosAtrasados() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT p.id, c.nome as clienteNome, p.data_requisicao, p.data_entrega, DATEDIFF(p.data_entrega, p.data_requisicao) as diasEntrega "
                +
                "FROM pedido p " +
                "JOIN cliente c ON p.cliente_id = c.id " +
                "WHERE p.data_entrega IS NOT NULL AND DATEDIFF(p.data_entrega, p.data_requisicao) > 7 " +
                "ORDER BY diasEntrega DESC";
        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("id"));
                map.put("clienteNome", rs.getString("clienteNome"));
                map.put("dataRequisicao", rs.getDate("data_requisicao"));
                map.put("dataEntrega", rs.getDate("data_entrega"));
                map.put("diasEntrega", rs.getInt("diasEntrega"));
                lista.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Endpoint para listar pedidos no prazo (simulado - até 7 dias)
    @GetMapping("/api/pedidos-no-prazo")
    @ResponseBody
    public List<Map<String, Object>> getPedidosNoPrazo() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT p.id, c.nome as clienteNome, p.data_requisicao, p.data_entrega, DATEDIFF(p.data_entrega, p.data_requisicao) as diasEntrega "
                +
                "FROM pedido p " +
                "JOIN cliente c ON p.cliente_id = c.id " +
                "WHERE p.data_entrega IS NOT NULL AND DATEDIFF(p.data_entrega, p.data_requisicao) <= 7 " +
                "ORDER BY diasEntrega ASC";
        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("id"));
                map.put("clienteNome", rs.getString("clienteNome"));
                map.put("dataRequisicao", rs.getDate("data_requisicao"));
                map.put("dataEntrega", rs.getDate("data_entrega"));
                map.put("diasEntrega", rs.getInt("diasEntrega"));
                lista.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Endpoint para movimentações de estoque recentes
    @GetMapping("/api/movimentacoes-estoque")
    @ResponseBody
    public List<Map<String, Object>> getMovimentacoesEstoque() {
        List<Map<String, Object>> lista = new ArrayList<>();
        // Se não houver movimentações, simular com base nos produtos vendidos
        String sql = "SELECT me.id, p.nome as produto, me.tipo, me.quantidade, me.motivo, me.data " +
                "FROM movimentacao_estoque me " +
                "JOIN produto p ON me.id_produto = p.id " +
                "ORDER BY me.data DESC " +
                "LIMIT 20";
        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("id"));
                map.put("produto", rs.getString("produto"));
                map.put("tipo", rs.getString("tipo"));
                map.put("quantidade", rs.getInt("quantidade"));
                map.put("motivo", rs.getString("motivo"));
                map.put("data", rs.getDate("data"));
                lista.add(map);
            }

            // Se não há movimentações, simular baseado em vendas recentes
            if (lista.isEmpty()) {
                String sqlSimulado = "SELECT p.id, p.nome as produto, SUM(ip.quantidade) as quantidade " +
                        "FROM produto p " +
                        "JOIN item_pedido ip ON p.id = ip.id_produto " +
                        "JOIN pedido ped ON ip.id_pedido = ped.id " +
                        "GROUP BY p.id, p.nome " +
                        "ORDER BY SUM(ip.quantidade) DESC " +
                        "LIMIT 10";
                try (PreparedStatement stmtSim = conn.prepareStatement(sqlSimulado);
                        ResultSet rsSim = stmtSim.executeQuery()) {
                    int id = 1;
                    while (rsSim.next()) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", id++);
                        map.put("produto", rsSim.getString("produto"));
                        map.put("tipo", "Saída");
                        map.put("quantidade", rsSim.getInt("quantidade"));
                        map.put("motivo", "Venda de produto");
                        map.put("data", new java.sql.Date(System.currentTimeMillis()));
                        lista.add(map);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Endpoint para top funcionários por vendas
    @GetMapping("/api/top-funcionarios")
    @ResponseBody
    public List<Map<String, Object>> getTopFuncionarios() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT f.nome, f.cargo, COUNT(p.id) as pedidos, " +
                "COALESCE(SUM(prod.valor * ip.quantidade), 0) as faturamento " +
                "FROM funcionario f " +
                "LEFT JOIN pedido p ON f.id = p.funcionario_id " +
                "LEFT JOIN item_pedido ip ON p.id = ip.id_pedido " +
                "LEFT JOIN produto prod ON ip.id_produto = prod.id " +
                "WHERE f.ativo = true " +
                "GROUP BY f.id, f.nome, f.cargo " +
                "HAVING pedidos > 0 " +
                "ORDER BY faturamento DESC " +
                "LIMIT 10";
        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("nome", rs.getString("nome"));
                map.put("cargo", rs.getString("cargo"));
                map.put("pedidos", rs.getInt("pedidos"));
                map.put("faturamento", rs.getDouble("faturamento"));
                lista.add(map);
            }

            // Se não há funcionários com vendas, mostrar todos os funcionários ativos
            if (lista.isEmpty()) {
                String sqlTodos = "SELECT nome, cargo, " +
                        "CASE WHEN cargo = 'Diretor' THEN 50000 " +
                        "     WHEN cargo = 'Auxiliar' THEN 15000 " +
                        "     ELSE 25000 END as faturamento, " +
                        "CASE WHEN cargo = 'Diretor' THEN 25 " +
                        "     WHEN cargo = 'Auxiliar' THEN 8 " +
                        "     ELSE 15 END as pedidos " +
                        "FROM funcionario WHERE ativo = true " +
                        "ORDER BY faturamento DESC";
                try (PreparedStatement stmtTodos = conn.prepareStatement(sqlTodos);
                        ResultSet rsTodos = stmtTodos.executeQuery()) {
                    while (rsTodos.next()) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("nome", rsTodos.getString("nome"));
                        map.put("cargo", rsTodos.getString("cargo"));
                        map.put("pedidos", rsTodos.getInt("pedidos"));
                        map.put("faturamento", rsTodos.getDouble("faturamento"));
                        lista.add(map);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Endpoint para uso de cupons
    @GetMapping("/api/uso-cupons")
    @ResponseBody
    public List<Map<String, Object>> getUsoCupons() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT c.codigo, c.descricao, c.valor, COUNT(p.id) as usos, " +
                "COALESCE(SUM(p.valor_desconto), 0) as desconto_total " +
                "FROM cupom c " +
                "LEFT JOIN pedido p ON c.id = p.cupom_id " +
                "GROUP BY c.id, c.codigo, c.descricao, c.valor " +
                "ORDER BY usos DESC";
        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("codigo", rs.getString("codigo"));
                map.put("descricao", rs.getString("descricao"));
                map.put("valor", rs.getDouble("valor"));
                map.put("usos", rs.getInt("usos"));
                map.put("descontoTotal", rs.getDouble("desconto_total"));
                lista.add(map);
            }

            // Se não há cupons com uso, simular dados baseado nos cupons cadastrados
            if (lista.stream().allMatch(map -> ((Integer) map.get("usos")) == 0)) {
                lista.clear();
                String sqlSimulado = "SELECT codigo, descricao, valor FROM cupom ORDER BY valor DESC";
                try (PreparedStatement stmtSim = conn.prepareStatement(sqlSimulado);
                        ResultSet rsSim = stmtSim.executeQuery()) {
                    int[] usosSimulados = { 25, 18, 12, 8, 3 }; // Usos simulados
                    int i = 0;
                    while (rsSim.next() && i < usosSimulados.length) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("codigo", rsSim.getString("codigo"));
                        map.put("descricao", rsSim.getString("descricao"));
                        map.put("valor", rsSim.getDouble("valor"));
                        map.put("usos", usosSimulados[i]);
                        map.put("descontoTotal", rsSim.getDouble("valor") * usosSimulados[i]);
                        lista.add(map);
                        i++;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Endpoint para produtos mais rentáveis
    @GetMapping("/api/produtos-rentaveis")
    @ResponseBody
    public List<Map<String, Object>> getProdutosRentaveis() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT p.nome, p.tipo, p.valor, " +
                "SUM(ip.quantidade) as quantidade_vendida, " +
                "SUM(p.valor * ip.quantidade) as receita_total, " +
                "(SUM(p.valor * ip.quantidade) / SUM(ip.quantidade)) as valor_medio " +
                "FROM produto p " +
                "JOIN item_pedido ip ON p.id = ip.id_produto " +
                "GROUP BY p.id, p.nome, p.tipo, p.valor " +
                "ORDER BY receita_total DESC " +
                "LIMIT 15";
        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("nome", rs.getString("nome"));
                map.put("tipo", rs.getString("tipo"));
                map.put("valor", rs.getDouble("valor"));
                map.put("quantidadeVendida", rs.getInt("quantidade_vendida"));
                map.put("receitaTotal", rs.getDouble("receita_total"));
                map.put("valorMedio", rs.getDouble("valor_medio"));
                lista.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Endpoint para análise de sazonalidade
    @GetMapping("/api/sazonalidade-vendas")
    @ResponseBody
    public List<Map<String, Object>> getSazonalidadeVendas() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT " +
                "MONTH(p.data_requisicao) as mes, " +
                "MONTHNAME(p.data_requisicao) as nome_mes, " +
                "COUNT(p.id) as pedidos, " +
                "COALESCE(SUM(prod.valor * ip.quantidade), 0) as faturamento, " +
                "COALESCE(AVG(prod.valor * ip.quantidade), 0) as ticket_medio " +
                "FROM pedido p " +
                "LEFT JOIN item_pedido ip ON p.id = ip.id_pedido " +
                "LEFT JOIN produto prod ON ip.id_produto = prod.id " +
                "WHERE p.data_requisicao >= DATE_SUB(CURDATE(), INTERVAL 24 MONTH) " +
                "GROUP BY MONTH(p.data_requisicao), MONTHNAME(p.data_requisicao) " +
                "ORDER BY mes";
        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("mes", rs.getInt("mes"));
                map.put("nomeMes", rs.getString("nome_mes"));
                map.put("pedidos", rs.getInt("pedidos"));
                map.put("faturamento", rs.getDouble("faturamento"));
                map.put("ticketMedio", rs.getDouble("ticket_medio"));
                lista.add(map);
            }

            // Se não há dados suficientes, simular sazonalidade baseada em padrões típicos
            if (lista.size() < 6) {
                lista.clear();
                String[] meses = { "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro" };
                double[] faturamentoBase = { 15000, 18000, 25000, 32000, 28000, 22000,
                        20000, 24000, 30000, 35000, 27000, 40000 };
                int[] pedidosBase = { 25, 30, 40, 55, 48, 38, 35, 42, 52, 65, 50, 75 };

                for (int i = 0; i < 12; i++) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("mes", i + 1);
                    map.put("nomeMes", meses[i]);
                    map.put("pedidos", pedidosBase[i]);
                    map.put("faturamento", faturamentoBase[i]);
                    map.put("ticketMedio", faturamentoBase[i] / pedidosBase[i]);
                    lista.add(map);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Endpoint para análise geográfica de clientes
    @GetMapping("/api/clientes-geografico")
    @ResponseBody
    public List<Map<String, Object>> getClientesGeografico() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT " +
                "COALESCE(cidade, 'Não informado') as cidade, " +
                "COALESCE(estado, 'N/A') as estado, " +
                "COUNT(*) as quantidade_clientes, " +
                "COUNT(CASE WHEN ativo = true THEN 1 END) as clientes_ativos " +
                "FROM cliente " +
                "GROUP BY cidade, estado " +
                "ORDER BY quantidade_clientes DESC " +
                "LIMIT 20";
        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("cidade", rs.getString("cidade"));
                map.put("estado", rs.getString("estado"));
                map.put("quantidadeClientes", rs.getInt("quantidade_clientes"));
                map.put("clientesAtivos", rs.getInt("clientes_ativos"));
                lista.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}