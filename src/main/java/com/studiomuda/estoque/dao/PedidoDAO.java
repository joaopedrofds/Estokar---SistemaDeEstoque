package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.Pedido;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {
    private static final String STATUS_PAGAMENTO_PENDENTE = "PENDENTE";
    private static final String STATUS_PAGAMENTO_PAGO = "PAGO";

    public static class InadimplenciaInfo {
        private final boolean bloqueado;
        private final Integer pedidoPendenteId;
        private final LocalDate dataPedidoPendente;
        private final int diasAtraso;

        public InadimplenciaInfo(boolean bloqueado, Integer pedidoPendenteId, LocalDate dataPedidoPendente, int diasAtraso) {
            this.bloqueado = bloqueado;
            this.pedidoPendenteId = pedidoPendenteId;
            this.dataPedidoPendente = dataPedidoPendente;
            this.diasAtraso = diasAtraso;
        }

        public boolean isBloqueado() {
            return bloqueado;
        }

        public Integer getPedidoPendenteId() {
            return pedidoPendenteId;
        }

        public LocalDate getDataPedidoPendente() {
            return dataPedidoPendente;
        }

        public int getDiasAtraso() {
            return diasAtraso;
        }
    }

    public void inserir(Pedido pedido) throws SQLException {
        try (Connection conn = Conexao.getConnection()) {
            inserirComStatusPagamento(conn, pedido);
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
                lista.add(mapearPedido(rs));
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
                return mapearPedido(rs);
            }
        }
        return null;
    }

    public void atualizar(Pedido pedido) throws SQLException {
        try (Connection conn = Conexao.getConnection()) {
            atualizarComStatusPagamento(conn, pedido);
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
                lista.add(mapearPedido(rs));
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
                Pedido p = mapearPedido(rs);
                // Adicionais
                try {
                    p.setClienteCpfCnpj(rs.getString("cliente_cpf_cnpj"));
                } catch (SQLException ignored) {
                }
                try {
                    p.setCupomCodigo(rs.getString("cupom_codigo"));
                } catch (SQLException ignored) {
                }
                lista.add(p);
            }
        }
        return lista;
    }

    public InadimplenciaInfo verificarInadimplenciaCliente(int clienteId, int diasLimite) throws SQLException {
        String sql = "SELECT id, data_requisicao, DATEDIFF(CURDATE(), data_requisicao) AS dias_atraso " +
                "FROM pedido " +
                "WHERE cliente_id = ? " +
                "AND data_requisicao IS NOT NULL " +
                "AND COALESCE(UPPER(status_pagamento), 'PENDENTE') = 'PENDENTE' " +
                "AND DATEDIFF(CURDATE(), data_requisicao) > ? " +
                "ORDER BY data_requisicao ASC LIMIT 1";
        boolean deveUsarConsultaLegada = false;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            stmt.setInt(2, diasLimite);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Date dataPedido = rs.getDate("data_requisicao");
                    return new InadimplenciaInfo(
                            true,
                            rs.getInt("id"),
                            dataPedido != null ? dataPedido.toLocalDate() : null,
                            rs.getInt("dias_atraso"));
                }
            }
        } catch (SQLException e) {
            if (!isColunaInexistente(e)) {
                throw e;
            }
            deveUsarConsultaLegada = true;
        }

        if (deveUsarConsultaLegada) {
            return verificarInadimplenciaClienteLegado(clienteId, diasLimite);
        }
        return new InadimplenciaInfo(false, null, null, 0);
    }

    public void registrarAlertaFinanceiro(int clienteId, Integer pedidoId, int diasAtraso, String mensagem) throws SQLException {
        String sql = "INSERT INTO alerta_financeiro (cliente_id, pedido_id, dias_atraso, mensagem) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            if (pedidoId != null && pedidoId > 0) {
                stmt.setInt(2, pedidoId);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setInt(3, diasAtraso);
            stmt.setString(4, mensagem);
            stmt.executeUpdate();
        } catch (SQLException e) {
            if (!isTabelaOuColunaInexistente(e)) {
                throw e;
            }
        }
    }

    public List<LocalDate> listarDatasCompraPorCliente(int clienteId) throws SQLException {
        List<LocalDate> datas = new ArrayList<>();
        String sql = "SELECT data_requisicao FROM pedido WHERE cliente_id = ? AND data_requisicao IS NOT NULL ORDER BY data_requisicao ASC";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Date data = rs.getDate("data_requisicao");
                    if (data != null) {
                        datas.add(data.toLocalDate());
                    }
                }
            }
        }
        return datas;
    }

    private void inserirComStatusPagamento(Connection conn, Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedido (data_requisicao, data_entrega, cliente_id, funcionario_id, cupom_id, valor_desconto, status_pagamento, data_pagamento) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherCamposBase(stmt, pedido);
            stmt.setString(7, normalizarStatusPagamento(pedido.getStatusPagamento()));
            if (pedido.getDataPagamento() != null) {
                stmt.setDate(8, pedido.getDataPagamento());
            } else {
                stmt.setNull(8, Types.DATE);
            }
            stmt.executeUpdate();
            preencherIdGerado(pedido, stmt);
        } catch (SQLException e) {
            if (!isColunaInexistente(e)) {
                throw e;
            }
            inserirLegado(conn, pedido);
        }
    }

    private void inserirLegado(Connection conn, Pedido pedido) throws SQLException {
        String sqlLegado = "INSERT INTO pedido (data_requisicao, data_entrega, cliente_id, funcionario_id, cupom_id, valor_desconto) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sqlLegado, Statement.RETURN_GENERATED_KEYS)) {
            preencherCamposBase(stmt, pedido);
            stmt.executeUpdate();
            preencherIdGerado(pedido, stmt);
        }
    }

    private void atualizarComStatusPagamento(Connection conn, Pedido pedido) throws SQLException {
        String sql = "UPDATE pedido SET data_requisicao = ?, data_entrega = ?, cliente_id = ?, funcionario_id = ?, cupom_id = ?, valor_desconto = ?, status_pagamento = ?, data_pagamento = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencherCamposBase(stmt, pedido);
            stmt.setString(7, normalizarStatusPagamento(pedido.getStatusPagamento()));
            if (pedido.getDataPagamento() != null) {
                stmt.setDate(8, pedido.getDataPagamento());
            } else {
                stmt.setNull(8, Types.DATE);
            }
            stmt.setInt(9, pedido.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            if (!isColunaInexistente(e)) {
                throw e;
            }
            atualizarLegado(conn, pedido);
        }
    }

    private void atualizarLegado(Connection conn, Pedido pedido) throws SQLException {
        String sqlLegado = "UPDATE pedido SET data_requisicao = ?, data_entrega = ?, cliente_id = ?, funcionario_id = ?, cupom_id = ?, valor_desconto = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlLegado)) {
            preencherCamposBase(stmt, pedido);
            stmt.setInt(7, pedido.getId());
            stmt.executeUpdate();
        }
    }

    private void preencherCamposBase(PreparedStatement stmt, Pedido pedido) throws SQLException {
        stmt.setDate(1, pedido.getDataRequisicao());
        stmt.setDate(2, pedido.getDataEntrega());
        stmt.setInt(3, pedido.getClienteId());

        if (pedido.getFuncionarioId() > 0) {
            stmt.setInt(4, pedido.getFuncionarioId());
        } else {
            stmt.setNull(4, Types.INTEGER);
        }

        if (pedido.getCupomId() > 0) {
            stmt.setInt(5, pedido.getCupomId());
            stmt.setDouble(6, pedido.getValorDesconto());
        } else {
            stmt.setNull(5, Types.INTEGER);
            stmt.setDouble(6, 0.0);
        }
    }

    private void preencherIdGerado(Pedido pedido, PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
                pedido.setId(rs.getInt(1));
            }
        }
    }

    private Pedido mapearPedido(ResultSet rs) throws SQLException {
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
        try {
            p.setStatus(rs.getString("status"));
        } catch (SQLException ignored) {
            p.setStatus(null);
        }

        try {
            p.setStatusPagamento(normalizarStatusPagamento(rs.getString("status_pagamento")));
        } catch (SQLException ignored) {
            p.setStatusPagamento(inferirStatusPagamentoLegado(p));
        }

        try {
            p.setDataPagamento(rs.getDate("data_pagamento"));
        } catch (SQLException ignored) {
            if (STATUS_PAGAMENTO_PAGO.equalsIgnoreCase(p.getStatusPagamento())) {
                p.setDataPagamento(p.getDataEntrega());
            } else {
                p.setDataPagamento(null);
            }
        }

        p.setDiasAtrasoPagamento(calcularDiasAtrasoPagamento(p));
        return p;
    }

    private Integer calcularDiasAtrasoPagamento(Pedido pedido) {
        if (pedido.getDataRequisicao() == null || STATUS_PAGAMENTO_PAGO.equalsIgnoreCase(pedido.getStatusPagamento())) {
            return 0;
        }
        long dias = ChronoUnit.DAYS.between(pedido.getDataRequisicao().toLocalDate(), LocalDate.now());
        return (int) Math.max(dias, 0);
    }

    private InadimplenciaInfo verificarInadimplenciaClienteLegado(int clienteId, int diasLimite) throws SQLException {
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
                    Date dataPedido = rs.getDate("data_requisicao");
                    return new InadimplenciaInfo(
                            true,
                            rs.getInt("id"),
                            dataPedido != null ? dataPedido.toLocalDate() : null,
                            rs.getInt("dias_atraso"));
                }
            }
        }

        return new InadimplenciaInfo(false, null, null, 0);
    }

    String normalizarStatusPagamento(String statusPagamento) {
        if (statusPagamento == null || statusPagamento.trim().isEmpty()) {
            return STATUS_PAGAMENTO_PENDENTE;
        }
        if (STATUS_PAGAMENTO_PAGO.equalsIgnoreCase(statusPagamento.trim())) {
            return STATUS_PAGAMENTO_PAGO;
        }
        return STATUS_PAGAMENTO_PENDENTE;
    }

    String inferirStatusPagamentoLegado(Pedido pedido) {
        if (pedido.getDataEntrega() != null && !pedido.getDataEntrega().toLocalDate().isAfter(LocalDate.now())) {
            return STATUS_PAGAMENTO_PAGO;
        }
        return STATUS_PAGAMENTO_PENDENTE;
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
