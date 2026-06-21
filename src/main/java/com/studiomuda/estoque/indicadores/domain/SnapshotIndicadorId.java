package com.studiomuda.estoque.indicadores.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object de identidade do {@link SnapshotIndicador} (E-13), no padrão do
 * PetCollar: {@code final class}, construtor privado, factories {@code gerar()}
 * (novo UUID) e {@code de(String)} (reconstrução validada), igualdade por valor.
 */
public final class SnapshotIndicadorId {

    private final String valor;

    private SnapshotIndicadorId(String valor) {
        this.valor = valor;
    }

    public static SnapshotIndicadorId gerar() {
        return new SnapshotIndicadorId(UUID.randomUUID().toString());
    }

    public static SnapshotIndicadorId de(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("SnapshotIndicadorId não pode ser vazio.");
        }
        return new SnapshotIndicadorId(valor);
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SnapshotIndicadorId)) return false;
        return Objects.equals(valor, ((SnapshotIndicadorId) o).valor);
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
