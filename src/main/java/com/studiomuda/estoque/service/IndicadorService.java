package com.studiomuda.estoque.service;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.dao.AlertaIndicadorDAO;
import com.studiomuda.estoque.dao.IndicadorOperacionalDAO;
import com.studiomuda.estoque.dao.MetaIndicadorDAO;
import com.studiomuda.estoque.dao.SnapshotIndicadorDAO;
import com.studiomuda.estoque.model.AlertaIndicador;
import com.studiomuda.estoque.model.IndicadorOperacional;
import com.studiomuda.estoque.model.MetaIndicador;
import com.studiomuda.estoque.model.SnapshotIndicador;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class IndicadorService {
    private final IndicadorOperacionalDAO indicadorDAO = new IndicadorOperacionalDAO();
    private final MetaIndicadorDAO metaDAO = new MetaIndicadorDAO();
    private final SnapshotIndicadorDAO snapshotDAO = new SnapshotIndicadorDAO();
    private final AlertaIndicadorDAO alertaDAO = new AlertaIndicadorDAO();

    public int recalcularTodos(LocalDate inicio, LocalDate fim, int usuarioId, String username) throws SQLException {
        List<IndicadorOperacional> ativos = indicadorDAO.listarAtivos();
        int recalculados = 0;
        for (IndicadorOperacional ind : ativos) {
            recalcular(ind.getId(), inicio, fim, usuarioId, username);
            recalculados++;
        }
        return recalculados;
    }

    public SnapshotIndicador recalcular(int indicadorId, LocalDate inicio, LocalDate fim, int usuarioId, String username) throws SQLException {
        IndicadorOperacional ind = indicadorDAO.buscarPorId(indicadorId);
        if (ind == null) {
            throw new IllegalArgumentException("Indicador operacional não encontrado com ID: " + indicadorId);
        }

        double valorCalculado = executarCalculo(ind.getTipoCalculo(), inicio, fim);

        // Criar e salvar snapshot
        SnapshotIndicador snap = new SnapshotIndicador();
        snap.setIndicadorId(indicadorId);
        snap.setValorCalculado(valorCalculado);
        snap.setPeriodoInicio(inicio);
        snap.setPeriodoFim(fim);
        snap.setExecutadoPorId(usuarioId > 0 ? usuarioId : null);
        snap.setExecutadoPor(username);
        snap.setDetalheRastreio("Cálculo periódico de " + ind.getNome());
        
        snapshotDAO.inserir(snap);

        // Validar contra a meta ativa
        MetaIndicador meta = metaDAO.buscarAtivaPorIndicador(indicadorId);
        if (meta != null) {
            boolean violada = meta.isViolada(valorCalculado);
            boolean critico = meta.isCritico(valorCalculado);

            AlertaIndicador alertaExistente = alertaDAO.buscarAtivoPorIndicador(indicadorId);

            if (violada || critico) {
                String tipoViolacao = critico ? "ACIMA_CRITICO" : "ABAIXO_META";
                String mensagem = gerarMensagemAlerta(ind.getNome(), valorCalculado, meta, critico);

                if (alertaExistente != null) {
                    // Atualiza o alerta ativo existente
                    alertaExistente.setSnapshotId(snap.getId());
                    alertaExistente.setValorEncontrado(valorCalculado);
                    alertaExistente.setTipoViolacao(tipoViolacao);
                    alertaExistente.setMensagem(mensagem);
                    alertaDAO.atualizar(alertaExistente);
                } else {
                    // Cria novo alerta
                    AlertaIndicador novoAlerta = new AlertaIndicador();
                    novoAlerta.setIndicadorId(indicadorId);
                    novoAlerta.setSnapshotId(snap.getId());
                    novoAlerta.setTipoViolacao(tipoViolacao);
                    novoAlerta.setValorEsperado(meta.getValorAlvo());
                    novoAlerta.setValorEncontrado(valorCalculado);
                    novoAlerta.setMensagem(mensagem);
                    novoAlerta.setStatus("ATIVO");
                    alertaDAO.inserir(novoAlerta);
                }
            } else {
                // Se a meta foi atingida com sucesso e havia um alerta ativo, resolvemos ele automaticamente!
                if (alertaExistente != null) {
                    alertaDAO.resolverAlerta(
                        alertaExistente.getId(), 
                        "SISTEMA_AUTO_RESOLVE", 
                        "Meta recuperada automaticamente após recálculo. Valor atual: " + valorCalculado
                    );
                }
            }
        }

        return snap;
    }

    private double executarCalculo(String tipoCalculo, LocalDate inicio, LocalDate fim) throws SQLException {
        String sql = "";
        boolean usaDatas = false;

        switch (tipoCalculo) {
            case "TICKET_MEDIO":
                sql = "SELECT COALESCE(AVG(sub.total), 0) AS valor FROM (" +
                      "  SELECT p.id, COALESCE(SUM(ip.quantidade * pr.valor), 0) - COALESCE(p.valor_desconto, 0) AS total " +
                      "  FROM pedido p " +
                      "  LEFT JOIN item_pedido ip ON p.id = ip.id_pedido " +
                      "  LEFT JOIN produto pr ON ip.id_produto = pr.id " +
                      "  WHERE p.data_requisicao >= ? AND p.data_requisicao <= ? " +
                      "  GROUP BY p.id" +
                      ") sub";
                usaDatas = true;
                break;
                
            case "ESTOQUE_CRITICO":
                sql = "SELECT COUNT(*) AS valor FROM produto WHERE quantidade <= 5";
                usaDatas = false;
                break;
                
            case "TAXA_CANCELAMENTO":
                sql = "SELECT COALESCE(" +
                      "  (SELECT COUNT(*) FROM pedido WHERE (status_pagamento = 'CANCELADO' OR status = 'CANCELADO') AND data_requisicao >= ? AND data_requisicao <= ?) * 100.0 / " +
                      "  NULLIF((SELECT COUNT(*) FROM pedido WHERE data_requisicao >= ? AND data_requisicao <= ?), 0), " +
                      "  0.0" +
                      ") AS valor";
                usaDatas = true;
                break;
                
            case "SEM_ESTOQUE":
                sql = "SELECT COUNT(*) AS valor FROM produto WHERE quantidade = 0";
                usaDatas = false;
                break;
                
            default:
                return 0.0;
        }

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (usaDatas) {
                stmt.setDate(1, Date.valueOf(inicio));
                stmt.setDate(2, Date.valueOf(fim));
                if (tipoCalculo.equals("TAXA_CANCELAMENTO")) {
                    stmt.setDate(3, Date.valueOf(inicio));
                    stmt.setDate(4, Date.valueOf(fim));
                }
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("valor");
                }
            }
        }
        return 0.0;
    }

    private String gerarMensagemAlerta(String nomeIndicador, double valor, MetaIndicador meta, boolean critico) {
        String statusText = critico ? "ATINGIU LIMITE CRÍTICO" : "VIOLOU META MÍNIMA";
        String operadorSinal = "MAIOR_IGUAL".equals(meta.getOperador()) ? "≥" : "≤";
        double limite = critico ? meta.getLimiteCritico() : meta.getValorAlvo();
        
        return String.format("O indicador '%s' %s. Encontrado: %.2f (Esperado %s %.2f)", 
            nomeIndicador, statusText, valor, operadorSinal, limite);
    }
}
