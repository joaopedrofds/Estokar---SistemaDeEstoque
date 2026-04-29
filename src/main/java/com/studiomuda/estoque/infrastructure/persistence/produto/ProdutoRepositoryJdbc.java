package com.studiomuda.estoque.infrastructure.persistence.produto;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.domain.produto.Produto;
import com.studiomuda.estoque.domain.produto.ProdutoRepository;
import com.studiomuda.estoque.domain.produto.StatusEstoque;
import com.studiomuda.estoque.domain.produto.TipoProduto;
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
public class ProdutoRepositoryJdbc implements ProdutoRepository {

    @Override
    public Produto salvar(Produto p) {
        String sql = "INSERT INTO produto (nome, descricao, tipo, quantidade, valor) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(stmt, p);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Produto(keys.getInt(1), p.nome(), p.descricao(), p.tipo(), p.quantidade(), p.valor());
                }
            }
            return p;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar produto: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Produto p) {
        String sql = "UPDATE produto SET nome = ?, descricao = ?, tipo = ?, quantidade = ?, valor = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencher(stmt, p);
            stmt.setInt(6, p.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Produto> buscarPorId(int id) {
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM produto WHERE id = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Produto> listarTodos() {
        List<Produto> lista = new ArrayList<>();
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM produto");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Produto> buscarComFiltros(String nome, TipoProduto tipo, StatusEstoque estoque) {
        List<Produto> lista = new ArrayList<>();
        String sql = "CALL sp_buscar_produtos(?, ?, ?)";
        String nomeFiltro = (nome != null && !nome.trim().isEmpty()) ? nome.trim() : null;
        String tipoFiltro = tipo != null ? tipo.name() : null;
        String estoqueFiltro = estoque != null ? estoque.codigo() : null;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeFiltro);
            stmt.setString(2, tipoFiltro);
            stmt.setString(3, estoqueFiltro);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produtos: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public void remover(int id) {
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM produto WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover produto: " + e.getMessage(), e);
        }
    }

    private void preencher(PreparedStatement stmt, Produto p) throws SQLException {
        stmt.setString(1, p.nome());
        stmt.setString(2, p.descricao());
        stmt.setString(3, p.tipo().name());
        stmt.setInt(4, p.quantidade());
        stmt.setDouble(5, p.valor());
    }

    private Produto mapear(ResultSet rs) throws SQLException {
        return new Produto(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("descricao"),
                TipoProduto.fromCodigo(rs.getString("tipo")),
                rs.getInt("quantidade"),
                rs.getDouble("valor"));
    }
}
