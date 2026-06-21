package com.studiomuda.estoque.indicadores.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object de identidade do {@link AlertaIndicador} (E-13), no padrão PetCollar.
 */
public final class AlertaId {

    private final String valor;

    private AlertaId(String valor) {
        this.valor = valor;
    }

    public static AlertaId gerar() {
        return new AlertaId(UUID.randomUUID().toString());
    }

    public static AlertaId de(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("AlertaId não pode ser vazio.");
        }
        return new AlertaId(valor);
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlertaId)) return false;
        return Objects.equals(valor, ((AlertaId) o).valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}
