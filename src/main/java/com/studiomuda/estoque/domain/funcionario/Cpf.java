package com.studiomuda.estoque.domain.funcionario;

import java.util.Objects;

public final class Cpf {
    private static final int TAMANHO = 11;

    private final String digitos;

    private Cpf(String digitos) {
        this.digitos = digitos;
    }

    public static Cpf of(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("O CPF é obrigatório.");
        }
        String somenteDigitos = valor.replaceAll("[^0-9]", "");
        if (somenteDigitos.length() != TAMANHO) {
            throw new IllegalArgumentException("O CPF deve conter " + TAMANHO + " dígitos.");
        }
        return new Cpf(somenteDigitos);
    }

    public String digitos() {
        return digitos;
    }

    public String formatado() {
        return digitos.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cpf)) return false;
        return digitos.equals(((Cpf) o).digitos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(digitos);
    }

    @Override
    public String toString() {
        return digitos;
    }
}
