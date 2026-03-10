package com.studiomuda.estoque.model;

public class Funcionario {
    private int id;
    private String nome;
    private String cpf;
    private String cargo;
    private java.sql.Date data_nasc;
    private String telefone;
    private String cep;
    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private boolean ativo;

    public Funcionario() {}

    public Funcionario(int id, String nome, String cpf, String cargo, java.sql.Date data_nasc,
                       String telefone, String cep, String rua, String numero,
                       String bairro, String cidade, String estado, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.cargo = cargo;
        this.data_nasc = data_nasc;
        this.telefone = telefone;
        this.cep = cep;
        this.rua = rua;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.ativo = ativo;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public java.sql.Date getData_nasc() { return data_nasc; }
    public void setData_nasc(java.sql.Date data_nasc) { this.data_nasc = data_nasc; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        String cpfFormatado = cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        String telefoneFormatado = telefone;
        if (telefone.matches("\\d{11}"))
            telefoneFormatado = telefone.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "$1 $2-$3");

        return "ID: " + id +
                " | Nome: " + nome +
                " | CPF: " + cpfFormatado +
                " | Cargo: " + cargo +
                " | Nasc: " + data_nasc +
                " | Tel: " + telefoneFormatado +
                " | Rua: " + rua +
                " | Número: " + numero +
                " | Bairro: " + bairro +
                " | Cidade: " + cidade +
                " | UF: " + estado +
                " | Ativo: " + (ativo ? "Sim" : "Não");
    }
}
