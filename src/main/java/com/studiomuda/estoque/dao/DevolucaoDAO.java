package com.studiomuda.estoque.dao;
import org.springframework.stereotype.Repository;




import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.Devolucao;
import com.studiomuda.estoque.model.ItemDevolucao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Repository
public class DevolucaoDAO {
    public void inserir(Devolucao d) throws SQLException {
        String sql = "INSERT INTO devolucao (pedido_id, cliente_id, motivo, tipo_restituicao, status) VALUES (?, ?, ?, ?, 'PENDENTE')";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, d.getPedidoId());
            stmt.setInt(2, d.getClienteId());
            stmt.setString(3, d.getMotivo());
            stmt.setString(4, d.getTipoRestituicao());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) d.setId(rs.getInt(1));
            }
        }
    }
    public void atualizarStatus(int id, String status, String observacao) throws SQLException {
        String sql = "UPDATE devolucao SET status = ?, observacao_gestor = ?, data_resolucao = NOW() WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, observacao);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        }
    }
    public List<Devolucao> listarTodas() throws SQLException {
        List<Devolucao> lista = new ArrayList<>();
        String sql = "SELECT * FROM devolucao ORDER BY data_solicitacao DESC";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Devolucao d = new Devolucao();
                d.setId(rs.getInt("id"));
                d.setPedidoId(rs.getInt("pedido_id"));
                d.setClienteId(rs.getInt("cliente_id"));
                d.setMotivo(rs.getString("motivo"));
                d.setTipoRestituicao(rs.getString("tipo_restituicao"));
                d.setStatus(rs.getString("status"));
                d.setObservacaoGestor(rs.getString("observacao_gestor"));
                d.setDataSolicitacao(rs.getTimestamp("data_solicitacao") != null ?
                        rs.getTimestamp("data_solicitacao").toLocalDateTime() : null);
                d.setDataResolucao(rs.getTimestamp("data_resolucao") != null ?
                        rs.getTimestamp("data_resolucao").toLocalDateTime() : null);
                lista.add(d);
            }
        }
        return lista;
    }
    public Devolucao buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM devolucao WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Devolucao d = new Devolucao();
                    d.setId(rs.getInt("id"));
                    d.setPedidoId(rs.getInt("pedido_id"));
                    d.setClienteId(rs.getInt("cliente_id"));
                    d.setMotivo(rs.getString("motivo"));
                    d.setTipoRestituicao(rs.getString("tipo_restituicao"));
                    d.setStatus(rs.getString("status"));
                    d.setObservacaoGestor(rs.getString("observacao_gestor"));
                    d.setDataSolicitacao(rs.getTimestamp("data_solicitacao") != null ?
                            rs.getTimestamp("data_solicitacao").toLocalDateTime() : null);
                    d.setDataResolucao(rs.getTimestamp("data_resolucao") != null ?
                            rs.getTimestamp("data_resolucao").toLocalDateTime() : null);
                    return d;
                }
            }
        }
        return null;
    }
    public void inserirItem(ItemDevolucao item) throws SQLException {
        String sql = "INSERT INTO item_devolucao (devolucao_id, produto_id, quantidade) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, item.getDevolucaoId());
            stmt.setInt(2, item.getProdutoId());
            stmt.setInt(3, item.getQuantidade());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) item.setId(rs.getInt(1));
            }
        }
    }
    public List<ItemDevolucao> buscarItensPorDevolucaoId(int devolucaoId) throws SQLException {
        List<ItemDevolucao> itens = new ArrayList<>();
        String sql = "SELECT * FROM item_devolucao WHERE devolucao_id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, devolucaoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ItemDevolucao item = new ItemDevolucao();
                    item.setId(rs.getInt("id"));
                    item.setDevolucaoId(rs.getInt("devolucao_id"));
                    item.setProdutoId(rs.getInt("produto_id"));
                    item.setQuantidade(rs.getInt("quantidade"));
                    itens.add(item);
                }
            }
        }
        return itens;
    }
}
