package com.studiomuda.estoque.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "meta_indicador")
public class MetaIndicador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "indicador_id")
    private int indicadorId;

    @Column(name = "valor_alvo")
    private double valorAlvo;

    @Column(name = "limite_critico")
    private double limiteCritico;

    @Column(name = "operador")
    private String operador; // MAIOR_IGUAL, MENOR_IGUAL

    @Column(name = "vigencia_inicio")
    private LocalDate vigenciaInicio;

    @Column(name = "vigencia_fim")
    private LocalDate vigenciaFim;

    @Column(name = "ativo")
    private boolean ativo;

    // Nome do indicador para facilitar exibições na interface (não persistido)
    @Transient
    private String indicadorNome;
    @Transient
    private String indicadorCodigo;

    public MetaIndicador() {}

    public MetaIndicador(int id, int indicadorId, double valorAlvo, double limiteCritico, String operador, LocalDate vigenciaInicio, LocalDate vigenciaFim, boolean ativo) {
        this.id = id;
        this.indicadorId = indicadorId;
        this.valorAlvo = valorAlvo;
        this.limiteCritico = limiteCritico;
        this.operador = operador;
        this.vigenciaInicio = vigenciaInicio;
        this.vigenciaFim = vigenciaFim;
        this.ativo = ativo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIndicadorId() { return indicadorId; }
    public void setIndicadorId(int indicadorId) { this.indicadorId = indicadorId; }

    public double getValorAlvo() { return valorAlvo; }
    public void setValorAlvo(double valorAlvo) { this.valorAlvo = valorAlvo; }

    public double getLimiteCritico() { return limiteCritico; }
    public void setLimiteCritico(double limiteCritico) { this.limiteCritico = limiteCritico; }

    public String getOperador() { return operador; }
    public void setOperador(String operador) { this.operador = operador; }

    public LocalDate getVigenciaInicio() { return vigenciaInicio; }
    public void setVigenciaInicio(LocalDate vigenciaInicio) { this.vigenciaInicio = vigenciaInicio; }

    public LocalDate getVigenciaFim() { return vigenciaFim; }
    public void setVigenciaFim(LocalDate vigenciaFim) { this.vigenciaFim = vigenciaFim; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public String getIndicadorNome() { return indicadorNome; }
    public void setIndicadorNome(String indicadorNome) { this.indicadorNome = indicadorNome; }

    public String getIndicadorCodigo() { return indicadorCodigo; }
    public void setIndicadorCodigo(String indicadorCodigo) { this.indicadorCodigo = indicadorCodigo; }

    public boolean isViolada(double valorCalculado) {
        if ("MAIOR_IGUAL".equals(operador)) {
            return valorCalculado < valorAlvo;
        } else if ("MENOR_IGUAL".equals(operador)) {
            return valorCalculado > valorAlvo;
        }
        return false;
    }

    public boolean isCritico(double valorCalculado) {
        if ("MAIOR_IGUAL".equals(operador)) {
            return valorCalculado < limiteCritico;
        } else if ("MENOR_IGUAL".equals(operador)) {
            return valorCalculado > limiteCritico;
        }
        return false;
    }
}
