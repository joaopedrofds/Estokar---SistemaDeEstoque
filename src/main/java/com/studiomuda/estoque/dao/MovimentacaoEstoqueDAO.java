package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.MovimentacaoEstoque;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoEstoqueDAO {

    public void registrar(MovimentacaoEstoque mov) throws SQLException {
        String sqlMov = "INSERT INTO movimentacao_estoque (id_produto, tipo, quantidade, motivo, data) VALUES (?, ?, ?, ?, ?)";
        String sqlEstoque = "UPDATE produto SET quantidade = quantidade + ? WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmtMov = conn.prepareStatement(sqlMov, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement stmtEstoque = conn.prepareStatement(sqlEstoque)) {

            conn.setAutoCommit(false);

            stmtMov.setInt(1, mov.getIdProduto());
            stmtMov.setString(2, mov.getTipo());
            stmtMov.setInt(3, mov.getQuantidade());
            stmtMov.setString(4, mov.getMotivo());
            stmtMov.setDate(5, mov.getData());
            stmtMov.executeUpdate();
            
            ResultSet rs = stmtMov.getGeneratedKeys();
            if (rs.next()) {
                mov.setId(rs.getInt(1));
            }

            int fator = mov.getTipo().equalsIgnoreCase("saida") ? -1 : 1;
            stmtEstoque.setInt(1, fator * mov.getQuantidade());
            stmtEstoque.setInt(2, mov.getIdProduto());
            stmtEstoque.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            try (Connection conn = Conexao.getConnection()) {
                conn.rollback();
            } catch (SQLException ex) {
                throw new SQLException("Erro ao fazer rollback: " + ex.getMessage());
            }
            throw e;
        }
    }

    public List<MovimentacaoEstoque> listar() throws SQLException {
        List<MovimentacaoEstoque> lista = new ArrayList<>();
        String sql = "SELECT me.*, p.nome as produto_nome FROM movimentacao_estoque me " +
                     "JOIN produto p ON me.id_produto = p.id " +
                     "ORDER BY me.data DESC";

        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                MovimentacaoEstoque m = new MovimentacaoEstoque(
                        rs.getInt("id"),
                        rs.getInt("id_produto"),
                        rs.getString("tipo"),
                        rs.getInt("quantidade"),
                        rs.getString("motivo"),
                        rs.getDate("data")
                );
                m.setProdutoNome(rs.getString("produto_nome"));
                lista.add(m);
            }
        }
        return lista;
    }

    public MovimentacaoEstoque buscarPorId(int id) throws SQLException {
        String sql = "SELECT me.*, p.nome as produto_nome FROM movimentacao_estoque me " +
                     "JOIN produto p ON me.id_produto = p.id " +
                     "WHERE me.id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                MovimentacaoEstoque m = new MovimentacaoEstoque(
                        rs.getInt("id"),
                        rs.getInt("id_produto"),
                        rs.getString("tipo"),
                        rs.getInt("quantidade"),
                        rs.getString("motivo"),
                        rs.getDate("data")
                );
                m.setProdutoNome(rs.getString("produto_nome"));
                return m;
            }
        }
        return null;
    }

    public void atualizar(MovimentacaoEstoque mov) throws SQLException {
        String sql = "UPDATE movimentacao_estoque SET motivo = ?, data = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mov.getMotivo());
            stmt.setDate(2, mov.getData());
            stmt.setInt(3, mov.getId());
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        // Primeiro, buscar a movimentação para obter informações necessárias para o estorno
        MovimentacaoEstoque movimentacao = buscarPorId(id);
        if (movimentacao == null) {
            throw new SQLException("Nenhuma movimentação encontrada com o ID: " + id);
        }
        
        String sqlDelete = "DELETE FROM movimentacao_estoque WHERE id = ?";
        String sqlEstorno = "UPDATE produto SET quantidade = quantidade - ? WHERE id = ?";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete);
             PreparedStatement stmtEstorno = conn.prepareStatement(sqlEstorno)) {
            
            conn.setAutoCommit(false);
            
            // Calcular o valor a reverter no estoque (negativo da operação original)
            int fator = movimentacao.getTipo().equalsIgnoreCase("saida") ? -1 : 1;
            int valorReverter = fator * movimentacao.getQuantidade();
            
            // Aplicar o estorno no estoque
            stmtEstorno.setInt(1, valorReverter);
            stmtEstorno.setInt(2, movimentacao.getIdProduto());
            stmtEstorno.executeUpdate();
            
            // Excluir a movimentação
            stmtDelete.setInt(1, id);
            stmtDelete.executeUpdate();
            
            conn.commit();
            System.out.println("Movimentação excluída com sucesso e estoque ajustado! ID: " + id);
        } catch (SQLException e) {
            try (Connection conn = Conexao.getConnection()) {
                conn.rollback();
            } catch (SQLException ex) {
                throw new SQLException("Erro ao fazer rollback: " + ex.getMessage());
            }
            throw e;
        }
    }

    public List<MovimentacaoEstoque> buscarComFiltros(String produto, String tipo, String dataInicio, String dataFim) throws SQLException {
        List<MovimentacaoEstoque> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT me.*, p.nome as produto_nome FROM movimentacao_estoque me JOIN produto p ON me.id_produto = p.id WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (produto != null && !produto.trim().isEmpty()) {
            sql.append(" AND p.nome LIKE ?");
            params.add("%" + produto.trim() + "%");
        }
        if (tipo != null && !tipo.trim().isEmpty()) {
            sql.append(" AND me.tipo = ?");
            params.add(tipo.trim());
        }
        if (dataInicio != null && !dataInicio.trim().isEmpty()) {
            sql.append(" AND me.data >= ?");
            params.add(java.sql.Date.valueOf(dataInicio));
        }
        if (dataFim != null && !dataFim.trim().isEmpty()) {
            sql.append(" AND me.data <= ?");
            params.add(java.sql.Date.valueOf(dataFim));
        }
        sql.append(" ORDER BY me.data DESC, me.id DESC");

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                MovimentacaoEstoque m = new MovimentacaoEstoque(
                        rs.getInt("id"),
                        rs.getInt("id_produto"),
                        rs.getString("tipo"),
                        rs.getInt("quantidade"),
                        rs.getString("motivo"),
                        rs.getDate("data")
                );
                m.setProdutoNome(rs.getString("produto_nome"));
                lista.add(m);
            }
        }
        return lista;
    }
}
