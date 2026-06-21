package com.studiomuda.estoque.indicadores.domain;

/**
 * Natureza da violação que originou um {@link AlertaIndicador}: o valor ficou
 * aquém da meta ({@code ABAIXO_META}) ou ultrapassou o limite crítico
 * ({@code ACIMA_CRITICO}).
 */
public enum TipoViolacao {
    ABAIXO_META,
    ACIMA_CRITICO;

    /** Resolve o tipo a partir do texto persistido, validando. */
    public static TipoViolacao de(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de violação não pode ser vazio.");
        }
        try {
            return TipoViolacao.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Tipo de violação inválido: '" + valor + "'. Use ABAIXO_META ou ACIMA_CRITICO.");
        }
    }
}
