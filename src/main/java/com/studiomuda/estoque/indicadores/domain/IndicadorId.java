package com.studiomuda.estoque.indicadores.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object de identidade do {@link IndicadorOperacional} (E-13), no padrão
 * PetCollar. É a identidade compartilhada referenciada pelos demais agregados do
 * contexto Indicadores ({@code MetaIndicador}, {@code SnapshotIndicador}).
 */
public final class IndicadorId {

    private final String valor;

    private IndicadorId(String valor) {
        this.valor = valor;
    }

    public static IndicadorId gerar() {
        return new IndicadorId(UUID.randomUUID().toString());
    }

    public static IndicadorId de(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("IndicadorId não pode ser vazio.");
        }
        return new IndicadorId(valor);
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndicadorId)) return false;
        return Objects.equals(valor, ((IndicadorId) o).valor);
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
