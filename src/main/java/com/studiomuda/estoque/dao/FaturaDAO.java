package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.Fatura;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

public class FaturaDAO {
    public Fatura buscarMaiorAbertaPorCliente(int clienteId, LocalDate dataReferencia) throws SQLException {
        String sql = "SELECT id, cliente_id, pedido_id, data_vencimento, data_pagamento, valor, status, " +
                "GREATEST(DATEDIFF(?, data_vencimento), 0) AS dias_atraso " +
                "FROM fatura WHERE cliente_id = ? AND UPPER(status) = 'ABERTA' AND data_pagamento IS NULL " +
                "ORDER BY dias_atraso DESC, data_vencimento ASC LIMIT 1";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(dataReferencia));
            stmt.setInt(2, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            if (!isTabelaInexistente(e)) {
                throw e;
            }
            return buscarMaiorPedidoPendenteComoFatura(clienteId, dataReferencia);
        }
        return null;
    }

    public void inserir(Fatura fatura) throws SQLException {
        String sql = "INSERT INTO fatura (cliente_id, pedido_id, data_vencimento, data_pagamento, valor, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fatura.getClienteId());
            if (fatura.getPedidoId() != null && fatura.getPedidoId() > 0) {
                stmt.setInt(2, fatura.getPedidoId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setDate(3, Date.valueOf(fatura.getDataVencimento()));
            if (fatura.getDataPagamento() != null) {
                stmt.setDate(4, Date.valueOf(fatura.getDataPagamento()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            stmt.setBigDecimal(5, fatura.getValor());
            stmt.setString(6, fatura.getStatus() != null ? fatura.getStatus() : "ABERTA");
            stmt.executeUpdate();
        }
    }

    private Fatura buscarMaiorPedidoPendenteComoFatura(int clienteId, LocalDate dataReferencia) throws SQLException {
        String sql = "SELECT id, cliente_id, id AS pedido_id, data_requisicao AS data_vencimento, data_pagamento, " +
                "0 AS valor, status_pagamento AS status, GREATEST(DATEDIFF(?, data_requisicao), 0) AS dias_atraso " +
                "FROM pedido WHERE cliente_id = ? AND data_requisicao IS NOT NULL " +
                "AND COALESCE(UPPER(status_pagamento), 'PENDENTE') = 'PENDENTE' " +
                "ORDER BY dias_atraso DESC, data_requisicao ASC LIMIT 1";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(dataReferencia));
            stmt.setInt(2, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    private Fatura mapear(ResultSet rs) throws SQLException {
        Fatura fatura = new Fatura();
        fatura.setId(rs.getInt("id"));
        fatura.setClienteId(rs.getInt("cliente_id"));
        fatura.setPedidoId((Integer) rs.getObject("pedido_id"));
        Date dataVencimento = rs.getDate("data_vencimento");
        Date dataPagamento = rs.getDate("data_pagamento");
        fatura.setDataVencimento(dataVencimento != null ? dataVencimento.toLocalDate() : null);
        fatura.setDataPagamento(dataPagamento != null ? dataPagamento.toLocalDate() : null);
        fatura.setValor(rs.getBigDecimal("valor"));
        fatura.setStatus(rs.getString("status"));
        fatura.setDiasAtraso(rs.getInt("dias_atraso"));
        return fatura;
    }

    private boolean isTabelaInexistente(SQLException e) {
        return "42S02".equals(e.getSQLState())
                || (e.getMessage() != null && e.getMessage().toLowerCase().contains("doesn't exist"));
    }
}
