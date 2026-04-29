package com.studiomuda.estoque.infrastructure.persistence.dashboard;

import com.studiomuda.estoque.application.dashboard.ports.DashboardQueryPort;
import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.dto.DashboardDTO;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DashboardQueryJdbc implements DashboardQueryPort {

    @Override
    public List<Map<String, Object>> listarCategorias() {
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
            throw new RuntimeException("Erro ao buscar categorias: " + e.getMessage(), e);
        }
        return categorias;
    }

    @Override
    public List<Map<String, Object>> listarClientesAtivos() {
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
            throw new RuntimeException("Erro ao buscar clientes: " + e.getMessage(), e);
        }
        return clientes;
    }

    @Override
    public List<DashboardDTO.PedidoResumo> listarPedidosRecentes(String dataInicio, String dataFim, String statusPedido,
                                                                 String categoria, String tipoCliente, Integer clienteId) {
        List<DashboardDTO.PedidoResumo> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT p.id, p.data_requisicao, p.data_entrega, c.nome as cliente_nome, " +
                        "COALESCE(SUM(ip.quantidade * prod.valor), 0) as valor_total " +
                        "FROM pedido p " +
                        "LEFT JOIN cliente c ON p.cliente_id = c.id " +
                        "LEFT JOIN item_pedido ip ON p.id = ip.id_pedido " +
                        "LEFT JOIN produto prod ON ip.id_produto = prod.id " +
                        "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
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
            sql.append(
                    "AND EXISTS (SELECT 1 FROM item_pedido ip2 JOIN produto prod2 ON ip2.id_produto = prod2.id WHERE ip2.id_pedido = p.id AND prod2.tipo = ?) ");
            params.add(categoria);
        }

        sql.append("GROUP BY p.id, p.data_requisicao, p.data_entrega, c.nome ");
        sql.append("ORDER BY p.data_requisicao DESC LIMIT 10");

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DashboardDTO.PedidoResumo dto = new DashboardDTO.PedidoResumo();
                    dto.id = rs.getInt("id");
                    dto.clienteNome = rs.getString("cliente_nome");
                    dto.dataRequisicao = rs.getDate("data_requisicao");
                    dto.dataEntrega = rs.getDate("data_entrega");
                    dto.valorTotal = rs.getDouble("valor_total");
                    lista.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pedidos recentes: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<DashboardDTO.ClienteAtivo> listarTopClientes() {
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
            throw new RuntimeException("Erro ao buscar top clientes: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Map<String, Object>> listarTopProdutos(String dataInicio, String dataFim, String statusPedido,
                                                       String categoria, String tipoCliente, Integer clienteId) {
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
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("produtoNome", rs.getString("produtoNome"));
                    map.put("quantidadeVendida", rs.getInt("quantidadeVendida"));
                    lista.add(map);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar top produtos: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Map<String, Object>> listarTopCidades() {
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
            throw new RuntimeException("Erro ao buscar top cidades: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Map<String, Object>> listarVendasSemana() {
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
            throw new RuntimeException("Erro ao buscar vendas da semana: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Map<String, Object> listarMetricasPrincipais(String dataInicio, String dataFim, String statusPedido,
                                                        String categoria, String tipoCliente, Integer clienteId) {
        Map<String, Object> metricas = new HashMap<>();
        try (Connection conn = Conexao.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) as total FROM pedido")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) metricas.put("totalPedidos", rs.getInt("total"));
            }

            StringBuilder sqlClientes = new StringBuilder("SELECT COUNT(*) as total FROM cliente WHERE ativo = 1 ");
            List<Object> paramsClientes = new ArrayList<>();
            if (tipoCliente != null && !tipoCliente.isEmpty()) {
                String tipo = tipoCliente.equals("PESSOA_FISICA") ? "PF"
                        : tipoCliente.equals("PESSOA_JURIDICA") ? "PJ" : tipoCliente;
                sqlClientes.append("AND tipo = ? ");
                paramsClientes.add(tipo);
            }
            try (PreparedStatement stmt = conn.prepareStatement(sqlClientes.toString())) {
                for (int i = 0; i < paramsClientes.size(); i++) stmt.setObject(i + 1, paramsClientes.get(i));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) metricas.put("clientesAtivos", rs.getInt("total"));
            }

            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) as total FROM funcionario WHERE ativo = 1");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) metricas.put("funcionariosAtivos", rs.getInt("total"));
            }

            StringBuilder sqlProdutos = new StringBuilder("SELECT COUNT(*) as total FROM produto WHERE 1=1 ");
            List<Object> paramsProdutos = new ArrayList<>();
            if (categoria != null && !categoria.isEmpty()) {
                sqlProdutos.append("AND tipo = ? ");
                paramsProdutos.add(categoria);
            }
            try (PreparedStatement stmt = conn.prepareStatement(sqlProdutos.toString())) {
                for (int i = 0; i < paramsProdutos.size(); i++) stmt.setObject(i + 1, paramsProdutos.get(i));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) metricas.put("totalProdutos", rs.getInt("total"));
            }

            StringBuilder sqlEstoque = new StringBuilder("SELECT COUNT(*) as total FROM produto WHERE quantidade > 0 ");
            List<Object> paramsEstoque = new ArrayList<>();
            if (categoria != null && !categoria.isEmpty()) {
                sqlEstoque.append("AND tipo = ? ");
                paramsEstoque.add(categoria);
            }
            try (PreparedStatement stmt = conn.prepareStatement(sqlEstoque.toString())) {
                for (int i = 0; i < paramsEstoque.size(); i++) stmt.setObject(i + 1, paramsEstoque.get(i));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) metricas.put("produtosEstoque", rs.getInt("total"));
            }

            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) as total FROM cupom");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) metricas.put("totalCupons", rs.getInt("total"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar métricas principais: " + e.getMessage(), e);
        }
        return metricas;
    }

    @Override
    public List<Map<String, Object>> listarEvolucaoVendas(String dataInicio, String dataFim, String statusPedido,
                                                          String categoria, String tipoCliente, Integer clienteId) {
        List<Map<String, Object>> evolucao = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT YEAR(p.data_requisicao) as ano, MONTH(p.data_requisicao) as mes, MONTHNAME(p.data_requisicao) as nome_mes, " +
                        "COUNT(DISTINCT p.id) as pedidos, COALESCE(SUM(prod.valor * ip.quantidade), 0) as faturamento " +
                        "FROM pedido p " +
                        "LEFT JOIN item_pedido ip ON p.id = ip.id_pedido " +
                        "LEFT JOIN produto prod ON ip.id_produto = prod.id ");
        List<Object> params = new ArrayList<>();
        if (tipoCliente != null && !tipoCliente.isEmpty()) sql.append("LEFT JOIN cliente c ON p.cliente_id = c.id ");
        sql.append("WHERE p.data_requisicao >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH) ");
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
            for (int i = 0; i < params.size(); i++) stmt.setObject(i + 1, params.get(i));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("ano", rs.getInt("ano"));
                    map.put("mes", rs.getInt("mes"));
                    map.put("nomeMes", rs.getString("nome_mes"));
                    map.put("pedidos", rs.getInt("pedidos"));
                    map.put("faturamento", rs.getDouble("faturamento"));
                    evolucao.add(map);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar evolução de vendas: " + e.getMessage(), e);
        }
        return evolucao;
    }

    @Override
    public List<Map<String, Object>> listarProdutosBaixoEstoque(String dataInicio, String dataFim, String statusPedido,
                                                                String categoria, String tipoCliente, Integer clienteId) {
        List<Map<String, Object>> produtos = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT DISTINCT p.id, p.nome, p.quantidade, p.valor, p.tipo, " +
                "CASE WHEN p.quantidade = 0 THEN 'ESGOTADO' WHEN p.quantidade <= 5 THEN 'CRITICO' WHEN p.quantidade <= 10 THEN 'BAIXO' ELSE 'NORMAL' END as status_estoque " +
                "FROM produto p WHERE p.quantidade <= 10 ");
        List<Object> params = new ArrayList<>();
        if (categoria != null && !categoria.isEmpty()) {
            sql.append("AND p.tipo = ? ");
            params.add(categoria);
        }
        sql.append("ORDER BY p.quantidade ASC");

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) stmt.setObject(i + 1, params.get(i));
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
            throw new RuntimeException("Erro ao buscar produtos baixo estoque: " + e.getMessage(), e);
        }
        return produtos;
    }

    @Override
    public List<Map<String, Object>> listarVendasPorCategoria(String dataInicio, String dataFim, String statusPedido,
                                                              String categoria, String tipoCliente, Integer clienteId) {
        List<Map<String, Object>> vendas = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT COALESCE(p.tipo, 'Sem Categoria') as categoria, COUNT(ip.id) as itens_vendidos, " +
                        "SUM(ip.quantidade) as quantidade_total, SUM(p.valor * ip.quantidade) as faturamento " +
                        "FROM produto p JOIN item_pedido ip ON p.id = ip.id_produto " +
                        "JOIN pedido ped ON ip.id_pedido = ped.id ");
        List<Object> params = new ArrayList<>();
        if (tipoCliente != null && !tipoCliente.isEmpty()) sql.append("JOIN cliente c ON ped.cliente_id = c.id ");
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
            for (int i = 0; i < params.size(); i++) stmt.setObject(i + 1, params.get(i));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("categoria", rs.getString("categoria"));
                    map.put("itensVendidos", rs.getInt("itens_vendidos"));
                    map.put("quantidadeTotal", rs.getInt("quantidade_total"));
                    map.put("faturamento", rs.getDouble("faturamento"));
                    vendas.add(map);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar vendas por categoria: " + e.getMessage(), e);
        }
        return vendas;
    }

    @Override
    public Map<String, Object> listarAlertas(String dataInicio, String dataFim, String statusPedido,
                                             String categoria, String tipoCliente, Integer clienteId) {
        Map<String, Object> alertas = new HashMap<>();
        try (Connection conn = Conexao.getConnection()) {
            StringBuilder sqlProdutosFalta = new StringBuilder("SELECT COUNT(*) as total FROM produto WHERE quantidade = 0 ");
            List<Object> paramsProdutosFalta = new ArrayList<>();
            if (categoria != null && !categoria.isEmpty()) {
                sqlProdutosFalta.append("AND tipo = ? ");
                paramsProdutosFalta.add(categoria);
            }
            try (PreparedStatement stmt = conn.prepareStatement(sqlProdutosFalta.toString())) {
                for (int i = 0; i < paramsProdutosFalta.size(); i++) stmt.setObject(i + 1, paramsProdutosFalta.get(i));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) alertas.put("produtosFalta", rs.getInt("total"));
            }

            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) as total FROM pedido WHERE (data_entrega IS NULL OR data_entrega > CURDATE())");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) alertas.put("pedidosPendentes", rs.getInt("total"));
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) as total FROM pedido WHERE data_entrega IS NOT NULL AND data_entrega < CURDATE()");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) alertas.put("pedidosAtrasados", rs.getInt("total"));
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) as total FROM alerta_financeiro WHERE resolvido = false");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) alertas.put("alertasFinanceiros", rs.getInt("total"));
            } catch (SQLException e) {
                alertas.put("alertasFinanceiros", 0);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar alertas: " + e.getMessage(), e);
        }
        return alertas;
    }

    @Override
    public Map<String, Integer> listarClientesPorTipo(String dataInicio, String dataFim, String statusPedido,
                                                      String categoria, String tipoCliente, Integer clienteId) {
        Map<String, Integer> tipos = new HashMap<>();
        StringBuilder sql = new StringBuilder("SELECT c.tipo, COUNT(DISTINCT c.id) as total FROM cliente c WHERE c.ativo = true ");
        List<Object> params = new ArrayList<>();
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
        sql.append("GROUP BY c.tipo");

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) stmt.setObject(i + 1, params.get(i));
            try (ResultSet rs = stmt.executeQuery()) {
                int pf = 0, pj = 0;
                while (rs.next()) {
                    String tipo = rs.getString("tipo");
                    int total = rs.getInt("total");
                    if ("PF".equalsIgnoreCase(tipo)) pf = total;
                    else if ("PJ".equalsIgnoreCase(tipo)) pj = total;
                }
                tipos.put("pf", pf);
                tipos.put("pj", pj);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar clientes por tipo: " + e.getMessage(), e);
        }
        return tipos;
    }

    @Override
    public List<Map<String, Object>> listarPedidosPorStatus(String dataInicio, String dataFim, String statusPedido,
                                                            String categoria, String tipoCliente, Integer clienteId) {
        List<Map<String, Object>> statusList = new ArrayList<>();
        Map<String, Integer> statusMap = new HashMap<>();
        statusMap.put("PENDENTE", 0);
        statusMap.put("EM_ANDAMENTO", 0);
        statusMap.put("ENTREGUE", 0);

        StringBuilder sql = new StringBuilder(
                "SELECT CASE WHEN p.data_entrega IS NULL THEN 'PENDENTE' " +
                        "WHEN p.data_entrega > CURDATE() THEN 'EM_ANDAMENTO' ELSE 'ENTREGUE' END as status, COUNT(*) as total " +
                        "FROM pedido p ");
        List<Object> params = new ArrayList<>();
        boolean whereAdded = false;
        if (tipoCliente != null && !tipoCliente.isEmpty()) {
            sql.append("JOIN cliente c ON p.cliente_id = c.id ");
            sql.append(whereAdded ? "AND " : "WHERE ");
            String tipo = tipoCliente.equals("PESSOA_FISICA") ? "PF"
                    : tipoCliente.equals("PESSOA_JURIDICA") ? "PJ" : tipoCliente;
            sql.append("c.tipo = ? ");
            params.add(tipo);
            whereAdded = true;
        }
        if (clienteId != null && clienteId > 0) {
            sql.append(whereAdded ? "AND " : "WHERE ");
            sql.append("p.cliente_id = ? ");
            params.add(clienteId);
            whereAdded = true;
        }
        sql.append("GROUP BY status");

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) stmt.setObject(i + 1, params.get(i));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    statusMap.put(rs.getString("status"), rs.getInt("total"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pedidos por status: " + e.getMessage(), e);
        }

        for (String status : new String[]{"PENDENTE", "EM_ANDAMENTO", "ENTREGUE"}) {
            Map<String, Object> map = new HashMap<>();
            map.put("status", status);
            map.put("total", statusMap.get(status));
            statusList.add(map);
        }
        return statusList;
    }

    @Override
    public Map<String, Integer> listarEntregasPrazo(String dataInicio, String dataFim, String statusPedido,
                                                    String categoria, String tipoCliente, Integer clienteId) {
        Map<String, Integer> map = new HashMap<>();
        try (Connection conn = Conexao.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) as total FROM pedido p WHERE p.data_entrega IS NOT NULL AND DATEDIFF(p.data_entrega, p.data_requisicao) <= 7");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) map.put("noPrazo", rs.getInt("total"));
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) as total FROM pedido p WHERE p.data_entrega IS NOT NULL AND DATEDIFF(p.data_entrega, p.data_requisicao) > 7");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) map.put("atrasados", rs.getInt("total"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar entregas no prazo: " + e.getMessage(), e);
        }
        return map;
    }

    @Override
    public List<Map<String, Object>> listarTempoEntregaMes(String dataInicio, String dataFim, String statusPedido,
                                                           String categoria, String tipoCliente, Integer clienteId) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT YEAR(p.data_requisicao) as ano, MONTH(p.data_requisicao) as mes, " +
                "AVG(DATEDIFF(p.data_entrega, p.data_requisicao)) as tempoMedio " +
                "FROM pedido p WHERE p.data_entrega IS NOT NULL GROUP BY ano, mes ORDER BY ano, mes";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("ano", rs.getInt("ano"));
                map.put("mes", rs.getInt("mes"));
                map.put("tempoMedio", rs.getDouble("tempoMedio"));
                lista.add(map);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar tempo médio de entrega: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Map<String, Object>> listarPedidosAtrasados() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT p.id, c.nome as clienteNome, p.data_requisicao, p.data_entrega, DATEDIFF(p.data_entrega, p.data_requisicao) as diasEntrega " +
                "FROM pedido p JOIN cliente c ON p.cliente_id = c.id " +
                "WHERE p.data_entrega IS NOT NULL AND DATEDIFF(p.data_entrega, p.data_requisicao) > 7 ORDER BY diasEntrega DESC";
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
            throw new RuntimeException("Erro ao buscar pedidos atrasados: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Map<String, Object>> listarPedidosNoPrazo() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT p.id, c.nome as clienteNome, p.data_requisicao, p.data_entrega, DATEDIFF(p.data_entrega, p.data_requisicao) as diasEntrega " +
                "FROM pedido p JOIN cliente c ON p.cliente_id = c.id " +
                "WHERE p.data_entrega IS NOT NULL AND DATEDIFF(p.data_entrega, p.data_requisicao) <= 7 ORDER BY diasEntrega ASC";
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
            throw new RuntimeException("Erro ao buscar pedidos no prazo: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Map<String, Object>> listarMovimentacoesEstoque() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT me.id, p.nome as produto, me.tipo, me.quantidade, me.motivo, me.data " +
                "FROM movimentacao_estoque me JOIN produto p ON me.id_produto = p.id ORDER BY me.data DESC LIMIT 20";
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
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar movimentações de estoque: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Map<String, Object>> listarTopFuncionarios() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT f.nome, f.cargo, COUNT(p.id) as pedidos, " +
                "COALESCE(SUM(prod.valor * ip.quantidade), 0) as faturamento " +
                "FROM funcionario f " +
                "LEFT JOIN pedido p ON f.id = p.funcionario_id " +
                "LEFT JOIN item_pedido ip ON p.id = ip.id_pedido " +
                "LEFT JOIN produto prod ON ip.id_produto = prod.id " +
                "WHERE f.ativo = true GROUP BY f.id, f.nome, f.cargo HAVING pedidos > 0 ORDER BY faturamento DESC LIMIT 10";
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
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar top funcionários: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Map<String, Object>> listarUsoCupons() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT c.codigo, c.descricao, c.valor, COUNT(p.id) as usos, " +
                "COALESCE(SUM(p.valor_desconto), 0) as desconto_total " +
                "FROM cupom c LEFT JOIN pedido p ON c.id = p.cupom_id " +
                "GROUP BY c.id, c.codigo, c.descricao, c.valor ORDER BY usos DESC";
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
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar uso de cupons: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Map<String, Object>> listarProdutosRentaveis() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT p.nome, p.tipo, p.valor, SUM(ip.quantidade) as quantidade_vendida, " +
                "SUM(p.valor * ip.quantidade) as receita_total, (SUM(p.valor * ip.quantidade) / SUM(ip.quantidade)) as valor_medio " +
                "FROM produto p JOIN item_pedido ip ON p.id = ip.id_produto " +
                "GROUP BY p.id, p.nome, p.tipo, p.valor ORDER BY receita_total DESC LIMIT 15";
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
            throw new RuntimeException("Erro ao buscar produtos rentáveis: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Map<String, Object>> listarSazonalidadeVendas() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT MONTH(p.data_requisicao) as mes, MONTHNAME(p.data_requisicao) as nome_mes, " +
                "COUNT(p.id) as pedidos, COALESCE(SUM(prod.valor * ip.quantidade), 0) as faturamento, " +
                "COALESCE(AVG(prod.valor * ip.quantidade), 0) as ticket_medio " +
                "FROM pedido p LEFT JOIN item_pedido ip ON p.id = ip.id_pedido " +
                "LEFT JOIN produto prod ON ip.id_produto = prod.id " +
                "WHERE p.data_requisicao >= DATE_SUB(CURDATE(), INTERVAL 24 MONTH) " +
                "GROUP BY MONTH(p.data_requisicao), MONTHNAME(p.data_requisicao) ORDER BY mes";
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
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar sazonalidade de vendas: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Map<String, Object>> listarClientesGeografico() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT COALESCE(cidade, 'Não informado') as cidade, COALESCE(estado, 'N/A') as estado, " +
                "COUNT(*) as quantidade_clientes, COUNT(CASE WHEN ativo = true THEN 1 END) as clientes_ativos " +
                "FROM cliente GROUP BY cidade, estado ORDER BY quantidade_clientes DESC LIMIT 20";
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
            throw new RuntimeException("Erro ao buscar distribuição geográfica: " + e.getMessage(), e);
        }
        return lista;
    }
}
