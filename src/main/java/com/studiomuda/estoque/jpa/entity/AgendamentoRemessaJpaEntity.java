package com.studiomuda.estoque.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "agendamento_remessa")
public class AgendamentoRemessaJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo_agendamento")
    private String codigoAgendamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doca_id")
    private DocaJpaEntity doca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "distribuidora_id")
    private DistribuidoraJpaEntity distribuidora;

    @Column(name = "data")
    private Date data;

    @Column(name = "horario")
    private String horario;

    @Column(name = "volume_paletes")
    private Integer volumePaletes;

    @Column(name = "status")
    private String status;

    public Integer getId() {
        return id;
    }

    public String getCodigoAgendamento() {
        return codigoAgendamento;
    }

    public DocaJpaEntity getDoca() {
        return doca;
    }

    public DistribuidoraJpaEntity getDistribuidora() {
        return distribuidora;
    }

    public Date getData() {
        return data;
    }

    public String getHorario() {
        return horario;
    }

    public Integer getVolumePaletes() {
        return volumePaletes;
    }

    public String getStatus() {
        return status;
    }

    public void setCodigoAgendamento(String codigoAgendamento) {
        this.codigoAgendamento = codigoAgendamento;
    }

    public void setDoca(DocaJpaEntity doca) {
        this.doca = doca;
    }

    public void setDistribuidora(DistribuidoraJpaEntity distribuidora) {
        this.distribuidora = distribuidora;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public void setVolumePaletes(Integer volumePaletes) {
        this.volumePaletes = volumePaletes;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
