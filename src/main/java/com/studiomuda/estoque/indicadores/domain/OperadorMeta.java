package com.studiomuda.estoque.indicadores.domain;

/**
 * Operador de comparação de uma {@link MetaIndicador}: define quando um valor
 * calculado viola um limite (alvo ou crítico) e qual sinal exibir.
 *
 * <p>Centraliza a regra que antes vivia espalhada como comparação por string
 * mágica ({@code "MAIOR_IGUAL"} / {@code "MENOR_IGUAL"}) na entidade e no
 * {@code IndicadorService}.</p>
 */
public enum OperadorMeta {

    /** Meta de desempenho: o valor calculado deve ser MAIOR ou IGUAL ao limite. */
    MAIOR_IGUAL("≥") {
        @Override
        public boolean violado(double valorCalculado, double limite) {
            return valorCalculado < limite;
        }
    },

    /** Meta de minimização: o valor calculado deve ser MENOR ou IGUAL ao limite. */
    MENOR_IGUAL("≤") {
        @Override
        public boolean violado(double valorCalculado, double limite) {
            return valorCalculado > limite;
        }
    };

    private final String sinal;

    OperadorMeta(String sinal) {
        this.sinal = sinal;
    }

    /** Sinal de comparação para exibição em mensagens ({@code ≥} ou {@code ≤}). */
    public String getSinal() {
        return sinal;
    }

    /** Indica se o valor calculado viola o limite informado (alvo ou crítico). */
    public abstract boolean violado(double valorCalculado, double limite);

    /**
     * Resolve o operador a partir do texto persistido, tolerando espaços e caixa.
     *
     * @throws IllegalArgumentException se o valor for nulo ou não reconhecido.
     */
    public static OperadorMeta de(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Operador da meta não pode ser vazio.");
        }
        try {
            return OperadorMeta.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Operador de meta inválido: '" + valor + "'. Use MAIOR_IGUAL ou MENOR_IGUAL.");
        }
    }
}
