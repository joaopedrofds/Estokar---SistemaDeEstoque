package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.Cliente;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    private final PedidoDAO pedidoDAO;

    public ClienteDAO() {
        this(new PedidoDAO());
    }

    ClienteDAO(PedidoDAO pedidoDAO) {
        this.pedidoDAO = pedidoDAO;
    }

    public void inserir(Cliente c) throws SQLException {
        String sql = "INSERT INTO cliente (nome, cpf_cnpj, telefone, email, cep, rua, numero, bairro, cidade, estado, tipo, ativo, dataNascimento) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, c.getNome());
            stmt.setString(2, c.getCpfCnpj());
            stmt.setString(3, c.getTelefone());
            stmt.setString(4, c.getEmail());
            stmt.setString(5, c.getCep());
            stmt.setString(6, c.getRua());
            stmt.setString(7, c.getNumero());
            stmt.setString(8, c.getBairro());
            stmt.setString(9, c.getCidade());
            stmt.setString(10, c.getEstado());
            stmt.setString(11, c.getTipo());
            stmt.setBoolean(12, c.isAtivo());
            stmt.setDate(13, c.getDataNascimento() != null ? java.sql.Date.valueOf(c.getDataNascimento()) : null);

            stmt.executeUpdate();
        }
    }

    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Cliente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf_cnpj"),
                        rs.getString("telefone"),
                        rs.getString("email"),
                        rs.getString("tipo"),
                        rs.getString("cep"),
                        rs.getString("rua"),
                        rs.getString("numero"),
                        rs.getString("bairro"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        rs.getBoolean("ativo"),
                        rs.getDate("dataNascimento") != null ? rs.getDate("dataNascimento").toLocalDate() : null);
            }
        }
        return null;
    }

    public Cliente buscarPorCpfCnpj(String cpfCnpj) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE cpf_cnpj = ?";
        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpfCnpj);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Cliente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf_cnpj"),
                        rs.getString("telefone"),
                        rs.getString("email"),
                        rs.getString("tipo"),
                        rs.getString("cep"),
                        rs.getString("rua"),
                        rs.getString("numero"),
                        rs.getString("bairro"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        rs.getBoolean("ativo"),
                        rs.getDate("dataNascimento") != null ? rs.getDate("dataNascimento").toLocalDate() : null);
            }
        }
        return null;
    }

    public void atualizar(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET nome = ?, telefone = ?, email = ?, cep = ?, rua = ?, numero = ?, bairro = ?, cidade = ?, estado = ?, tipo = ?, ativo = ?, dataNascimento = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getNome());
            stmt.setString(2, c.getTelefone());
            stmt.setString(3, c.getEmail());
            stmt.setString(4, c.getCep());
            stmt.setString(5, c.getRua());
            stmt.setString(6, c.getNumero());
            stmt.setString(7, c.getBairro());
            stmt.setString(8, c.getCidade());
            stmt.setString(9, c.getEstado());
            stmt.setString(10, c.getTipo());
            stmt.setBoolean(11, c.isAtivo());
            stmt.setDate(12, c.getDataNascimento() != null ? java.sql.Date.valueOf(c.getDataNascimento()) : null);
            stmt.setInt(13, c.getId());

            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "UPDATE cliente SET ativo = false WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void bloquearPorInadimplencia(int id) throws SQLException {
        String sql = "UPDATE cliente SET ativo = false WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Cliente> listar() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente";
        try (Connection conn = Conexao.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cliente c = new Cliente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf_cnpj"),
                        rs.getString("telefone"),
                        rs.getString("email"),
                        rs.getString("tipo"),
                        rs.getString("cep"),
                        rs.getString("rua"),
                        rs.getString("numero"),
                        rs.getString("bairro"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        rs.getBoolean("ativo"),
                        rs.getDate("dataNascimento") != null ? rs.getDate("dataNascimento").toLocalDate() : null);
                lista.add(c);
            }
        }
        aplicarAnaliseFrequencia(lista);
        return lista;
    }

    public List<Cliente> listarAtivos() throws SQLException {
        return listarPorStatus(true);
    }

    public List<Cliente> listarInativos() throws SQLException {
        return listarPorStatus(false);
    }

    private List<Cliente> listarPorStatus(boolean ativo) throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE ativo = ?";
        try (Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, ativo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Cliente c = new Cliente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf_cnpj"),
                        rs.getString("telefone"),
                        rs.getString("email"),
                        rs.getString("tipo"),
                        rs.getString("cep"),
                        rs.getString("rua"),
                        rs.getString("numero"),
                        rs.getString("bairro"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        rs.getBoolean("ativo"),
                        rs.getDate("dataNascimento") != null ? rs.getDate("dataNascimento").toLocalDate() : null);
                lista.add(c);
            }
        }
        aplicarAnaliseFrequencia(lista);
        return lista;
    }

    public List<Cliente> buscarComFiltros(String nome, String tipo, String status) throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM cliente WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (nome != null && !nome.trim().isEmpty()) {
            sql.append(" AND nome LIKE ?");
            params.add("%" + nome.trim() + "%");
        }
        if (tipo != null && !tipo.trim().isEmpty()) {
            sql.append(" AND tipo = ?");
            params.add(tipo.trim());
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
                Cliente c = new Cliente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf_cnpj"),
                        rs.getString("telefone"),
                        rs.getString("email"),
                        rs.getString("tipo"),
                        rs.getString("cep"),
                        rs.getString("rua"),
                        rs.getString("numero"),
                        rs.getString("bairro"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        rs.getBoolean("ativo"),
                        rs.getDate("dataNascimento") != null ? rs.getDate("dataNascimento").toLocalDate() : null
                );
                lista.add(c);
            }
        }
        aplicarAnaliseFrequencia(lista);
        return lista;
    }

    private void aplicarAnaliseFrequencia(List<Cliente> clientes) throws SQLException {
        for (Cliente cliente : clientes) {
            List<LocalDate> datasCompra = pedidoDAO.listarDatasCompraPorCliente(cliente.getId());
            cliente.setTotalPedidos(datasCompra.size());

            if (datasCompra.isEmpty()) {
                cliente.setMediaDiasEntreCompras(null);
                cliente.setClassificacaoFrequencia("Sem Compras");
                continue;
            }

            double mediaDias = calcularMediaDiasEntreCompras(datasCompra, LocalDate.now());

            cliente.setMediaDiasEntreCompras(Math.round(mediaDias * 100.0) / 100.0);
            cliente.setClassificacaoFrequencia(classificarFrequencia(mediaDias));
        }
    }

    double calcularMediaDiasEntreCompras(List<LocalDate> datasCompra, LocalDate dataReferencia) {
        if (datasCompra == null || datasCompra.isEmpty()) {
            return 0.0;
        }

        if (datasCompra.size() == 1) {
            return ChronoUnit.DAYS.between(datasCompra.get(0), dataReferencia);
        }

        long somaIntervalos = 0L;
        for (int i = 1; i < datasCompra.size(); i++) {
            somaIntervalos += ChronoUnit.DAYS.between(datasCompra.get(i - 1), datasCompra.get(i));
        }
        return (double) somaIntervalos / (datasCompra.size() - 1);
    }

    String classificarFrequencia(double mediaDias) {
        if (mediaDias < 15) {
            return "Cliente VIP";
        }
        if (mediaDias <= 30) {
            return "Regular";
        }
        return "Em Risco/Inativo";
    }
}