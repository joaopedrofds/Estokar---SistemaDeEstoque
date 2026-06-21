package com.studiomuda.estoque.financeiro.domain;

import java.util.Objects;
import java.util.UUID;

/** Value Object de identidade do {@link RelatorioGerado} (E-12), padrão PetCollar. */
public final class RelatorioId {

    private final String valor;

    private RelatorioId(String valor) {
        this.valor = valor;
    }

    public static RelatorioId gerar() {
        return new RelatorioId(UUID.randomUUID().toString());
    }

    public static RelatorioId de(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("RelatorioId não pode ser vazio.");
        }
        return new RelatorioId(valor);
    }

    public String getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelatorioId)) return false;
        return Objects.equals(valor, ((RelatorioId) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor; }
}
