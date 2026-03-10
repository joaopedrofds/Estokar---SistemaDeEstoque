package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.Pedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    public void inserir(Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedido (data_requisicao, data_entrega, cliente_id, funcionario_id, cupom_id, valor_desconto) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, pedido.getDataRequisicao());
            stmt.setDate(2, pedido.getDataEntrega());
            stmt.setInt(3, pedido.getClienteId());
            
            // Lidar com valores que podem ser zero (não definidos)
            if (pedido.getFuncionarioId() > 0) {
                stmt.setInt(4, pedido.getFuncionarioId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            // Quando cupomId é 0, salva como NULL no banco
            if (pedido.getCupomId() > 0) {
                stmt.setInt(5, pedido.getCupomId());
                stmt.setDouble(6, pedido.getValorDesconto());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
                stmt.setDouble(6, 0.0);
            }
            
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                pedido.setId(rs.getInt(1));
            }
        }
    }

    public List<Pedido> listar() throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT p.*, c.nome as cliente_nome, f.nome as funcionario_nome, f.cargo as funcionario_cargo " +
                     "FROM pedido p " +
                     "LEFT JOIN cliente c ON p.cliente_id = c.id " +
                     "LEFT JOIN funcionario f ON p.funcionario_id = f.id";

        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Pedido p = new Pedido(
                        rs.getInt("id"),
                        rs.getDate("data_requisicao"),
                        rs.getDate("data_entrega"),
                        rs.getInt("cliente_id"),
                        rs.getInt("cupom_id"),
                        rs.getInt("funcionario_id"),
                        rs.getDouble("valor_desconto")
                );
                p.setClienteNome(rs.getString("cliente_nome"));
                p.setFuncionarioNome(rs.getString("funcionario_nome"));
                p.setFuncionarioCargo(rs.getString("funcionario_cargo"));
                try { p.setStatus(rs.getString("status")); } catch (SQLException e) { p.setStatus(null); }
                lista.add(p);
            }
        }
        return lista;
    }

    public Pedido buscarPorId(int id) throws SQLException {
        String sql = "SELECT p.*, c.nome as cliente_nome, f.nome as funcionario_nome, f.cargo as funcionario_cargo " +
                     "FROM pedido p " +
                     "LEFT JOIN cliente c ON p.cliente_id = c.id " +
                     "LEFT JOIN funcionario f ON p.funcionario_id = f.id " +
                     "WHERE p.id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Pedido p = new Pedido(
                        rs.getInt("id"),
                        rs.getDate("data_requisicao"),
                        rs.getDate("data_entrega"),
                        rs.getInt("cliente_id"),
                        rs.getInt("cupom_id"),
                        rs.getInt("funcionario_id"),
                        rs.getDouble("valor_desconto")
                );
                p.setClienteNome(rs.getString("cliente_nome"));
                p.setFuncionarioNome(rs.getString("funcionario_nome"));
                p.setFuncionarioCargo(rs.getString("funcionario_cargo"));
                try { p.setStatus(rs.getString("status")); } catch (SQLException e) { p.setStatus(null); }
                return p;
            }
        }
        return null;
    }

    public void atualizar(Pedido pedido) throws SQLException {
        String sql = "UPDATE pedido SET data_requisicao = ?, data_entrega = ?, cliente_id = ?, funcionario_id = ?, cupom_id = ?, valor_desconto = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, pedido.getDataRequisicao());
            stmt.setDate(2, pedido.getDataEntrega());
            stmt.setInt(3, pedido.getClienteId());
            
            // Lidar com valores que podem ser zero (não definidos)
            if (pedido.getFuncionarioId() > 0) {
                stmt.setInt(4, pedido.getFuncionarioId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            if (pedido.getCupomId() > 0) {
                stmt.setInt(5, pedido.getCupomId());
                stmt.setDouble(6, pedido.getValorDesconto());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
                stmt.setDouble(6, 0.0);
            }
            
            stmt.setInt(7, pedido.getId());
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM pedido WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Pedido> listarPorCliente(int clienteId) throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT p.*, c.nome as cliente_nome, f.nome as funcionario_nome, f.cargo as funcionario_cargo " +
                     "FROM pedido p " +
                     "LEFT JOIN cliente c ON p.cliente_id = c.id " +
                     "LEFT JOIN funcionario f ON p.funcionario_id = f.id " +
                     "WHERE p.cliente_id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Pedido p = new Pedido(
                        rs.getInt("id"),
                        rs.getDate("data_requisicao"),
                        rs.getDate("data_entrega"),
                        rs.getInt("cliente_id"),
                        rs.getInt("cupom_id"),
                        rs.getInt("funcionario_id"),
                        rs.getDouble("valor_desconto")
                );
                p.setClienteNome(rs.getString("cliente_nome"));
                p.setFuncionarioNome(rs.getString("funcionario_nome"));
                p.setFuncionarioCargo(rs.getString("funcionario_cargo"));
                try { p.setStatus(rs.getString("status")); } catch (SQLException e) { p.setStatus(null); }
                lista.add(p);
            }
        }
        return lista;
    }

    public int obterUltimoId() throws SQLException {
        String sql = "SELECT MAX(id) AS ultimo_id FROM pedido";
        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("ultimo_id");
            }
        }
        return 0;
    }

    /**
     * Busca pedidos com múltiplos filtros reais (cliente, status, datas, funcionário, cupom).
     * Parâmetros podem ser nulos para não filtrar por eles.
     */
    public List<Pedido> buscarComFiltros(String cpfCnpj, String status, Date dataInicio, Date dataFim, Integer funcionarioId, Integer clienteId, Integer cupomId) throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT p.*, c.nome as cliente_nome, c.cpf_cnpj as cliente_cpf_cnpj, f.nome as funcionario_nome, f.cargo as funcionario_cargo, cu.codigo as cupom_codigo " +
            "FROM pedido p " +
            "LEFT JOIN cliente c ON p.cliente_id = c.id " +
            "LEFT JOIN funcionario f ON p.funcionario_id = f.id " +
            "LEFT JOIN cupom cu ON p.cupom_id = cu.id WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        if (cpfCnpj != null && !cpfCnpj.trim().isEmpty()) {
            sql.append(" AND c.cpf_cnpj LIKE ?");
            params.add("%" + cpfCnpj.trim() + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND p.status = ?");
            params.add(status.trim());
        }
        if (dataInicio != null) {
            sql.append(" AND p.data_requisicao >= ?");
            params.add(dataInicio);
        }
        if (dataFim != null) {
            sql.append(" AND p.data_requisicao <= ?");
            params.add(dataFim);
        }
        if (funcionarioId != null && funcionarioId > 0) {
            sql.append(" AND p.funcionario_id = ?");
            params.add(funcionarioId);
        }
        if (clienteId != null && clienteId > 0) {
            sql.append(" AND p.cliente_id = ?");
            params.add(clienteId);
        }
        if (cupomId != null && cupomId > 0) {
            sql.append(" AND p.cupom_id = ?");
            params.add(cupomId);
        }

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Pedido p = new Pedido(
                        rs.getInt("id"),
                        rs.getDate("data_requisicao"),
                        rs.getDate("data_entrega"),
                        rs.getInt("cliente_id"),
                        rs.getInt("cupom_id"),
                        rs.getInt("funcionario_id"),
                        rs.getDouble("valor_desconto")
                );
                p.setClienteNome(rs.getString("cliente_nome"));
                p.setFuncionarioNome(rs.getString("funcionario_nome"));
                p.setFuncionarioCargo(rs.getString("funcionario_cargo"));
                try { p.setStatus(rs.getString("status")); } catch (SQLException e) { p.setStatus(null); }
                // Adicionais
                try { p.setClienteCpfCnpj(rs.getString("cliente_cpf_cnpj")); } catch (SQLException e) {}
                try { p.setCupomCodigo(rs.getString("cupom_codigo")); } catch (SQLException e) {}
                lista.add(p);
            }
        }
        return lista;
    }
}
