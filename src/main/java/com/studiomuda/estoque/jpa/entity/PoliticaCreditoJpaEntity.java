package com.studiomuda.estoque.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "politica_credito")
public class PoliticaCreditoJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;

    @Column(name = "limite_dias_atraso")
    private Integer limiteDiasAtraso = 45;

    @Column(name = "limite_credito")
    private BigDecimal limiteCredito;

    @Column(name = "data_inicio")
    private LocalDate dataInicio = LocalDate.now();

    @Column(name = "data_fim")
    private LocalDate dataFim;

    private Boolean ativa = true;

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public Integer getLimiteDiasAtraso() { return limiteDiasAtraso; }
    public BigDecimal getLimiteCredito() { return limiteCredito; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public Boolean getAtiva() { return ativa; }

    public void setId(Integer id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setLimiteDiasAtraso(Integer limiteDiasAtraso) { this.limiteDiasAtraso = limiteDiasAtraso; }
    public void setLimiteCredito(BigDecimal limiteCredito) { this.limiteCredito = limiteCredito; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    public void setAtiva(Boolean ativa) { this.ativa = ativa; }
}
