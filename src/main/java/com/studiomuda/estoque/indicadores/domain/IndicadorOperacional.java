package com.studiomuda.estoque.indicadores.domain;

/**
 * Indicador operacional (E-13) — agregado raiz do contexto Indicadores, em
 * domínio puro (padrão PetCollar). Identidade por {@link IndicadorId}. Define o
 * que se mede ({@code tipoCalculo}) e como se exibe; o cálculo do valor vive na
 * cadeia de decorators de {@code calculo/}.
 */
public class IndicadorOperacional {

    private final IndicadorId id;
    private final String codigo;
    private final String nome;
    private final String descricao;
    private final String tipoCalculo;
    private final String periodoPadrao;
    private final boolean ativo;

    public IndicadorOperacional(IndicadorId id, String codigo, String nome, String descricao,
                                String tipoCalculo, String periodoPadrao, boolean ativo) {
        if (id == null) {
            throw new IllegalArgumentException("Id do indicador não pode ser nulo.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do indicador não pode ser vazio.");
        }
        this.id = id;
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.tipoCalculo = tipoCalculo;
        this.periodoPadrao = periodoPadrao;
        this.ativo = ativo;
    }

    public IndicadorId getId() { return id; }

    public String getCodigo() { return codigo; }

    public String getNome() { return nome; }

    public String getDescricao() { return descricao; }

    public String getTipoCalculo() { return tipoCalculo; }

    public String getPeriodoPadrao() { return periodoPadrao; }

    public boolean isAtivo() { return ativo; }
}
