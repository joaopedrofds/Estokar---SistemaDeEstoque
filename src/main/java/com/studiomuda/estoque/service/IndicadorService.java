package com.studiomuda.estoque.service;

import com.studiomuda.estoque.calculo.ArredondamentoDecorator;
import com.studiomuda.estoque.calculo.CalculadoraBase;
import com.studiomuda.estoque.calculo.CalculadoraIndicador;
import com.studiomuda.estoque.calculo.LogCalculoDecorator;
import com.studiomuda.estoque.calculo.ValidacaoPeriodoDecorator;
import com.studiomuda.estoque.model.AlertaIndicador;
import com.studiomuda.estoque.model.IndicadorOperacional;
import com.studiomuda.estoque.model.MetaIndicador;
import com.studiomuda.estoque.model.SnapshotIndicador;
import com.studiomuda.estoque.repository.AlertaIndicadorRepository;
import com.studiomuda.estoque.repository.CalculoIndicadorRepository;
import com.studiomuda.estoque.repository.IndicadorOperacionalRepository;
import com.studiomuda.estoque.repository.MetaIndicadorRepository;
import com.studiomuda.estoque.repository.SnapshotIndicadorRepository;
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
    private final IndicadorOperacionalRepository indicadorRepo;
    private final MetaIndicadorRepository metaRepo;
    private final SnapshotIndicadorRepository snapshotRepo;
    private final AlertaIndicadorRepository alertaRepo;

    /**
     * Cadeia de decorators usada para calcular o valor de um indicador.
     * Camadas (de fora para dentro): Log -> Validação de período -> Arredondamento -> Base (JPA).
     */
    private final CalculadoraIndicador calculadora;

    public IndicadorService(IndicadorOperacionalRepository indicadorRepo,
                            MetaIndicadorRepository metaRepo,
                            SnapshotIndicadorRepository snapshotRepo,
                            AlertaIndicadorRepository alertaRepo,
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
        List<IndicadorOperacional> ativos = indicadorRepo.findByAtivoTrueOrderByNomeAsc();
        int recalculados = 0;
        for (IndicadorOperacional ind : ativos) {
            recalcular(ind.getId(), inicio, fim, usuarioId, username);
            recalculados++;
        }
        return recalculados;
    }

    @Transactional
    public SnapshotIndicador recalcular(int indicadorId, LocalDate inicio, LocalDate fim, int usuarioId, String username) {
        IndicadorOperacional ind = indicadorRepo.findById(indicadorId)
                .orElseThrow(() -> new IllegalArgumentException("Indicador operacional não encontrado com ID: " + indicadorId));

        double valorCalculado;
        try {
            valorCalculado = calculadora.calcular(ind, inicio, fim);
        } catch (java.sql.SQLException e) {
            // A cadeia de cálculo agora usa JPA; a SQLException permanece na assinatura
            // apenas por compatibilidade da interface e nao deve ocorrer.
            throw new IllegalStateException("Falha ao calcular indicador", e);
        }

        // Criar e salvar snapshot imutável
        SnapshotIndicador snap = new SnapshotIndicador();
        snap.setIndicadorId(indicadorId);
        snap.setValorCalculado(valorCalculado);
        snap.setPeriodoInicio(inicio);
        snap.setPeriodoFim(fim);
        snap.setExecutadoPorId(usuarioId > 0 ? usuarioId : null);
        snap.setExecutadoPor(username);
        snap.setDetalheRastreio("Cálculo periódico de " + ind.getNome());

        snapshotRepo.save(snap);

        // Validar contra a meta vigente
        MetaIndicador meta = metaRepo.buscarVigentesPorIndicador(indicadorId)
                .stream().findFirst().orElse(null);
        if (meta != null) {
            boolean violada = meta.isViolada(valorCalculado);
            boolean critico = meta.isCritico(valorCalculado);

            AlertaIndicador alertaExistente =
                    alertaRepo.findFirstByIndicadorIdAndStatusOrderByDataAlertaDesc(indicadorId, "ATIVO");

            if (violada || critico) {
                String tipoViolacao = critico ? "ACIMA_CRITICO" : "ABAIXO_META";
                String mensagem = gerarMensagemAlerta(ind.getNome(), valorCalculado, meta, critico);

                if (alertaExistente != null) {
                    // Atualiza o alerta ativo existente
                    alertaExistente.setSnapshotId(snap.getId());
                    alertaExistente.setValorEncontrado(valorCalculado);
                    alertaExistente.setTipoViolacao(tipoViolacao);
                    alertaExistente.setMensagem(mensagem);
                    alertaRepo.save(alertaExistente);
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
                    alertaRepo.save(novoAlerta);
                }
            } else if (alertaExistente != null) {
                // Meta atingida com sucesso e havia alerta ativo: resolvemos automaticamente.
                aplicarResolucao(alertaExistente, "SISTEMA_AUTO_RESOLVE",
                        "Meta recuperada automaticamente após recálculo. Valor atual: " + valorCalculado);
                alertaRepo.save(alertaExistente);
            }
        }

        return snap;
    }

    /** Salva uma meta e, se ela for ativa, desativa as demais metas do indicador. */
    @Transactional
    public void salvarMeta(MetaIndicador meta) {
        metaRepo.save(meta);
        if (meta.isAtivo()) {
            metaRepo.desativarOutrasMetas(meta.getIndicadorId(), meta.getId());
        }
    }

    /** Resolve manualmente um alerta (ação do gerente). */
    @Transactional
    public void resolverAlerta(int alertaId, String resolvidoPor, String observacao) {
        AlertaIndicador alerta = alertaRepo.findById(alertaId)
                .orElseThrow(() -> new IllegalArgumentException("Alerta não encontrado com ID: " + alertaId));
        aplicarResolucao(alerta, resolvidoPor, observacao);
        alertaRepo.save(alerta);
    }

    public List<IndicadorOperacional> listarIndicadores() {
        return indicadorRepo.findAllByOrderByNomeAsc();
    }

    public IndicadorOperacional buscarIndicador(int id) {
        return indicadorRepo.findById(id).orElse(null);
    }

    public MetaIndicador buscarMetaVigente(int indicadorId) {
        return metaRepo.buscarVigentesPorIndicador(indicadorId).stream().findFirst().orElse(null);
    }

    public SnapshotIndicador buscarUltimoSnapshot(int indicadorId) {
        return snapshotRepo.findFirstByIndicadorIdOrderByDataExecucaoDesc(indicadorId);
    }

    public List<SnapshotIndicador> listarSnapshots() {
        List<SnapshotIndicador> snaps = snapshotRepo.findAllByOrderByDataExecucaoDesc();
        Map<Integer, IndicadorOperacional> indicadores = mapaIndicadores();
        for (SnapshotIndicador s : snaps) {
            IndicadorOperacional ind = indicadores.get(s.getIndicadorId());
            if (ind != null) {
                s.setIndicadorNome(ind.getNome());
                s.setIndicadorCodigo(ind.getCodigo());
            }
        }
        return snaps;
    }

    public List<AlertaIndicador> listarAlertas(String status) {
        List<AlertaIndicador> alertas = alertaRepo.findByStatusOrderByDataAlertaDesc(status);
        Map<Integer, IndicadorOperacional> indicadores = mapaIndicadores();
        for (AlertaIndicador a : alertas) {
            IndicadorOperacional ind = indicadores.get(a.getIndicadorId());
            if (ind != null) {
                a.setIndicadorNome(ind.getNome());
                a.setIndicadorCodigo(ind.getCodigo());
            }
        }
        return alertas;
    }

    private Map<Integer, IndicadorOperacional> mapaIndicadores() {
        return indicadorRepo.findAll().stream()
                .collect(Collectors.toMap(IndicadorOperacional::getId, Function.identity()));
    }

    private void aplicarResolucao(AlertaIndicador alerta, String resolvidoPor, String observacao) {
        alerta.setStatus("RESOLVIDO");
        alerta.setResolvidoPor(resolvidoPor);
        alerta.setObservacao(observacao);
        alerta.setDataResolucao(LocalDateTime.now());
    }

    private String gerarMensagemAlerta(String nomeIndicador, double valor, MetaIndicador meta, boolean critico) {
        String statusText = critico ? "ATINGIU LIMITE CRÍTICO" : "VIOLOU META MÍNIMA";
        String operadorSinal = "MAIOR_IGUAL".equals(meta.getOperador()) ? "≥" : "≤";
        double limite = critico ? meta.getLimiteCritico() : meta.getValorAlvo();

        return String.format("O indicador '%s' %s. Encontrado: %.2f (Esperado %s %.2f)",
            nomeIndicador, statusText, valor, operadorSinal, limite);
    }
}
