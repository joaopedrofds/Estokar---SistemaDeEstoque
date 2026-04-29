package com.studiomuda.estoque.infrastructure.persistence.pedido;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.domain.pedido.AnaliseInadimplencia;
import com.studiomuda.estoque.domain.pedido.Pedido;
import com.studiomuda.estoque.domain.pedido.PedidoComJoins;
import com.studiomuda.estoque.domain.pedido.PedidoRepository;
import com.studiomuda.estoque.domain.pedido.StatusPagamento;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PedidoRepositoryJdbc implements PedidoRepository {

    private static final String STATUS_PAGAMENTO_PENDENTE = "PENDENTE";
    private static final String STATUS_PAGAMENTO_PAGO = "PAGO";

    @Override
    public Pedido salvar(Pedido pedido) {
        try (Connection conn = Conexao.getConnection()) {
            return inserirComStatusPagamento(conn, pedido);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Pedido pedido) {
        try (Connection conn = Conexao.getConnection()) {
            atualizarComStatusPagamento(conn, pedido);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Pedido> buscarPorId(int id) {
        String sql = "SELECT p.* FROM pedido p WHERE p.id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapearPedido(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pedido: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<PedidoComJoins> listarTodos() {
        String sql = "SELECT p.*, c.nome AS cliente_nome, c.cpf_cnpj AS cliente_cpf_cnpj, " +
                "f.nome AS funcionario_nome, f.cargo AS funcionario_cargo, cu.codigo AS cupom_codigo " +
                "FROM pedido p " +
                "LEFT JOIN cliente c ON p.cliente_id = c.id " +
                "LEFT JOIN funcionario f ON p.funcionario_id = f.id " +
                "LEFT JOIN cupom cu ON p.cupom_id = cu.id";
        return executarLista(sql, new ArrayList<>());
    }

    @Override
    public List<PedidoComJoins> listarPorCliente(int clienteId) {
        String sql = "SELECT p.*, c.nome AS cliente_nome, c.cpf_cnpj AS cliente_cpf_cnpj, " +
                "f.nome AS funcionario_nome, f.cargo AS funcionario_cargo, cu.codigo AS cupom_codigo " +
                "FROM pedido p " +
                "LEFT JOIN cliente c ON p.cliente_id = c.id " +
                "LEFT JOIN funcionario f ON p.funcionario_id = f.id " +
                "LEFT JOIN cupom cu ON p.cupom_id = cu.id WHERE p.cliente_id = ?";
        List<Object> params = new ArrayList<>();
        params.add(clienteId);
        return executarLista(sql, params);
    }

    @Override
    public List<PedidoComJoins> buscarComFiltros(String cpfCnpj, String status, LocalDate dataInicio,
                                                  LocalDate dataFim, Integer funcionarioId,
                                                  Integer clienteId, Integer cupomId) {
        StringBuilder sql = new StringBuilder(
                "SELECT p.*, c.nome AS cliente_nome, c.cpf_cnpj AS cliente_cpf_cnpj, " +
                        "f.nome AS funcionario_nome, f.cargo AS funcionario_cargo, cu.codigo AS cupom_codigo " +
                        "FROM pedido p " +
                        "LEFT JOIN cliente c ON p.cliente_id = c.id " +
                        "LEFT JOIN funcionario f ON p.funcionario_id = f.id " +
                        "LEFT JOIN cupom cu ON p.cupom_id = cu.id WHERE 1=1");
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
            params.add(Date.valueOf(dataInicio));
        }
        if (dataFim != null) {
            sql.append(" AND p.data_requisicao <= ?");
            params.add(Date.valueOf(dataFim));
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
        return executarLista(sql.toString(), params);
    }

    @Override
    public void remover(int id) {
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM pedido WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public AnaliseInadimplencia verificarInadimplenciaCliente(int clienteId, int diasLimite) {
        String sql = "SELECT id, data_requisicao, DATEDIFF(CURDATE(), data_requisicao) AS dias_atraso " +
                "FROM pedido " +
                "WHERE cliente_id = ? " +
                "AND data_requisicao IS NOT NULL " +
                "AND COALESCE(UPPER(status_pagamento), 'PENDENTE') = 'PENDENTE' " +
                "AND DATEDIFF(CURDATE(), data_requisicao) > ? " +
                "ORDER BY data_requisicao ASC LIMIT 1";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            stmt.setInt(2, diasLimite);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Date dataPedido = rs.getDate("data_requisicao");
                    return AnaliseInadimplencia.bloqueado(
                            rs.getInt("id"),
                            dataPedido != null ? dataPedido.toLocalDate() : null,
                            rs.getInt("dias_atraso"));
                }
            }
        } catch (SQLException e) {
            if (isColunaInexistente(e)) {
                return verificarInadimplenciaLegado(clienteId, diasLimite);
            }
            throw new RuntimeException("Erro ao verificar inadimplência: " + e.getMessage(), e);
        }
        return AnaliseInadimplencia.naoBloqueado();
    }

    @Override
    public void registrarAlertaFinanceiro(int clienteId, Integer pedidoId, int diasAtraso, String mensagem) {
        String sql = "INSERT INTO alerta_financeiro (cliente_id, pedido_id, dias_atraso, mensagem) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            if (pedidoId != null && pedidoId > 0) stmt.setInt(2, pedidoId);
            else stmt.setNull(2, Types.INTEGER);
            stmt.setInt(3, diasAtraso);
            stmt.setString(4, mensagem);
            stmt.executeUpdate();
        } catch (SQLException e) {
            if (!isTabelaOuColunaInexistente(e)) {
                throw new RuntimeException("Erro ao registrar alerta financeiro: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public List<LocalDate> listarDatasCompraPorCliente(int clienteId) {
        List<LocalDate> datas = new ArrayList<>();
        String sql = "SELECT data_requisicao FROM pedido WHERE cliente_id = ? AND data_requisicao IS NOT NULL ORDER BY data_requisicao ASC";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Date data = rs.getDate("data_requisicao");
                    if (data != null) datas.add(data.toLocalDate());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar datas: " + e.getMessage(), e);
        }
        return datas;
    }

    private List<PedidoComJoins> executarLista(String sql, List<Object> params) {
        List<PedidoComJoins> lista = new ArrayList<>();
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) stmt.setObject(i + 1, params.get(i));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Pedido pedido = mapearPedido(rs);
                    lista.add(new PedidoComJoins(
                            pedido,
                            rs.getString("cliente_nome"),
                            seguro(rs, "cliente_cpf_cnpj"),
                            rs.getString("funcionario_nome"),
                            rs.getString("funcionario_cargo"),
                            seguro(rs, "cupom_codigo")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos: " + e.getMessage(), e);
        }
        return lista;
    }

    private String seguro(ResultSet rs, String coluna) {
        try { return rs.getString(coluna); } catch (SQLException ignored) { return null; }
    }

    private Pedido inserirComStatusPagamento(Connection conn, Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedido (data_requisicao, data_entrega, cliente_id, funcionario_id, cupom_id, valor_desconto, status_pagamento, data_pagamento) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherCamposBase(stmt, pedido);
            stmt.setString(7, pedido.statusPagamento() == StatusPagamento.PAGO ? STATUS_PAGAMENTO_PAGO : STATUS_PAGAMENTO_PENDENTE);
            if (pedido.dataPagamento() != null) stmt.setDate(8, Date.valueOf(pedido.dataPagamento()));
            else stmt.setNull(8, Types.DATE);
            stmt.executeUpdate();
            return pedidoComIdGerado(pedido, stmt);
        } catch (SQLException e) {
            if (!isColunaInexistente(e)) throw e;
            return inserirLegado(conn, pedido);
        }
    }

    private Pedido inserirLegado(Connection conn, Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedido (data_requisicao, data_entrega, cliente_id, funcionario_id, cupom_id, valor_desconto) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherCamposBase(stmt, pedido);
            stmt.executeUpdate();
            return pedidoComIdGerado(pedido, stmt);
        }
    }

    private void atualizarComStatusPagamento(Connection conn, Pedido pedido) throws SQLException {
        String sql = "UPDATE pedido SET data_requisicao = ?, data_entrega = ?, cliente_id = ?, funcionario_id = ?, cupom_id = ?, valor_desconto = ?, status_pagamento = ?, data_pagamento = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencherCamposBase(stmt, pedido);
            stmt.setString(7, pedido.statusPagamento() == StatusPagamento.PAGO ? STATUS_PAGAMENTO_PAGO : STATUS_PAGAMENTO_PENDENTE);
            if (pedido.dataPagamento() != null) stmt.setDate(8, Date.valueOf(pedido.dataPagamento()));
            else stmt.setNull(8, Types.DATE);
            stmt.setInt(9, pedido.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            if (!isColunaInexistente(e)) throw e;
            atualizarLegado(conn, pedido);
        }
    }

    private void atualizarLegado(Connection conn, Pedido pedido) throws SQLException {
        String sql = "UPDATE pedido SET data_requisicao = ?, data_entrega = ?, cliente_id = ?, funcionario_id = ?, cupom_id = ?, valor_desconto = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencherCamposBase(stmt, pedido);
            stmt.setInt(7, pedido.id());
            stmt.executeUpdate();
        }
    }

    private void preencherCamposBase(PreparedStatement stmt, Pedido pedido) throws SQLException {
        stmt.setDate(1, pedido.dataRequisicao() != null ? Date.valueOf(pedido.dataRequisicao()) : null);
        stmt.setDate(2, pedido.dataEntrega() != null ? Date.valueOf(pedido.dataEntrega()) : null);
        stmt.setInt(3, pedido.clienteId());
        if (pedido.funcionarioId() > 0) stmt.setInt(4, pedido.funcionarioId());
        else stmt.setNull(4, Types.INTEGER);
        if (pedido.cupomId() != null && pedido.cupomId() > 0) {
            stmt.setInt(5, pedido.cupomId());
            stmt.setDouble(6, pedido.valorDesconto());
        } else {
            stmt.setNull(5, Types.INTEGER);
            stmt.setDouble(6, 0.0);
        }
    }

    private Pedido pedidoComIdGerado(Pedido pedido, PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
                int novoId = rs.getInt(1);
                return new Pedido(novoId, pedido.dataRequisicao(), pedido.dataEntrega(),
                        pedido.clienteId(), pedido.funcionarioId(), pedido.cupomId(),
                        pedido.valorDesconto(), pedido.status(), pedido.statusPagamento(), pedido.dataPagamento());
            }
        }
        return pedido;
    }

    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        Date req = rs.getDate("data_requisicao");
        Date ent = rs.getDate("data_entrega");
        int cupomIdRaw = rs.getInt("cupom_id");
        Integer cupomId = rs.wasNull() ? null : cupomIdRaw;

        String status;
        try { status = rs.getString("status"); }
        catch (SQLException ignored) { status = null; }

        StatusPagamento statusPag;
        try {
            statusPag = StatusPagamento.fromCodigo(rs.getString("status_pagamento"));
        } catch (SQLException ignored) {
            statusPag = ent != null && !ent.toLocalDate().isAfter(LocalDate.now())
                    ? StatusPagamento.PAGO : StatusPagamento.PENDENTE;
        }

        LocalDate dataPag;
        try {
            Date dp = rs.getDate("data_pagamento");
            dataPag = dp != null ? dp.toLocalDate() : null;
        } catch (SQLException ignored) {
            dataPag = (statusPag == StatusPagamento.PAGO && ent != null) ? ent.toLocalDate() : null;
        }

        return new Pedido(
                rs.getInt("id"),
                req != null ? req.toLocalDate() : null,
                ent != null ? ent.toLocalDate() : null,
                rs.getInt("cliente_id"),
                rs.getInt("funcionario_id"),
                cupomId,
                rs.getDouble("valor_desconto"),
                status,
                statusPag,
                dataPag);
    }

    private AnaliseInadimplencia verificarInadimplenciaLegado(int clienteId, int diasLimite) {
        String sql = "SELECT id, data_requisicao, DATEDIFF(CURDATE(), data_requisicao) AS dias_atraso " +
                "FROM pedido " +
                "WHERE cliente_id = ? " +
                "AND data_requisicao IS NOT NULL " +
                "AND (data_entrega IS NULL OR data_entrega > CURDATE()) " +
                "AND DATEDIFF(CURDATE(), data_requisicao) > ? " +
                "ORDER BY data_requisicao ASC LIMIT 1";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            stmt.setInt(2, diasLimite);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Date dp = rs.getDate("data_requisicao");
                    return AnaliseInadimplencia.bloqueado(
                            rs.getInt("id"),
                            dp != null ? dp.toLocalDate() : null,
                            rs.getInt("dias_atraso"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro inadimplência legado: " + e.getMessage(), e);
        }
        return AnaliseInadimplencia.naoBloqueado();
    }

    private boolean isColunaInexistente(SQLException e) {
        return "42S22".equals(e.getSQLState())
                || (e.getMessage() != null && e.getMessage().toLowerCase().contains("unknown column"));
    }

    private boolean isTabelaOuColunaInexistente(SQLException e) {
        return isColunaInexistente(e)
                || "42S02".equals(e.getSQLState())
                || (e.getMessage() != null && e.getMessage().toLowerCase().contains("doesn't exist"));
    }
}
