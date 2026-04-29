package com.studiomuda.estoque.domain.funcionario;

public enum Cargo {
    DIRETOR("Diretor"),
    AUXILIAR("Auxiliar"),
    ESTOQUISTA("Estoquista");

    private final String rotulo;

    Cargo(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    public static Cargo desdeRotulo(String rotulo) {
        if (rotulo == null || rotulo.trim().isEmpty()) {
            throw new IllegalArgumentException("O cargo é obrigatório.");
        }
        for (Cargo c : values()) {
            if (c.rotulo.equalsIgnoreCase(rotulo.trim()) || c.name().equalsIgnoreCase(rotulo.trim())) {
                return c;
            }
        }
        throw new IllegalArgumentException("Cargo inválido: " + rotulo);
    }
}
