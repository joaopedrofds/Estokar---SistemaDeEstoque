package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.Cupom;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CupomDAO {

    public List<Cupom> listar() throws SQLException {
        List<Cupom> cupons = new ArrayList<>();
        String sql = "SELECT * FROM cupom ORDER BY id";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cupom cupom = new Cupom();
                cupom.setId(rs.getInt("id"));
                cupom.setCodigo(rs.getString("codigo"));
                cupom.setDescricao(rs.getString("descricao"));
                cupom.setValor(rs.getDouble("valor"));

                Date dataInicio = rs.getDate("data_inicio");
                if (dataInicio != null) {
                    cupom.setDataInicio(dataInicio.toLocalDate());
                }

                Date validade = rs.getDate("validade");
                if (validade != null) {
                    cupom.setValidade(validade.toLocalDate());
                }

                cupom.setCondicoesUso(rs.getString("condicoes_uso"));

                cupons.add(cupom);
            }
        }

        return cupons;
    }

    public Cupom buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM cupom WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cupom cupom = new Cupom();
                    cupom.setId(rs.getInt("id"));
                    cupom.setCodigo(rs.getString("codigo"));
                    cupom.setDescricao(rs.getString("descricao"));
                    cupom.setValor(rs.getDouble("valor"));

                    Date dataInicio = rs.getDate("data_inicio");
                    if (dataInicio != null) {
                        cupom.setDataInicio(dataInicio.toLocalDate());
                    }

                    Date validade = rs.getDate("validade");
                    if (validade != null) {
                        cupom.setValidade(validade.toLocalDate());
                    }

                    cupom.setCondicoesUso(rs.getString("condicoes_uso"));

                    return cupom;
                }
            }
        }

        return null;
    }

    public Cupom buscarPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT * FROM cupom WHERE codigo = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cupom cupom = new Cupom();
                    cupom.setId(rs.getInt("id"));
                    cupom.setCodigo(rs.getString("codigo"));
                    cupom.setDescricao(rs.getString("descricao"));
                    cupom.setValor(rs.getDouble("valor"));

                    Date dataInicio = rs.getDate("data_inicio");
                    if (dataInicio != null) {
                        cupom.setDataInicio(dataInicio.toLocalDate());
                    }

                    Date validade = rs.getDate("validade");
                    if (validade != null) {
                        cupom.setValidade(validade.toLocalDate());
                    }

                    cupom.setCondicoesUso(rs.getString("condicoes_uso"));

                    return cupom;
                }
            }
        }

        return null;
    }

    public void inserir(Cupom cupom) throws SQLException {
        String sql = "INSERT INTO cupom (codigo, descricao, valor, data_inicio, validade, condicoes_uso) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cupom.getCodigo());
            stmt.setString(2, cupom.getDescricao());
            stmt.setDouble(3, cupom.getValor());

            if (cupom.getDataInicio() != null) {
                stmt.setDate(4, Date.valueOf(cupom.getDataInicio()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            if (cupom.getValidade() != null) {
                stmt.setDate(5, Date.valueOf(cupom.getValidade()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            stmt.setString(6, cupom.getCondicoesUso());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cupom.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void atualizar(Cupom cupom) throws SQLException {
        String sql = "UPDATE cupom SET codigo = ?, descricao = ?, valor = ?, " +
                "data_inicio = ?, validade = ?, condicoes_uso = ? WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cupom.getCodigo());
            stmt.setString(2, cupom.getDescricao());
            stmt.setDouble(3, cupom.getValor());

            if (cupom.getDataInicio() != null) {
                stmt.setDate(4, Date.valueOf(cupom.getDataInicio()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            if (cupom.getValidade() != null) {
                stmt.setDate(5, Date.valueOf(cupom.getValidade()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            stmt.setString(6, cupom.getCondicoesUso());
            stmt.setInt(7, cupom.getId());

            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM cupom WHERE id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Cupom> buscarCuponsValidos() throws SQLException {
        List<Cupom> cupons = new ArrayList<>();
        LocalDate hoje = LocalDate.now();
        String sql = "SELECT * FROM cupom WHERE data_inicio <= ? AND validade >= ? ORDER BY validade";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(hoje));
            stmt.setDate(2, Date.valueOf(hoje));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Cupom cupom = new Cupom();
                    cupom.setId(rs.getInt("id"));
                    cupom.setCodigo(rs.getString("codigo"));
                    cupom.setDescricao(rs.getString("descricao"));
                    cupom.setValor(rs.getDouble("valor"));

                    Date dataInicio = rs.getDate("data_inicio");
                    if (dataInicio != null) {
                        cupom.setDataInicio(dataInicio.toLocalDate());
                    }

                    Date validade = rs.getDate("validade");
                    if (validade != null) {
                        cupom.setValidade(validade.toLocalDate());
                    }

                    cupom.setCondicoesUso(rs.getString("condicoes_uso"));

                    cupons.add(cupom);
                }
            }
        }

        return cupons;
    }

    public List<Cupom> buscarComFiltros(String codigo, String status) throws SQLException {
        List<Cupom> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM cupom WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (codigo != null && !codigo.trim().isEmpty()) {
            sql.append(" AND codigo LIKE ?");
            params.add("%" + codigo.trim() + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            if (status.equalsIgnoreCase("valido")) {
                sql.append(" AND validade >= CURDATE()");
            } else if (status.equalsIgnoreCase("expirado")) {
                sql.append(" AND validade < CURDATE()");
            }
        }

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Corrigido: converter java.sql.Date para LocalDate
                LocalDate dataInicio = null;
                Date dataInicioSql = rs.getDate("data_inicio");
                if (dataInicioSql != null) {
                    dataInicio = dataInicioSql.toLocalDate();
                }

                LocalDate validade = null;
                Date validadeSql = rs.getDate("validade");
                if (validadeSql != null) {
                    validade = validadeSql.toLocalDate();
                }

                Cupom c = new Cupom(
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("descricao"),
                        rs.getDouble("valor"),
                        dataInicio,
                        validade,
                        rs.getString("condicoes_uso")
                );
                lista.add(c);
            }
        }
        return lista;
    }
}