package com.studiomuda.estoque.dao;
import org.springframework.stereotype.Repository;




import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.ItemDevolucao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Repository
public class ItemDevolucaoDAO {

    public void inserir(ItemDevolucao item) throws SQLException {
        String sql = "INSERT INTO item_devolucao (devolucao_id, produto_id, quantidade, valor_unitario, condicao) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, item.getDevolucaoId());
            stmt.setInt(2, item.getProdutoId());
            stmt.setInt(3, item.getQuantidade());
            stmt.setDouble(4, item.getValorUnitario());
            stmt.setString(5, item.getCondicao());
            stmt.executeUpdate();
        }
    }

    public List<ItemDevolucao> buscarPorDevolucaoId(int devolucaoId) throws SQLException {
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
                    item.setValorUnitario(rs.getDouble("valor_unitario"));
                    item.setCondicao(rs.getString("condicao"));
                    itens.add(item);
                }
            }
        }
        return itens;
    }
}
