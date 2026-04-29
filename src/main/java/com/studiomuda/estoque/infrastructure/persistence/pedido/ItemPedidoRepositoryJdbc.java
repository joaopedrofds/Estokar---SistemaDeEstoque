package com.studiomuda.estoque.infrastructure.persistence.pedido;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.domain.pedido.ItemPedido;
import com.studiomuda.estoque.domain.pedido.ItemPedidoComProduto;
import com.studiomuda.estoque.domain.pedido.ItemPedidoRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ItemPedidoRepositoryJdbc implements ItemPedidoRepository {

    @Override
    public ItemPedido salvar(ItemPedido item) {
        String sql = "INSERT INTO item_pedido (id_pedido, id_produto, quantidade) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, item.pedidoId());
            stmt.setInt(2, item.produtoId());
            stmt.setInt(3, item.quantidade());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new ItemPedido(keys.getInt(1), item.pedidoId(), item.produtoId(), item.quantidade());
                }
            }
            return item;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar item de pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<ItemPedido> buscarPorId(int id) {
        String sql = "SELECT id, id_pedido, id_produto, quantidade FROM item_pedido WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new ItemPedido(
                            rs.getInt("id"),
                            rs.getInt("id_pedido"),
                            rs.getInt("id_produto"),
                            rs.getInt("quantidade")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar item de pedido: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<ItemPedidoComProduto> listarPorPedido(int pedidoId) {
        List<ItemPedidoComProduto> lista = new ArrayList<>();
        String sql = "SELECT ip.id, ip.id_pedido, ip.id_produto, ip.quantidade, p.nome AS produto_nome, p.valor AS produto_valor "
                + "FROM item_pedido ip "
                + "JOIN produto p ON ip.id_produto = p.id "
                + "WHERE ip.id_pedido = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pedidoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ItemPedido item = new ItemPedido(
                            rs.getInt("id"),
                            rs.getInt("id_pedido"),
                            rs.getInt("id_produto"),
                            rs.getInt("quantidade"));
                    lista.add(new ItemPedidoComProduto(
                            item,
                            rs.getString("produto_nome"),
                            rs.getDouble("produto_valor")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar itens do pedido: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public void remover(int id) {
        String sql = "DELETE FROM item_pedido WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover item de pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public void removerPorPedido(int pedidoId) {
        String sql = "DELETE FROM item_pedido WHERE id_pedido = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pedidoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover itens do pedido: " + e.getMessage(), e);
        }
    }
}
