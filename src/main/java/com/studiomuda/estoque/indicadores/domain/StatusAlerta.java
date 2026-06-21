package com.studiomuda.estoque.indicadores.domain;

/** Estado de um {@link AlertaIndicador}: ativo (não conformidade aberta) ou resolvido. */
public enum StatusAlerta {
    ATIVO,
    RESOLVIDO;

    /** Resolve o status a partir do texto persistido, validando. */
    public static StatusAlerta de(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Status do alerta não pode ser vazio.");
        }
        try {
            return StatusAlerta.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Status de alerta inválido: '" + valor + "'. Use ATIVO ou RESOLVIDO.");
        }
    }
}
