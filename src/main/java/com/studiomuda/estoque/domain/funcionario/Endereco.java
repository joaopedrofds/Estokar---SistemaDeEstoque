package com.studiomuda.estoque.domain.funcionario;

import java.util.Objects;

public final class Endereco {
    private final String cep;
    private final String rua;
    private final String numero;
    private final String bairro;
    private final String cidade;
    private final String estado;

    public Endereco(String cep, String rua, String numero, String bairro, String cidade, String estado) {
        this.cep = cep;
        this.rua = rua;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
    }

    public String cep() { return cep; }
    public String rua() { return rua; }
    public String numero() { return numero; }
    public String bairro() { return bairro; }
    public String cidade() { return cidade; }
    public String estado() { return estado; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Endereco)) return false;
        Endereco e = (Endereco) o;
        return Objects.equals(cep, e.cep) && Objects.equals(rua, e.rua)
                && Objects.equals(numero, e.numero) && Objects.equals(bairro, e.bairro)
                && Objects.equals(cidade, e.cidade) && Objects.equals(estado, e.estado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cep, rua, numero, bairro, cidade, estado);
    }
}
