package com.studiomuda.estoque.indicadores.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object de identidade da {@link MetaIndicador} (E-13), no padrão do
 * PetCollar: {@code final class}, construtor privado, factories {@code gerar()}
 * (novo UUID) e {@code de(String)} (reconstrução validada), igualdade por valor.
 */
public final class MetaIndicadorId {

    private final String valor;

    private MetaIndicadorId(String valor) {
        this.valor = valor;
    }

    public static MetaIndicadorId gerar() {
        return new MetaIndicadorId(UUID.randomUUID().toString());
    }

    public static MetaIndicadorId de(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("MetaIndicadorId não pode ser vazio.");
        }
        return new MetaIndicadorId(valor);
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetaIndicadorId)) return false;
        return Objects.equals(valor, ((MetaIndicadorId) o).valor);
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
