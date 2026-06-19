package com.studiomuda.estoque.service;

import com.studiomuda.estoque.calculo.ArredondamentoDecorator;
import com.studiomuda.estoque.calculo.CalculadoraBase;
import com.studiomuda.estoque.calculo.CalculadoraIndicador;
import com.studiomuda.estoque.calculo.LogCalculoDecorator;
import com.studiomuda.estoque.calculo.ValidacaoPeriodoDecorator;
import com.studiomuda.estoque.dao.AlertaIndicadorDAO;
import com.studiomuda.estoque.dao.IndicadorOperacionalDAO;
import com.studiomuda.estoque.dao.MetaIndicadorDAO;
import com.studiomuda.estoque.dao.SnapshotIndicadorDAO;
import com.studiomuda.estoque.model.AlertaIndicador;
import com.studiomuda.estoque.model.IndicadorOperacional;
import com.studiomuda.estoque.model.MetaIndicador;
import com.studiomuda.estoque.model.SnapshotIndicador;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class IndicadorService {
    private final IndicadorOperacionalDAO indicadorDAO = new IndicadorOperacionalDAO();
    private final MetaIndicadorDAO metaDAO = new MetaIndicadorDAO();
    private final SnapshotIndicadorDAO snapshotDAO = new SnapshotIndicadorDAO();
    private final AlertaIndicadorDAO alertaDAO = new AlertaIndicadorDAO();

    /**
     * Cadeia de decorators usada para calcular o valor de um indicador.
     * Camadas (de fora para dentro): Log -> Validação de período -> Arredondamento -> Base (SQL).
     */
    private final CalculadoraIndicador calculadora =
            new LogCalculoDecorator(
                new ValidacaoPeriodoDecorator(
                    new ArredondamentoDecorator(
                        new CalculadoraBase())));

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

        // Calcula o valor através da cadeia de decorators (log -> validação -> arredondamento -> base)
        double valorCalculado = calculadora.calcular(ind, inicio, fim);

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

    private String gerarMensagemAlerta(String nomeIndicador, double valor, MetaIndicador meta, boolean critico) {
        String statusText = critico ? "ATINGIU LIMITE CRÍTICO" : "VIOLOU META MÍNIMA";
        String operadorSinal = "MAIOR_IGUAL".equals(meta.getOperador()) ? "≥" : "≤";
        double limite = critico ? meta.getLimiteCritico() : meta.getValorAlvo();
        
        return String.format("O indicador '%s' %s. Encontrado: %.2f (Esperado %s %.2f)", 
            nomeIndicador, statusText, valor, operadorSinal, limite);
    }
}
