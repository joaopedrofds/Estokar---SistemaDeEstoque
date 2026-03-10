package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.ItemPedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemPedidoDAO {

    public void inserir(ItemPedido item) throws SQLException {
        String sql = "INSERT INTO item_pedido (id_pedido, id_produto, quantidade) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, item.getPedidoId());
            stmt.setInt(2, item.getProdutoId());
            stmt.setInt(3, item.getQuantidade());
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                item.setId(rs.getInt(1));
            }
        }
    }

    public List<ItemPedido> listarPorPedido(int pedidoId) throws SQLException {
        List<ItemPedido> lista = new ArrayList<>();
        String sql = "SELECT ip.*, p.nome as produto_nome, p.valor as produto_valor " +
                     "FROM item_pedido ip " +
                     "JOIN produto p ON ip.id_produto = p.id " +
                     "WHERE ip.id_pedido = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pedidoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ItemPedido item = new ItemPedido(
                        rs.getInt("id"),
                        rs.getInt("id_pedido"),
                        rs.getInt("id_produto"),
                        rs.getInt("quantidade")
                );
                item.setProdutoNome(rs.getString("produto_nome"));
                item.setProdutoValor(rs.getDouble("produto_valor"));
                lista.add(item);
            }
        }

        return lista;
    }

    public ItemPedido buscarPorId(int id) throws SQLException {
        String sql = "SELECT ip.*, p.nome as produto_nome, p.valor as produto_valor " +
                     "FROM item_pedido ip " +
                     "JOIN produto p ON ip.id_produto = p.id " +
                     "WHERE ip.id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ItemPedido item = new ItemPedido(
                        rs.getInt("id"),
                        rs.getInt("id_pedido"),
                        rs.getInt("id_produto"),
                        rs.getInt("quantidade")
                );
                item.setProdutoNome(rs.getString("produto_nome"));
                item.setProdutoValor(rs.getDouble("produto_valor"));
                return item;
            }
        }

        return null;
    }
    
    public void atualizar(ItemPedido item) throws SQLException {
        String sql = "UPDATE item_pedido SET id_produto = ?, quantidade = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, item.getProdutoId());
            stmt.setInt(2, item.getQuantidade());
            stmt.setInt(3, item.getId());
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM item_pedido WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    public void deletarPorPedido(int pedidoId) throws SQLException {
        String sql = "DELETE FROM item_pedido WHERE id_pedido = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pedidoId);
            stmt.executeUpdate();
        }
    }
}
