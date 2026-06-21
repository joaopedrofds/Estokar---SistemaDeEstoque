package com.studiomuda.estoque.financeiro.domain;

import java.util.Objects;
import java.util.UUID;

/** Value Object de identidade do {@link TemplateRelatorio} (E-12), padrão PetCollar. */
public final class TemplateId {

    private final String valor;

    private TemplateId(String valor) {
        this.valor = valor;
    }

    public static TemplateId gerar() {
        return new TemplateId(UUID.randomUUID().toString());
    }

    public static TemplateId de(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("TemplateId não pode ser vazio.");
        }
        return new TemplateId(valor);
    }

    public String getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TemplateId)) return false;
        return Objects.equals(valor, ((TemplateId) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor; }
}
