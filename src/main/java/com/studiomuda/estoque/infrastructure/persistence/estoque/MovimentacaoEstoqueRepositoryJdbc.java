package com.studiomuda.estoque.infrastructure.persistence.estoque;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.domain.estoque.MovimentacaoEstoque;
import com.studiomuda.estoque.domain.estoque.MovimentacaoEstoqueComProduto;
import com.studiomuda.estoque.domain.estoque.MovimentacaoEstoqueRepository;
import com.studiomuda.estoque.domain.estoque.TipoMovimentacao;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MovimentacaoEstoqueRepositoryJdbc implements MovimentacaoEstoqueRepository {

    @Override
    public MovimentacaoEstoque registrar(MovimentacaoEstoque mov) {
        String sqlMov = "INSERT INTO movimentacao_estoque (id_produto, tipo, quantidade, motivo, data) VALUES (?, ?, ?, ?, ?)";
        String sqlEstoque = "UPDATE produto SET quantidade = quantidade + ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = Conexao.getConnection();
            conn.setAutoCommit(false);

            int novoId;
            try (PreparedStatement stmt = conn.prepareStatement(sqlMov, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, mov.produtoId());
                stmt.setString(2, mov.tipo().codigo());
                stmt.setInt(3, mov.quantidade());
                stmt.setString(4, mov.motivo());
                stmt.setDate(5, Date.valueOf(mov.data()));
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    novoId = rs.next() ? rs.getInt(1) : 0;
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlEstoque)) {
                stmt.setInt(1, mov.deltaEstoque());
                stmt.setInt(2, mov.produtoId());
                stmt.executeUpdate();
            }

            conn.commit();
            return new MovimentacaoEstoque(novoId, mov.produtoId(), mov.tipo(),
                    mov.quantidade(), mov.motivo(), mov.data());
        } catch (SQLException e) {
            rollbackQuieto(conn);
            throw new RuntimeException("Erro ao registrar movimentação: " + e.getMessage(), e);
        } finally {
            fecharQuieto(conn);
        }
    }

    @Override
    public void removerComEstorno(MovimentacaoEstoque mov) {
        String sqlDelete = "DELETE FROM movimentacao_estoque WHERE id = ?";
        String sqlEstorno = "UPDATE produto SET quantidade = quantidade + ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = Conexao.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sqlEstorno)) {
                stmt.setInt(1, mov.deltaEstorno());
                stmt.setInt(2, mov.produtoId());
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement(sqlDelete)) {
                stmt.setInt(1, mov.id());
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            rollbackQuieto(conn);
            throw new RuntimeException("Erro ao excluir movimentação: " + e.getMessage(), e);
        } finally {
            fecharQuieto(conn);
        }
    }

    @Override
    public Optional<MovimentacaoEstoque> buscarPorId(int id) {
        String sql = "SELECT * FROM movimentacao_estoque WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapearMov(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar movimentação: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public void atualizarMetadados(int id, String motivo, LocalDate data) {
        String sql = "UPDATE movimentacao_estoque SET motivo = ?, data = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, motivo);
            stmt.setDate(2, data != null ? Date.valueOf(data) : null);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar movimentação: " + e.getMessage(), e);
        }
    }

    @Override
    public List<MovimentacaoEstoqueComProduto> listarTodas() {
        String sql = "SELECT me.*, p.nome AS produto_nome FROM movimentacao_estoque me " +
                "JOIN produto p ON me.id_produto = p.id ORDER BY me.data DESC, me.id DESC";
        return executarLista(sql, new ArrayList<>());
    }

    @Override
    public List<MovimentacaoEstoqueComProduto> buscarComFiltros(String produtoNome, String tipo,
                                                                 LocalDate dataInicio, LocalDate dataFim) {
        StringBuilder sql = new StringBuilder(
                "SELECT me.*, p.nome AS produto_nome FROM movimentacao_estoque me JOIN produto p ON me.id_produto = p.id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (produtoNome != null && !produtoNome.trim().isEmpty()) {
            sql.append(" AND p.nome LIKE ?");
            params.add("%" + produtoNome.trim() + "%");
        }
        if (tipo != null && !tipo.trim().isEmpty()) {
            sql.append(" AND me.tipo = ?");
            params.add(tipo.trim());
        }
        if (dataInicio != null) {
            sql.append(" AND me.data >= ?");
            params.add(Date.valueOf(dataInicio));
        }
        if (dataFim != null) {
            sql.append(" AND me.data <= ?");
            params.add(Date.valueOf(dataFim));
        }
        sql.append(" ORDER BY me.data DESC, me.id DESC");
        return executarLista(sql.toString(), params);
    }

    private List<MovimentacaoEstoqueComProduto> executarLista(String sql, List<Object> params) {
        List<MovimentacaoEstoqueComProduto> lista = new ArrayList<>();
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) stmt.setObject(i + 1, params.get(i));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new MovimentacaoEstoqueComProduto(mapearMov(rs), rs.getString("produto_nome")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar movimentações: " + e.getMessage(), e);
        }
        return lista;
    }

    private MovimentacaoEstoque mapearMov(ResultSet rs) throws SQLException {
        Date data = rs.getDate("data");
        return new MovimentacaoEstoque(
                rs.getInt("id"),
                rs.getInt("id_produto"),
                TipoMovimentacao.fromCodigo(rs.getString("tipo")),
                rs.getInt("quantidade"),
                rs.getString("motivo"),
                data != null ? data.toLocalDate() : null);
    }

    private void rollbackQuieto(Connection conn) {
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ignored) {}
        }
    }

    private void fecharQuieto(Connection conn) {
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }
}
