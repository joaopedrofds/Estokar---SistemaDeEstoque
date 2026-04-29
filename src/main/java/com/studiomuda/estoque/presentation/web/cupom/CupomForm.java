package com.studiomuda.estoque.presentation.web.cupom;

import com.studiomuda.estoque.application.cupom.dto.SalvarCupomCommand;
import com.studiomuda.estoque.domain.cupom.Cupom;

import java.time.LocalDate;

public class CupomForm {
    private int id;
    private String codigo;
    private String descricao;
    private double valor;
    private LocalDate dataInicio;
    private LocalDate validade;
    private String condicoesUso;

    public CupomForm() {}

    public static CupomForm desde(Cupom c) {
        CupomForm f = new CupomForm();
        f.id = c.id();
        f.codigo = c.codigo();
        f.descricao = c.descricao();
        f.valor = c.valor();
        f.dataInicio = c.dataInicio();
        f.validade = c.validade();
        f.condicoesUso = c.condicoesUso();
        return f;
    }

    public SalvarCupomCommand toCommand(LocalDate dataInicioOverride, LocalDate validadeOverride) {
        return new SalvarCupomCommand(id, codigo, descricao, valor,
                dataInicioOverride != null ? dataInicioOverride : dataInicio,
                validadeOverride != null ? validadeOverride : validade,
                condicoesUso);
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
}
