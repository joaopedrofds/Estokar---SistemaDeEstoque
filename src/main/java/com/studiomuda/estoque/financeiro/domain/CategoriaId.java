package com.studiomuda.estoque.financeiro.domain;

import java.util.Objects;
import java.util.UUID;

/** Value Object de identidade da {@link CategoriaFinanceira} (E-12), padrão PetCollar. */
public final class CategoriaId {

    private final String valor;

    private CategoriaId(String valor) {
        this.valor = valor;
    }

    public static CategoriaId gerar() {
        return new CategoriaId(UUID.randomUUID().toString());
    }

    public static CategoriaId de(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("CategoriaId não pode ser vazio.");
        }
        return new CategoriaId(valor);
    }

    public String getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoriaId)) return false;
        return Objects.equals(valor, ((CategoriaId) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor; }
}
