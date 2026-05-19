package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.Fornecedor;
import com.studiomuda.estoque.model.OrdemCompra;
import com.studiomuda.estoque.model.ParametroEstoque;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SuprimentoDAO {

    public List<Fornecedor> listarFornecedoresAtivos() throws SQLException {
        List<Fornecedor> fornecedores = new ArrayList<>();
        String sql = "SELECT * FROM fornecedor WHERE ativo = TRUE ORDER BY lead_time_dias ASC, nome ASC";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                fornecedores.add(new Fornecedor(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getInt("lead_time_dias"),
                        rs.getBoolean("ativo")
                ));
            }
        }
        return fornecedores;
    }

    public void salvarParametro(int produtoId, String fornecedorNome, int leadTimeDias, int margemSeguranca) throws SQLException {
        int fornecedorId = inserirFornecedor(fornecedorNome, leadTimeDias);
        String sql = "INSERT INTO parametro_estoque (produto_id, fornecedor_id, margem_seguranca) " +
                "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE fornecedor_id = VALUES(fornecedor_id), margem_seguranca = VALUES(margem_seguranca)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produtoId);
            stmt.setInt(2, fornecedorId);
            stmt.setInt(3, margemSeguranca);
            stmt.executeUpdate();
        }
    }

    private int inserirFornecedor(String nome, int leadTimeDias) throws SQLException {
        String sql = "INSERT INTO fornecedor (nome, lead_time_dias, ativo) VALUES (?, ?, TRUE)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nome);
            stmt.setInt(2, leadTimeDias);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Nao foi possivel cadastrar o fornecedor.");
    }

    public List<ParametroEstoque> listarParametros() throws SQLException {
        List<ParametroEstoque> parametros = new ArrayList<>();
        String sql = "SELECT pe.id, pe.produto_id, pe.fornecedor_id, pe.margem_seguranca, " +
                "p.nome AS produto_nome, p.quantidade AS estoque_atual, f.nome AS fornecedor_nome, f.lead_time_dias " +
                "FROM parametro_estoque pe " +
                "JOIN produto p ON p.id = pe.produto_id " +
                "JOIN fornecedor f ON f.id = pe.fornecedor_id " +
                "ORDER BY p.nome";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                parametros.add(mapearParametro(rs));
            }
        }
        return parametros;
    }

    public ParametroEstoque buscarParametroPorProduto(int produtoId) throws SQLException {
        String sql = "SELECT pe.id, pe.produto_id, pe.fornecedor_id, pe.margem_seguranca, " +
                "p.nome AS produto_nome, p.quantidade AS estoque_atual, f.nome AS fornecedor_nome, f.lead_time_dias " +
                "FROM parametro_estoque pe " +
                "JOIN produto p ON p.id = pe.produto_id " +
                "JOIN fornecedor f ON f.id = pe.fornecedor_id " +
                "WHERE pe.produto_id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produtoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearParametro(rs);
                }
            }
        }
        return null;
    }

    private ParametroEstoque mapearParametro(ResultSet rs) throws SQLException {
        ParametroEstoque parametro = new ParametroEstoque();
        parametro.setId(rs.getInt("id"));
        parametro.setProdutoId(rs.getInt("produto_id"));
        parametro.setFornecedorId(rs.getInt("fornecedor_id"));
        parametro.setMargemSeguranca(rs.getInt("margem_seguranca"));
        parametro.setProdutoNome(rs.getString("produto_nome"));
        parametro.setFornecedorNome(rs.getString("fornecedor_nome"));
        parametro.setLeadTimeDias(rs.getInt("lead_time_dias"));
        parametro.setEstoqueAtual(rs.getInt("estoque_atual"));
        return parametro;
    }

    public double calcularConsumoMedioDiario(int produtoId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(quantidade), 0) AS total_saida, " +
                "COALESCE(DATEDIFF(MAX(data), MIN(data)) + 1, 1) AS dias " +
                "FROM movimentacao_estoque " +
                "WHERE id_produto = ? AND LOWER(tipo) = 'saida' AND data >= DATE_SUB(CURDATE(), INTERVAL 90 DAY)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produtoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int dias = Math.max(rs.getInt("dias"), 1);
                    return rs.getDouble("total_saida") / dias;
                }
            }
        }
        return 0;
    }

    public boolean existeRascunhoParaProduto(int produtoId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ordem_compra oc " +
                "JOIN item_ordem_compra ioc ON ioc.ordem_compra_id = oc.id " +
                "WHERE oc.status = ? AND ioc.produto_id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, OrdemCompra.STATUS_RASCUNHO);
            stmt.setInt(2, produtoId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public boolean criarOrdemRascunho(int fornecedorId, int produtoId, int quantidade) throws SQLException {
        String sqlProduto = "SELECT valor FROM produto WHERE id = ?";
        String sqlOrdem = "INSERT INTO ordem_compra (fornecedor_id, status, valor_total, data_criacao) VALUES (?, ?, ?, ?)";
        String sqlItem = "INSERT INTO item_ordem_compra (ordem_compra_id, produto_id, quantidade, valor_unitario) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmtProduto = conn.prepareStatement(sqlProduto);
                 PreparedStatement stmtOrdem = conn.prepareStatement(sqlOrdem, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement stmtItem = conn.prepareStatement(sqlItem)) {

                stmtProduto.setInt(1, produtoId);
                double valorUnitario = 0;
                try (ResultSet rs = stmtProduto.executeQuery()) {
                    if (rs.next()) {
                        valorUnitario = rs.getDouble("valor");
                    }
                }

                stmtOrdem.setInt(1, fornecedorId);
                stmtOrdem.setString(2, OrdemCompra.STATUS_RASCUNHO);
                stmtOrdem.setDouble(3, valorUnitario * quantidade);
                stmtOrdem.setDate(4, Date.valueOf(LocalDate.now()));
                stmtOrdem.executeUpdate();

                int ordemId;
                try (ResultSet rs = stmtOrdem.getGeneratedKeys()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    ordemId = rs.getInt(1);
                }

                stmtItem.setInt(1, ordemId);
                stmtItem.setInt(2, produtoId);
                stmtItem.setInt(3, quantidade);
                stmtItem.setDouble(4, valorUnitario);
                stmtItem.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public List<OrdemCompra> listarOrdens() throws SQLException {
        List<OrdemCompra> ordens = new ArrayList<>();
        String sql = "SELECT oc.*, f.nome AS fornecedor_nome, ioc.id AS item_id, ioc.produto_id, ioc.quantidade, " +
                "ioc.valor_unitario, p.nome AS produto_nome " +
                "FROM ordem_compra oc " +
                "JOIN fornecedor f ON f.id = oc.fornecedor_id " +
                "JOIN item_ordem_compra ioc ON ioc.ordem_compra_id = oc.id " +
                "JOIN produto p ON p.id = ioc.produto_id " +
                "ORDER BY oc.data_criacao DESC, oc.id DESC";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ordens.add(mapearOrdem(rs));
            }
        }
        return ordens;
    }

    public OrdemCompra buscarOrdemPorId(int ordemId) throws SQLException {
        String sql = "SELECT oc.*, f.nome AS fornecedor_nome, ioc.id AS item_id, ioc.produto_id, ioc.quantidade, " +
                "ioc.valor_unitario, p.nome AS produto_nome " +
                "FROM ordem_compra oc " +
                "JOIN fornecedor f ON f.id = oc.fornecedor_id " +
                "JOIN item_ordem_compra ioc ON ioc.ordem_compra_id = oc.id " +
                "JOIN produto p ON p.id = ioc.produto_id " +
                "WHERE oc.id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ordemId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearOrdem(rs);
                }
            }
        }
        return null;
    }

    private OrdemCompra mapearOrdem(ResultSet rs) throws SQLException {
        OrdemCompra ordem = new OrdemCompra();
        ordem.setId(rs.getInt("id"));
        ordem.setFornecedorId(rs.getInt("fornecedor_id"));
        ordem.setFornecedorNome(rs.getString("fornecedor_nome"));
        ordem.setStatus(rs.getString("status"));
        ordem.setValorTotal(rs.getDouble("valor_total"));
        ordem.setDataCriacao(rs.getDate("data_criacao"));
        ordem.setDataAprovacao(rs.getDate("data_aprovacao"));
        ordem.setItemId(rs.getInt("item_id"));
        ordem.setProdutoId(rs.getInt("produto_id"));
        ordem.setProdutoNome(rs.getString("produto_nome"));
        ordem.setQuantidade(rs.getInt("quantidade"));
        ordem.setValorUnitario(rs.getDouble("valor_unitario"));
        return ordem;
    }

    public void atualizarRascunho(OrdemCompra ordem) throws SQLException {
        String sqlItem = "UPDATE item_ordem_compra ioc JOIN ordem_compra oc ON oc.id = ioc.ordem_compra_id " +
                "SET ioc.quantidade = ?, ioc.valor_unitario = ?, oc.valor_total = ? " +
                "WHERE oc.id = ? AND oc.status = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlItem)) {
            stmt.setInt(1, ordem.getQuantidade());
            stmt.setDouble(2, ordem.getValorUnitario());
            stmt.setDouble(3, ordem.getValorTotal());
            stmt.setInt(4, ordem.getId());
            stmt.setString(5, OrdemCompra.STATUS_RASCUNHO);
            stmt.executeUpdate();
        }
    }

    public void alterarStatus(int ordemId, String status) throws SQLException {
        String sql = "UPDATE ordem_compra SET status = ?, data_aprovacao = ? WHERE id = ? AND status = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setDate(2, OrdemCompra.STATUS_APROVADA.equals(status) ? Date.valueOf(LocalDate.now()) : null);
            stmt.setInt(3, ordemId);
            stmt.setString(4, OrdemCompra.STATUS_RASCUNHO);
            stmt.executeUpdate();
        }
    }
}
