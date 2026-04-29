package com.studiomuda.estoque.domain.cliente;

import java.util.Objects;

public final class CpfCnpj {
    private final String digitos;
    private final TipoPessoa tipo;

    private CpfCnpj(String digitos, TipoPessoa tipo) {
        this.digitos = digitos;
        this.tipo = tipo;
    }

    public static CpfCnpj of(String valor, TipoPessoa tipo) {
        if (valor == null) {
            throw new IllegalArgumentException("CPF/CNPJ é obrigatório");
        }
        String somenteDigitos = valor.replaceAll("[^0-9]", "");
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de pessoa é obrigatório");
        }
        if (somenteDigitos.length() != tipo.tamanhoDocumento()) {
            throw new IllegalArgumentException(
                    "O " + tipo.rotulo() + " deve conter exatamente " + tipo.tamanhoDocumento()
                            + " dígitos. Valor informado: " + somenteDigitos);
        }
        return new CpfCnpj(somenteDigitos, tipo);
    }

    public String digitos() {
        return digitos;
    }

    public TipoPessoa tipo() {
        return tipo;
    }

    public String formatado() {
        if (tipo == TipoPessoa.PF) {
            return digitos.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }
        return digitos.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CpfCnpj)) return false;
        CpfCnpj that = (CpfCnpj) o;
        return digitos.equals(that.digitos) && tipo == that.tipo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(digitos, tipo);
    }

    @Override
    public String toString() {
        return digitos;
    }
}
