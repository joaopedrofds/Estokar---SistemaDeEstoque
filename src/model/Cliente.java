package model;

import java.time.LocalDate;

public class Cliente {
    private int id;
    private String nome;
    private String cpfCnpj;
    private String telefone;
    private String email;
    private String tipo; // PF ou PJ
    private String cep;
    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private boolean ativo;
    private LocalDate dataNascimento;

    public Cliente() {
    }

    public Cliente(int id, String nome, String cpfCnpj, String telefone, String email, String tipo,
            String cep, String rua, String numero, String bairro, String cidade, String estado,
            boolean ativo, LocalDate dataNascimento) {
        this.id = id;
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.telefone = telefone;
        this.email = email;
        this.tipo = tipo;
        this.cep = cep;
        this.rua = rua;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.ativo = ativo;
        this.dataNascimento = dataNascimento;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    @Override
    public String toString() {
        String documento = cpfCnpj;
        if (cpfCnpj.length() == 11)
            documento = cpfCnpj.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        else if (cpfCnpj.length() == 14)
            documento = cpfCnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");

        String telefoneFormatado = telefone;
        if (telefone.matches("\\d{11}"))
            telefoneFormatado = telefone.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "$1 $2-$3");

        return "ID: " + id +
                " | Nome: " + nome +
                " | " + (tipo.equals("PF") ? "CPF: " : "CNPJ: ") + documento +
                " | Tel: " + telefoneFormatado +
                " | Email: " + email +
                " | Rua: " + rua +
                " | Nº: " + numero +
                " | Bairro: " + bairro +
                " | Cidade: " + cidade +
                " | UF: " + estado +
                " | Ativo: " + (ativo ? "Sim" : "Não") +
                " | Data Nasc.: " + (dataNascimento != null ? dataNascimento.toString() : "N/A");
    }
}
