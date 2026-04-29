package com.studiomuda.estoque.infrastructure.persistence.cupom;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.domain.cupom.Cupom;
import com.studiomuda.estoque.domain.cupom.CupomRepository;
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
public class CupomRepositoryJdbc implements CupomRepository {

    @Override
    public Cupom salvar(Cupom c) {
        String sql = "INSERT INTO cupom (codigo, descricao, valor, data_inicio, validade, condicoes_uso) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(stmt, c);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Cupom(keys.getInt(1), c.codigo(), c.descricao(), c.valor(),
                            c.dataInicio(), c.validade(), c.condicoesUso());
                }
            }
            return c;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar cupom: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Cupom c) {
        String sql = "UPDATE cupom SET codigo = ?, descricao = ?, valor = ?, data_inicio = ?, validade = ?, condicoes_uso = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencher(stmt, c);
            stmt.setInt(7, c.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar cupom: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Cupom> buscarPorId(int id) {
        return executarUm("SELECT * FROM cupom WHERE id = ?", id);
    }

    @Override
    public Optional<Cupom> buscarPorCodigo(String codigo) {
        return executarUm("SELECT * FROM cupom WHERE codigo = ?", codigo);
    }

    @Override
    public List<Cupom> listarTodos() {
        return executarLista("SELECT * FROM cupom ORDER BY id");
    }

    @Override
    public List<Cupom> listarValidos() {
        List<Cupom> lista = new ArrayList<>();
        LocalDate hoje = LocalDate.now();
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM cupom WHERE data_inicio <= ? AND validade >= ? ORDER BY validade")) {
            stmt.setDate(1, Date.valueOf(hoje));
            stmt.setDate(2, Date.valueOf(hoje));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar cupons válidos: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Cupom> buscarComFiltros(String codigo, String status) {
        StringBuilder sql = new StringBuilder("SELECT * FROM cupom WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (codigo != null && !codigo.trim().isEmpty()) {
            sql.append(" AND codigo LIKE ?");
            params.add("%" + codigo.trim() + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            if ("valido".equalsIgnoreCase(status)) sql.append(" AND validade >= CURDATE()");
            else if ("expirado".equalsIgnoreCase(status)) sql.append(" AND validade < CURDATE()");
        }
        List<Cupom> lista = new ArrayList<>();
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) stmt.setObject(i + 1, params.get(i));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cupons: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public void remover(int id) {
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM cupom WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover cupom: " + e.getMessage(), e);
        }
    }

    private Optional<Cupom> executarUm(String sql, Object param) {
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cupom: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    private List<Cupom> executarLista(String sql) {
        List<Cupom> lista = new ArrayList<>();
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar cupons: " + e.getMessage(), e);
        }
        return lista;
    }

    private void preencher(PreparedStatement stmt, Cupom c) throws SQLException {
        stmt.setString(1, c.codigo());
        stmt.setString(2, c.descricao());
        stmt.setDouble(3, c.valor());
        if (c.dataInicio() != null) stmt.setDate(4, Date.valueOf(c.dataInicio()));
        else stmt.setNull(4, Types.DATE);
        if (c.validade() != null) stmt.setDate(5, Date.valueOf(c.validade()));
        else stmt.setNull(5, Types.DATE);
        stmt.setString(6, c.condicoesUso());
    }

    private Cupom mapear(ResultSet rs) throws SQLException {
        Date di = rs.getDate("data_inicio");
        Date va = rs.getDate("validade");
        return new Cupom(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("descricao"),
                rs.getDouble("valor"),
                di != null ? di.toLocalDate() : null,
                va != null ? va.toLocalDate() : null,
                rs.getString("condicoes_uso"));
    }
}
