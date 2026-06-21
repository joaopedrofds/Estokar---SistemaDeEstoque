package com.studiomuda.estoque.financeiro.domain;

import java.util.Objects;
import java.util.UUID;

/** Value Object de identidade do {@link LancamentoAjuste} (E-12), padrão PetCollar. */
public final class LancamentoAjusteId {

    private final String valor;

    private LancamentoAjusteId(String valor) {
        this.valor = valor;
    }

    public static LancamentoAjusteId gerar() {
        return new LancamentoAjusteId(UUID.randomUUID().toString());
    }

    public static LancamentoAjusteId de(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("LancamentoAjusteId não pode ser vazio.");
        }
        return new LancamentoAjusteId(valor);
    }

    public String getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LancamentoAjusteId)) return false;
        return Objects.equals(valor, ((LancamentoAjusteId) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor; }
}
