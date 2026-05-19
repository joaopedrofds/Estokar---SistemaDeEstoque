package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.TemplateRelatorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TemplateRelatorioDAO {

    public void inserir(TemplateRelatorio template) throws SQLException {
        try (Connection conn = Conexao.getConnection()) {
            conn.setAutoCommit(false);
            try {
                inserirTemplate(conn, template);
                substituirCategorias(conn, template.getId(), template.getCategoriaIds());
                substituirIndicadores(conn, template.getId(), template.getIndicadores());
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void atualizar(TemplateRelatorio template) throws SQLException {
        try (Connection conn = Conexao.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String sql = "UPDATE template_relatorio SET nome = ?, descricao = ?, periodo_padrao = ?, agrupamento = ?, ativo = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, template.getNome());
                    stmt.setString(2, template.getDescricao());
                    stmt.setString(3, template.getPeriodoPadrao());
                    stmt.setString(4, template.getAgrupamento());
                    stmt.setBoolean(5, template.isAtivo());
                    stmt.setInt(6, template.getId());
                    stmt.executeUpdate();
                }
                substituirCategorias(conn, template.getId(), template.getCategoriaIds());
                substituirIndicadores(conn, template.getId(), template.getIndicadores());
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public TemplateRelatorio buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM template_relatorio WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TemplateRelatorio template = mapear(rs);
                    template.setCategoriaIds(carregarCategorias(conn, id));
                    template.setIndicadores(carregarIndicadores(conn, id));
                    return template;
                }
            }
        }
        return null;
    }

    public List<TemplateRelatorio> listarTodos() throws SQLException {
        List<TemplateRelatorio> lista = new ArrayList<>();
        String sql = "SELECT * FROM template_relatorio ORDER BY ativo DESC, nome ASC";
        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                TemplateRelatorio template = mapear(rs);
                template.setCategoriaIds(carregarCategorias(conn, template.getId()));
                template.setIndicadores(carregarIndicadores(conn, template.getId()));
                lista.add(template);
            }
        }
        return lista;
    }

    public List<TemplateRelatorio> listarAtivos() throws SQLException {
        List<TemplateRelatorio> lista = new ArrayList<>();
        String sql = "SELECT * FROM template_relatorio WHERE ativo = TRUE ORDER BY nome ASC";
        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                TemplateRelatorio template = mapear(rs);
                template.setCategoriaIds(carregarCategorias(conn, template.getId()));
                template.setIndicadores(carregarIndicadores(conn, template.getId()));
                lista.add(template);
            }
        }
        return lista;
    }

    public void inativar(int id) throws SQLException {
        String sql = "UPDATE template_relatorio SET ativo = FALSE WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private void inserirTemplate(Connection conn, TemplateRelatorio template) throws SQLException {
        String sql = "INSERT INTO template_relatorio (nome, descricao, periodo_padrao, agrupamento, ativo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, template.getNome());
            stmt.setString(2, template.getDescricao());
            stmt.setString(3, template.getPeriodoPadrao());
            stmt.setString(4, template.getAgrupamento());
            stmt.setBoolean(5, template.isAtivo());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    template.setId(rs.getInt(1));
                }
            }
        }
    }

    private void substituirCategorias(Connection conn, int templateId, List<Integer> categoriaIds) throws SQLException {
        try (PreparedStatement delete = conn.prepareStatement("DELETE FROM template_categoria WHERE template_id = ?")) {
            delete.setInt(1, templateId);
            delete.executeUpdate();
        }
        if (categoriaIds == null || categoriaIds.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO template_categoria (template_id, categoria_id) VALUES (?, ?)";
        try (PreparedStatement insert = conn.prepareStatement(sql)) {
            Set<Integer> unicos = new HashSet<>(categoriaIds);
            for (Integer categoriaId : unicos) {
                insert.setInt(1, templateId);
                insert.setInt(2, categoriaId);
                insert.addBatch();
            }
            insert.executeBatch();
        }
    }

    private void substituirIndicadores(Connection conn, int templateId, List<String> indicadores) throws SQLException {
        try (PreparedStatement delete = conn.prepareStatement("DELETE FROM template_indicador WHERE template_id = ?")) {
            delete.setInt(1, templateId);
            delete.executeUpdate();
        }
        if (indicadores == null || indicadores.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO template_indicador (template_id, indicador) VALUES (?, ?)";
        try (PreparedStatement insert = conn.prepareStatement(sql)) {
            Set<String> unicos = new HashSet<>(indicadores);
            for (String indicador : unicos) {
                insert.setInt(1, templateId);
                insert.setString(2, indicador);
                insert.addBatch();
            }
            insert.executeBatch();
        }
    }

    private List<Integer> carregarCategorias(Connection conn, int templateId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT categoria_id FROM template_categoria WHERE template_id = ? ORDER BY categoria_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, templateId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("categoria_id"));
                }
            }
        }
        return ids;
    }

    private List<String> carregarIndicadores(Connection conn, int templateId) throws SQLException {
        List<String> indicadores = new ArrayList<>();
        String sql = "SELECT indicador FROM template_indicador WHERE template_id = ? ORDER BY indicador";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, templateId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    indicadores.add(rs.getString("indicador"));
                }
            }
        }
        return indicadores;
    }

    private TemplateRelatorio mapear(ResultSet rs) throws SQLException {
        TemplateRelatorio template = new TemplateRelatorio();
        template.setId(rs.getInt("id"));
        template.setNome(rs.getString("nome"));
        template.setDescricao(rs.getString("descricao"));
        template.setPeriodoPadrao(rs.getString("periodo_padrao"));
        template.setAgrupamento(rs.getString("agrupamento"));
        template.setAtivo(rs.getBoolean("ativo"));
        return template;
    }
}
