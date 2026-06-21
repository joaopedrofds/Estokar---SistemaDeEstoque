package com.studiomuda.estoque.service;

import com.studiomuda.estoque.calculo.ArredondamentoDecorator;
import com.studiomuda.estoque.calculo.CalculadoraBase;
import com.studiomuda.estoque.calculo.CalculadoraIndicador;
import com.studiomuda.estoque.calculo.LogCalculoDecorator;
import com.studiomuda.estoque.calculo.ValidacaoPeriodoDecorator;
import com.studiomuda.estoque.indicadores.application.dto.AlertaIndicadorView;
import com.studiomuda.estoque.indicadores.application.dto.SnapshotIndicadorView;
import com.studiomuda.estoque.indicadores.domain.AlertaId;
import com.studiomuda.estoque.indicadores.domain.AlertaIndicador;
import com.studiomuda.estoque.indicadores.domain.IAlertaIndicadorRepositorio;
import com.studiomuda.estoque.indicadores.domain.IIndicadorOperacionalRepositorio;
import com.studiomuda.estoque.indicadores.domain.IMetaIndicadorRepositorio;
import com.studiomuda.estoque.indicadores.domain.ISnapshotIndicadorRepositorio;
import com.studiomuda.estoque.indicadores.domain.IndicadorId;
import com.studiomuda.estoque.indicadores.domain.IndicadorOperacional;
import com.studiomuda.estoque.indicadores.domain.MetaIndicador;
import com.studiomuda.estoque.indicadores.domain.SnapshotIndicador;
import com.studiomuda.estoque.indicadores.domain.SnapshotIndicadorId;
import com.studiomuda.estoque.indicadores.domain.StatusAlerta;
import com.studiomuda.estoque.indicadores.domain.TipoViolacao;
import com.studiomuda.estoque.repository.CalculoIndicadorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class IndicadorService {
    private final IIndicadorOperacionalRepositorio indicadorRepo;
    private final IMetaIndicadorRepositorio metaRepo;
    private final ISnapshotIndicadorRepositorio snapshotRepo;
    private final IAlertaIndicadorRepositorio alertaRepo;

    /**
     * Cadeia de decorators usada para calcular o valor de um indicador.
     * Camadas (de fora para dentro): Log -> Validação de período -> Arredondamento -> Base (JPA).
     */
    private final CalculadoraIndicador calculadora;

    public IndicadorService(IIndicadorOperacionalRepositorio indicadorRepo,
                            IMetaIndicadorRepositorio metaRepo,
                            ISnapshotIndicadorRepositorio snapshotRepo,
                            IAlertaIndicadorRepositorio alertaRepo,
                            CalculoIndicadorRepository calculoRepo) {
        this.indicadorRepo = indicadorRepo;
        this.metaRepo = metaRepo;
        this.snapshotRepo = snapshotRepo;
        this.alertaRepo = alertaRepo;
        this.calculadora =
                new LogCalculoDecorator(
                    new ValidacaoPeriodoDecorator(
                        new ArredondamentoDecorator(
                            new CalculadoraBase(calculoRepo))));
    }

    @Transactional
    public int recalcularTodos(LocalDate inicio, LocalDate fim, int usuarioId, String username) {
        List<IndicadorOperacional> ativos = indicadorRepo.listarAtivosOrdenadoPorNome();
        int recalculados = 0;
        for (IndicadorOperacional ind : ativos) {
            recalcular(ind.getId(), inicio, fim, usuarioId, username);
            recalculados++;
        }
        return recalculados;
    }

    @Transactional
    public SnapshotIndicador recalcular(IndicadorId indicadorId, LocalDate inicio, LocalDate fim, int usuarioId, String username) {
        IndicadorOperacional ind = indicadorRepo.buscarPorId(indicadorId)
                .orElseThrow(() -> new IllegalArgumentException("Indicador operacional não encontrado com ID: " + indicadorId));

        double valorCalculado;
        try {
            valorCalculado = calculadora.calcular(ind, inicio, fim);
        } catch (java.sql.SQLException e) {
            // A cadeia de cálculo agora usa JPA; a SQLException permanece na assinatura
            // apenas por compatibilidade da interface e nao deve ocorrer.
            throw new IllegalStateException("Falha ao calcular indicador", e);
        }

        // Criar e salvar snapshot imutável (identidade gerada no domínio)
        SnapshotIndicador snap = new SnapshotIndicador(
                SnapshotIndicadorId.gerar(),
                indicadorId,
                valorCalculado,
                inicio,
                fim,
                usuarioId > 0 ? usuarioId : null,
                username,
                "Cálculo periódico de " + ind.getNome());

        snapshotRepo.salvar(snap);

        // Validar contra a meta vigente
        MetaIndicador meta = metaRepo.buscarVigentesPorIndicador(indicadorId)
                .stream().findFirst().orElse(null);
        if (meta != null) {
            boolean violada = meta.isViolada(valorCalculado);
            boolean critico = meta.isCritico(valorCalculado);

            AlertaIndicador alertaExistente =
                    alertaRepo.buscarAtivoPorIndicador(indicadorId).orElse(null);

            if (violada || critico) {
                TipoViolacao tipoViolacao = critico ? TipoViolacao.ACIMA_CRITICO : TipoViolacao.ABAIXO_META;
                String mensagem = gerarMensagemAlerta(ind.getNome(), valorCalculado, meta, critico);

                if (alertaExistente != null) {
                    // Reatualiza o alerta ativo existente com a violação mais recente.
                    alertaExistente.registrarNovaOcorrencia(snap.getId(), tipoViolacao, valorCalculado, mensagem);
                    alertaRepo.salvar(alertaExistente);
                } else {
                    // Cria novo alerta ATIVO (identidade gerada no domínio).
                    AlertaIndicador novoAlerta = AlertaIndicador.criar(
                            AlertaId.gerar(), indicadorId, snap.getId(), tipoViolacao,
                            meta.getValorAlvo(), valorCalculado, mensagem);
                    alertaRepo.salvar(novoAlerta);
                }
            } else if (alertaExistente != null) {
                // Meta atingida com sucesso e havia alerta ativo: resolvemos automaticamente.
                alertaExistente.resolver("SISTEMA_AUTO_RESOLVE",
                        "Meta recuperada automaticamente após recálculo. Valor atual: " + valorCalculado);
                alertaRepo.salvar(alertaExistente);
            }
        }

        return snap;
    }

    /** Salva uma meta e, se ela for ativa, desativa as demais metas do indicador. */
    @Transactional
    public void salvarMeta(MetaIndicador meta) {
        metaRepo.salvar(meta);
        if (meta.isAtivo()) {
            metaRepo.desativarOutrasMetas(meta.getIndicadorId(), meta.getId());
        }
    }

    /** Resolve manualmente um alerta (ação do gerente). */
    @Transactional
    public void resolverAlerta(String alertaId, String resolvidoPor, String observacao) {
        AlertaIndicador alerta = alertaRepo.buscarPorId(AlertaId.de(alertaId))
                .orElseThrow(() -> new IllegalArgumentException("Alerta não encontrado com ID: " + alertaId));
        alerta.resolver(resolvidoPor, observacao);
        alertaRepo.salvar(alerta);
    }

    public List<IndicadorOperacional> listarIndicadores() {
        return indicadorRepo.listarTodosOrdenadoPorNome();
    }

    public IndicadorOperacional buscarIndicador(IndicadorId id) {
        return indicadorRepo.buscarPorId(id).orElse(null);
    }

    public MetaIndicador buscarMetaVigente(IndicadorId indicadorId) {
        return metaRepo.buscarVigentesPorIndicador(indicadorId).stream().findFirst().orElse(null);
    }

    public SnapshotIndicador buscarUltimoSnapshot(IndicadorId indicadorId) {
        return snapshotRepo.buscarUltimoPorIndicador(indicadorId).orElse(null);
    }

    public List<SnapshotIndicadorView> listarSnapshots() {
        Map<IndicadorId, IndicadorOperacional> indicadores = mapaIndicadores();
        return snapshotRepo.listarTodosOrdenadoPorExecucao().stream()
                .map(s -> SnapshotIndicadorView.de(s, indicadores.get(s.getIndicadorId())))
                .toList();
    }

    public List<AlertaIndicadorView> listarAlertas(String status) {
        Map<IndicadorId, IndicadorOperacional> indicadores = mapaIndicadores();
        return alertaRepo.listarPorStatus(StatusAlerta.de(status)).stream()
                .map(a -> AlertaIndicadorView.de(a, indicadores.get(a.getIndicadorId())))
                .toList();
    }

    private Map<IndicadorId, IndicadorOperacional> mapaIndicadores() {
        return indicadorRepo.listarTodosOrdenadoPorNome().stream()
                .collect(Collectors.toMap(IndicadorOperacional::getId, Function.identity()));
    }

    private String gerarMensagemAlerta(String nomeIndicador, double valor, MetaIndicador meta, boolean critico) {
        String statusText = critico ? "ATINGIU LIMITE CRÍTICO" : "VIOLOU META MÍNIMA";
        String operadorSinal = meta.getSinal();
        double limite = critico ? meta.getLimiteCritico() : meta.getValorAlvo();

        return String.format("O indicador '%s' %s. Encontrado: %.2f (Esperado %s %.2f)",
            nomeIndicador, statusText, valor, operadorSinal, limite);
    }
}
