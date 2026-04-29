package com.studiomuda.estoque.infrastructure.persistence.cliente;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.domain.cliente.Cliente;
import com.studiomuda.estoque.domain.cliente.ClienteRepository;
import com.studiomuda.estoque.domain.cliente.CpfCnpj;
import com.studiomuda.estoque.domain.cliente.Endereco;
import com.studiomuda.estoque.domain.cliente.TipoPessoa;
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
public class ClienteRepositoryJdbc implements ClienteRepository {

    @Override
    public Cliente salvar(Cliente cliente) {
        String sql = "INSERT INTO cliente (nome, cpf_cnpj, telefone, email, cep, rua, numero, bairro, cidade, estado, tipo, ativo, dataNascimento) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(stmt, cliente);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int novoId = keys.getInt(1);
                    return new Cliente(novoId, cliente.nome(), cliente.cpfCnpj(), cliente.telefone(),
                            cliente.email(), cliente.endereco(), cliente.ativo(), cliente.dataNascimento());
                }
            }
            return cliente;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET nome = ?, telefone = ?, email = ?, cep = ?, rua = ?, numero = ?, bairro = ?, cidade = ?, estado = ?, tipo = ?, ativo = ?, dataNascimento = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            Endereco e = cliente.endereco();
            stmt.setString(1, cliente.nome());
            stmt.setString(2, cliente.telefone());
            stmt.setString(3, cliente.email());
            stmt.setString(4, e != null ? e.cep() : null);
            stmt.setString(5, e != null ? e.rua() : null);
            stmt.setString(6, e != null ? e.numero() : null);
            stmt.setString(7, e != null ? e.bairro() : null);
            stmt.setString(8, e != null ? e.cidade() : null);
            stmt.setString(9, e != null ? e.estado() : null);
            stmt.setString(10, cliente.tipo().name());
            stmt.setBoolean(11, cliente.ativo());
            stmt.setDate(12, cliente.dataNascimento() != null ? java.sql.Date.valueOf(cliente.dataNascimento()) : null);
            stmt.setInt(13, cliente.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(int id) {
        String sql = "SELECT * FROM cliente WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Cliente> buscarPorCpfCnpj(CpfCnpj cpfCnpj) {
        String sql = "SELECT * FROM cliente WHERE cpf_cnpj = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpfCnpj.digitos());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente por CPF/CNPJ: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Cliente> listarTodos() {
        return executarQuery("SELECT * FROM cliente", null);
    }

    @Override
    public List<Cliente> listarAtivos() {
        return executarQuery("SELECT * FROM cliente WHERE ativo = ?", Boolean.TRUE);
    }

    @Override
    public List<Cliente> listarInativos() {
        return executarQuery("SELECT * FROM cliente WHERE ativo = ?", Boolean.FALSE);
    }

    @Override
    public List<Cliente> buscarComFiltros(String nome, TipoPessoa tipo, Boolean ativo) {
        StringBuilder sql = new StringBuilder("SELECT * FROM cliente WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (nome != null && !nome.trim().isEmpty()) {
            sql.append(" AND nome LIKE ?");
            params.add("%" + nome.trim() + "%");
        }
        if (tipo != null) {
            sql.append(" AND tipo = ?");
            params.add(tipo.name());
        }
        if (ativo != null) {
            sql.append(" AND ativo = ?");
            params.add(ativo);
        }

        List<Cliente> lista = new ArrayList<>();
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar clientes: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public void desativar(int id) {
        String sql = "UPDATE cliente SET ativo = false WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao desativar cliente: " + e.getMessage(), e);
        }
    }

    private List<Cliente> executarQuery(String sql, Object param) {
        List<Cliente> lista = new ArrayList<>();
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (param != null) stmt.setObject(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar clientes: " + e.getMessage(), e);
        }
        return lista;
    }

    private void preencher(PreparedStatement stmt, Cliente c) throws SQLException {
        Endereco e = c.endereco();
        stmt.setString(1, c.nome());
        stmt.setString(2, c.cpfCnpj().digitos());
        stmt.setString(3, c.telefone());
        stmt.setString(4, c.email());
        stmt.setString(5, e != null ? e.cep() : null);
        stmt.setString(6, e != null ? e.rua() : null);
        stmt.setString(7, e != null ? e.numero() : null);
        stmt.setString(8, e != null ? e.bairro() : null);
        stmt.setString(9, e != null ? e.cidade() : null);
        stmt.setString(10, e != null ? e.estado() : null);
        stmt.setString(11, c.tipo().name());
        stmt.setBoolean(12, c.ativo());
        stmt.setDate(13, c.dataNascimento() != null ? java.sql.Date.valueOf(c.dataNascimento()) : null);
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        TipoPessoa tipo = TipoPessoa.fromCodigo(rs.getString("tipo"));
        CpfCnpj cpfCnpj = CpfCnpj.of(rs.getString("cpf_cnpj"), tipo);
        Endereco endereco = new Endereco(
                rs.getString("cep"), rs.getString("rua"), rs.getString("numero"),
                rs.getString("bairro"), rs.getString("cidade"), rs.getString("estado"));
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nome"),
                cpfCnpj,
                rs.getString("telefone"),
                rs.getString("email"),
                endereco,
                rs.getBoolean("ativo"),
                rs.getDate("dataNascimento") != null ? rs.getDate("dataNascimento").toLocalDate() : null);
    }
}
