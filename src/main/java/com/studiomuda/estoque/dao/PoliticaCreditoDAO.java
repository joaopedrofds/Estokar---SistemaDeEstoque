package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.PoliticaCredito;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;

public class PoliticaCreditoDAO {
    public PoliticaCredito buscarAtiva() throws SQLException {
        String sql = "SELECT id, nome, dias_limite_atraso, ativa, data_inicio, data_fim, criado_em " +
                "FROM politica_credito WHERE ativa = TRUE AND data_fim IS NULL ORDER BY data_inicio DESC, id DESC LIMIT 1";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return mapear(rs);
            }
        } catch (SQLException e) {
            if (!isTabelaInexistente(e)) {
                throw e;
            }
        }
        return politicaPadrao();
    }

    public void ativarNova(PoliticaCredito politica) throws SQLException {
        validar(politica);
        String encerrarSql = "UPDATE politica_credito SET ativa = FALSE, data_fim = ? WHERE ativa = TRUE AND data_fim IS NULL";
        String inserirSql = "INSERT INTO politica_credito (nome, dias_limite_atraso, ativa, data_inicio, data_fim) VALUES (?, ?, TRUE, ?, NULL)";

        try (Connection conn = Conexao.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement encerrar = conn.prepareStatement(encerrarSql);
                 PreparedStatement inserir = conn.prepareStatement(inserirSql, Statement.RETURN_GENERATED_KEYS)) {
                LocalDate inicio = politica.getDataInicio() != null ? politica.getDataInicio() : LocalDate.now();
                encerrar.setDate(1, Date.valueOf(inicio.minusDays(1)));
                encerrar.executeUpdate();

                inserir.setString(1, politica.getNome().trim());
                inserir.setInt(2, politica.getDiasLimiteAtraso());
                inserir.setDate(3, Date.valueOf(inicio));
                inserir.executeUpdate();

                try (ResultSet rs = inserir.getGeneratedKeys()) {
                    if (rs.next()) {
                        politica.setId(rs.getInt(1));
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private void validar(PoliticaCredito politica) {
        if (politica == null || politica.getNome() == null || politica.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o nome da politica de credito.");
        }
        if (politica.getDiasLimiteAtraso() < 0) {
            throw new IllegalArgumentException("O limite de atraso nao pode ser negativo.");
        }
    }

    private PoliticaCredito politicaPadrao() {
        PoliticaCredito politica = new PoliticaCredito();
        politica.setNome("Politica padrao");
        politica.setDiasLimiteAtraso(45);
        politica.setAtiva(true);
        politica.setDataInicio(LocalDate.now());
        return politica;
    }

    private PoliticaCredito mapear(ResultSet rs) throws SQLException {
        PoliticaCredito politica = new PoliticaCredito();
        politica.setId(rs.getInt("id"));
        politica.setNome(rs.getString("nome"));
        politica.setDiasLimiteAtraso(rs.getInt("dias_limite_atraso"));
        politica.setAtiva(rs.getBoolean("ativa"));
        Date dataInicio = rs.getDate("data_inicio");
        Date dataFim = rs.getDate("data_fim");
        Timestamp criadoEm = rs.getTimestamp("criado_em");
        politica.setDataInicio(dataInicio != null ? dataInicio.toLocalDate() : null);
        politica.setDataFim(dataFim != null ? dataFim.toLocalDate() : null);
        politica.setCriadoEm(criadoEm != null ? criadoEm.toLocalDateTime() : null);
        return politica;
    }

    private boolean isTabelaInexistente(SQLException e) {
        return "42S02".equals(e.getSQLState())
                || (e.getMessage() != null && e.getMessage().toLowerCase().contains("doesn't exist"));
    }
}
