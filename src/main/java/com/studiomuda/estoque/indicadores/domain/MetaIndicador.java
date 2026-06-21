package com.studiomuda.estoque.indicadores.domain;

import java.time.LocalDate;

/**
 * Meta de um indicador operacional (E-13) — agregado de domínio puro, sem
 * dependência de framework (padrão PetCollar / slice canônico).
 *
 * <p>Identidade por {@link MetaIndicadorId}; o {@code indicadorId} é apenas a
 * referência ({@code int}) ao agregado {@code IndicadorOperacional} (ainda
 * legado, não refatorado). A regra de violação/criticidade vive em
 * {@link OperadorMeta}.</p>
 */
public class MetaIndicador {

    private final MetaIndicadorId id;
    private final IndicadorId indicadorId;
    private final double valorAlvo;
    private final double limiteCritico;
    private final OperadorMeta operador;
    private final LocalDate vigenciaInicio;
    private final LocalDate vigenciaFim;
    private final boolean ativo;

    public MetaIndicador(MetaIndicadorId id, IndicadorId indicadorId, double valorAlvo, double limiteCritico,
                         String operador, LocalDate vigenciaInicio, LocalDate vigenciaFim, boolean ativo) {
        if (id == null) {
            throw new IllegalArgumentException("Id da meta não pode ser nulo.");
        }
        if (indicadorId == null) {
            throw new IllegalArgumentException("Id do indicador da meta não pode ser nulo.");
        }
        OperadorMeta operadorResolvido = OperadorMeta.de(operador);
        if (vigenciaInicio == null) {
            throw new IllegalArgumentException("Vigência inicial da meta não pode ser nula.");
        }
        if (vigenciaFim != null && vigenciaFim.isBefore(vigenciaInicio)) {
            throw new IllegalArgumentException("Vigência final não pode ser anterior à inicial.");
        }
        this.id = id;
        this.indicadorId = indicadorId;
        this.valorAlvo = valorAlvo;
        this.limiteCritico = limiteCritico;
        this.operador = operadorResolvido;
        this.vigenciaInicio = vigenciaInicio;
        this.vigenciaFim = vigenciaFim;
        this.ativo = ativo;
    }

    /**
     * Cria uma meta padrão (em branco) para um indicador, usada como base do
     * formulário de cadastro: operador de desempenho, vigência iniciando hoje e ativa.
     */
    public static MetaIndicador padraoPara(IndicadorId indicadorId) {
        return new MetaIndicador(MetaIndicadorId.gerar(), indicadorId, 0.0, 0.0,
            OperadorMeta.MAIOR_IGUAL.name(), LocalDate.now(), null, true);
    }

    public MetaIndicadorId getId() { return id; }

    public IndicadorId getIndicadorId() { return indicadorId; }

    public double getValorAlvo() { return valorAlvo; }

    public double getLimiteCritico() { return limiteCritico; }

    /** Nome canônico do operador ({@code MAIOR_IGUAL}/{@code MENOR_IGUAL}) para formulários/persistência. */
    public String getOperador() { return operador.name(); }

    public LocalDate getVigenciaInicio() { return vigenciaInicio; }

    public LocalDate getVigenciaFim() { return vigenciaFim; }

    public boolean isAtivo() { return ativo; }

    /** Sinal de comparação da meta ({@code ≥} ou {@code ≤}) para mensagens. */
    public String getSinal() { return operador.getSinal(); }

    /** Verdadeiro quando o valor calculado não atinge o valor alvo da meta. */
    public boolean isViolada(double valorCalculado) {
        return operador.violado(valorCalculado, valorAlvo);
    }

    /** Verdadeiro quando o valor calculado ultrapassa o limite crítico da meta. */
    public boolean isCritico(double valorCalculado) {
        return operador.violado(valorCalculado, limiteCritico);
    }
}
