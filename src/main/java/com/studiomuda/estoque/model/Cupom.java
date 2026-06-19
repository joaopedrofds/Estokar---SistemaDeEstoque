package com.studiomuda.estoque.model;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Entidade — Cupom de desconto com regras avançadas.
 * Nível Tático DDD: Entity
 * Persistência: ORM via Spring Data JPA
 */
@Entity
@Table(name = "cupom")
public class Cupom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "codigo", unique = true, nullable = false, length = 50)
    private String codigo;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "valor", nullable = false)
    private double valor;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "validade")
    private LocalDate validade;

    @Column(name = "condicoes_uso", length = 500)
    private String condicoesUso;

    @Column(name = "tipo_desconto", length = 20)
    private String tipoDesconto = "FIXO";

    @Column(name = "limite_usos")
    private Integer limiteUsos;

    @Column(name = "usos_realizados")
    private Integer usosRealizados;

    @Column(name = "cliente_id")
    private Integer clienteId;

    @Column(name = "ativo")
    private boolean ativo = true;

    public Cupom() {}

    public Cupom(int id, String codigo, String descricao, double valor,
                LocalDate dataInicio, LocalDate validade, String condicoesUso) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.valor = valor;
        this.dataInicio = dataInicio;
        this.validade = validade;
        this.condicoesUso = condicoesUso;
        this.tipoDesconto = "FIXO";
        this.ativo = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getValidade() { return validade; }
    public void setValidade(LocalDate validade) { this.validade = validade; }

    public String getCondicoesUso() { return condicoesUso; }
    public void setCondicoesUso(String condicoesUso) { this.condicoesUso = condicoesUso; }

    public String getTipoDesconto()              { return tipoDesconto; }
    public void setTipoDesconto(String t)        { this.tipoDesconto = t; }
    public Integer getLimiteUsos()               { return limiteUsos; }
    public void setLimiteUsos(Integer l)         { this.limiteUsos = l; }
    public Integer getUsosRealizados()           { return usosRealizados; }
    public void setUsosRealizados(Integer u)     { this.usosRealizados = u; }
    public Integer getClienteId()                { return clienteId; }
    public void setClienteId(Integer c)          { this.clienteId = c; }
    public boolean isAtivo()                     { return ativo; }
    public void setAtivo(boolean a)              { this.ativo = a; }

    public boolean isEsgotado() {
        return limiteUsos != null && usosRealizados >= limiteUsos;
    }

    public boolean podeSerUsadoPor(int clienteIdRequisitante) {
        return clienteId == null || clienteId <= 0 || clienteId == clienteIdRequisitante;
    }

    public boolean isValido() {
        LocalDate hoje = LocalDate.now();
        if (dataInicio == null || validade == null) return false;
        return !hoje.isBefore(dataInicio) && !hoje.isAfter(validade);
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Código: %s | Valor: R$ %.2f | Válido: %s a %s | Descrição: %s",
                id, codigo, valor,
                dataInicio != null ? dataInicio.toString() : "N/A",
                validade != null ? validade.toString() : "N/A",
                descricao != null ? descricao : "Sem descrição");
    }
}