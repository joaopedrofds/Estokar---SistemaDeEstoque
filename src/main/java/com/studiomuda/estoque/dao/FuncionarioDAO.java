package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.Funcionario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {

    public void inserir(Funcionario f) throws SQLException {
        String sql = "INSERT INTO funcionario (nome, cpf, cargo, data_nasc, telefone, " +
                "cep, rua, numero, bairro, cidade, estado, ativo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, f.getNome());
            stmt.setString(2, f.getCpf());
            stmt.setString(3, f.getCargo());
            stmt.setDate(4, f.getData_nasc());
            stmt.setString(5, f.getTelefone());
            stmt.setString(6, f.getCep());
            stmt.setString(7, f.getRua());
            stmt.setString(8, f.getNumero());
            stmt.setString(9, f.getBairro());
            stmt.setString(10, f.getCidade());
            stmt.setString(11, f.getEstado());
            stmt.setBoolean(12, f.isAtivo());

            stmt.executeUpdate();
        }
    }

    public List<Funcionario> listar() throws SQLException {
        List<Funcionario> lista = new ArrayList<>();
        String sql = "SELECT * FROM funcionario";
        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Funcionario f = new Funcionario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("cargo"),
                        rs.getDate("data_nasc"),
                        rs.getString("telefone"),
                        rs.getString("cep"),
                        rs.getString("rua"),
                        rs.getString("numero"),
                        rs.getString("bairro"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        rs.getBoolean("ativo")
                );
                lista.add(f);
            }
        }
        return lista;
    }

    public Funcionario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM funcionario WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Funcionario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("cargo"),
                        rs.getDate("data_nasc"),
                        rs.getString("telefone"),
                        rs.getString("cep"),
                        rs.getString("rua"),
                        rs.getString("numero"),
                        rs.getString("bairro"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        rs.getBoolean("ativo")
                );
            }
        }
        return null;
    }

    public Funcionario buscarPorCpf(String cpf) throws SQLException {
        String sql = "SELECT * FROM funcionario WHERE cpf = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Funcionario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("cargo"),
                        rs.getDate("data_nasc"),
                        rs.getString("telefone"),
                        rs.getString("cep"),
                        rs.getString("rua"),
                        rs.getString("numero"),
                        rs.getString("bairro"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        rs.getBoolean("ativo")
                );
            }
        }
        return null;
    }

    public void atualizar(Funcionario f) throws SQLException {
        String sql = "UPDATE funcionario SET nome = ?, telefone = ?, cep = ?, rua = ?, numero = ?, bairro = ?, " +
                "cidade = ?, estado = ?, cargo = ?, ativo = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, f.getNome());
            stmt.setString(2, f.getTelefone());
            stmt.setString(3, f.getCep());
            stmt.setString(4, f.getRua());
            stmt.setString(5, f.getNumero());
            stmt.setString(6, f.getBairro());
            stmt.setString(7, f.getCidade());
            stmt.setString(8, f.getEstado());
            stmt.setString(9, f.getCargo());
            stmt.setBoolean(10, f.isAtivo());
            stmt.setInt(11, f.getId());

            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "UPDATE funcionario SET ativo = false WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Funcionario> buscarComFiltros(String nome, String cargo, String status) throws SQLException {
        List<Funcionario> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM funcionario WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (nome != null && !nome.trim().isEmpty()) {
            sql.append(" AND nome LIKE ?");
            params.add("%" + nome.trim() + "%");
        }
        if (cargo != null && !cargo.trim().isEmpty()) {
            sql.append(" AND cargo = ?");
            params.add(cargo.trim());
        }
        if (status != null && !status.trim().isEmpty()) {
            if (status.equalsIgnoreCase("ativo")) {
                sql.append(" AND ativo = 1");
            } else if (status.equalsIgnoreCase("inativo")) {
                sql.append(" AND ativo = 0");
            }
        }

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Funcionario f = new Funcionario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("cargo"),
                        rs.getDate("data_nasc"),
                        rs.getString("telefone"),
                        rs.getString("cep"),
                        rs.getString("rua"),
                        rs.getString("numero"),
                        rs.getString("bairro"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        rs.getBoolean("ativo")
                );
                lista.add(f);
            }
        }

        return lista;
    }
}