package com.studiomuda.estoque.infrastructure.persistence.funcionario;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.domain.funcionario.Cargo;
import com.studiomuda.estoque.domain.funcionario.Cpf;
import com.studiomuda.estoque.domain.funcionario.Endereco;
import com.studiomuda.estoque.domain.funcionario.Funcionario;
import com.studiomuda.estoque.domain.funcionario.FuncionarioRepository;
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
public class FuncionarioRepositoryJdbc implements FuncionarioRepository {

    @Override
    public Funcionario salvar(Funcionario f) {
        String sql = "INSERT INTO funcionario (nome, cpf, cargo, data_nasc, telefone, " +
                "cep, rua, numero, bairro, cidade, estado, ativo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(stmt, f);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int novoId = keys.getInt(1);
                    return new Funcionario(novoId, f.nome(), f.cpf(), f.cargo(), f.dataNascimento(),
                            f.telefone(), f.endereco(), f.ativo());
                }
            }
            return f;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar funcionário: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Funcionario f) {
        String sql = "UPDATE funcionario SET nome = ?, telefone = ?, cep = ?, rua = ?, numero = ?, bairro = ?, " +
                "cidade = ?, estado = ?, cargo = ?, ativo = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            Endereco e = f.endereco();
            stmt.setString(1, f.nome());
            stmt.setString(2, f.telefone());
            stmt.setString(3, e != null ? e.cep() : null);
            stmt.setString(4, e != null ? e.rua() : null);
            stmt.setString(5, e != null ? e.numero() : null);
            stmt.setString(6, e != null ? e.bairro() : null);
            stmt.setString(7, e != null ? e.cidade() : null);
            stmt.setString(8, e != null ? e.estado() : null);
            stmt.setString(9, f.cargo().rotulo());
            stmt.setBoolean(10, f.ativo());
            stmt.setInt(11, f.id());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao atualizar funcionário: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Optional<Funcionario> buscarPorId(int id) {
        return executarUm("SELECT * FROM funcionario WHERE id = ?", id);
    }

    @Override
    public Optional<Funcionario> buscarPorCpf(Cpf cpf) {
        return executarUm("SELECT * FROM funcionario WHERE cpf = ?", cpf.digitos());
    }

    @Override
    public List<Funcionario> listarTodos() {
        List<Funcionario> lista = new ArrayList<>();
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM funcionario");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar funcionários: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Funcionario> buscarComFiltros(String nome, Cargo cargo, Boolean ativo) {
        StringBuilder sql = new StringBuilder("SELECT * FROM funcionario WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (nome != null && !nome.trim().isEmpty()) {
            sql.append(" AND nome LIKE ?");
            params.add("%" + nome.trim() + "%");
        }
        if (cargo != null) {
            sql.append(" AND cargo = ?");
            params.add(cargo.rotulo());
        }
        if (ativo != null) {
            sql.append(" AND ativo = ?");
            params.add(ativo);
        }

        List<Funcionario> lista = new ArrayList<>();
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar funcionários: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public void desativar(int id) {
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE funcionario SET ativo = false WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao desativar funcionário: " + e.getMessage(), e);
        }
    }

    private Optional<Funcionario> executarUm(String sql, Object param) {
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar funcionário: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    private void preencher(PreparedStatement stmt, Funcionario f) throws SQLException {
        Endereco e = f.endereco();
        stmt.setString(1, f.nome());
        stmt.setString(2, f.cpf().digitos());
        stmt.setString(3, f.cargo().rotulo());
        stmt.setDate(4, f.dataNascimento() != null ? java.sql.Date.valueOf(f.dataNascimento()) : null);
        stmt.setString(5, f.telefone());
        stmt.setString(6, e != null ? e.cep() : null);
        stmt.setString(7, e != null ? e.rua() : null);
        stmt.setString(8, e != null ? e.numero() : null);
        stmt.setString(9, e != null ? e.bairro() : null);
        stmt.setString(10, e != null ? e.cidade() : null);
        stmt.setString(11, e != null ? e.estado() : null);
        stmt.setBoolean(12, f.ativo());
    }

    private Funcionario mapear(ResultSet rs) throws SQLException {
        Cpf cpf = Cpf.of(rs.getString("cpf"));
        Cargo cargo = Cargo.desdeRotulo(rs.getString("cargo"));
        Endereco endereco = new Endereco(
                rs.getString("cep"), rs.getString("rua"), rs.getString("numero"),
                rs.getString("bairro"), rs.getString("cidade"), rs.getString("estado"));
        java.sql.Date dataNasc = rs.getDate("data_nasc");
        return new Funcionario(
                rs.getInt("id"),
                rs.getString("nome"),
                cpf,
                cargo,
                dataNasc != null ? dataNasc.toLocalDate() : null,
                rs.getString("telefone"),
                endereco,
                rs.getBoolean("ativo"));
    }
}
