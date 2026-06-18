package com.studiomuda.estoque.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_cobranca")
public class HistoricoCobrancaJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id")
    private ClienteJpaEntity cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fatura_id")
    private FaturaJpaEntity fatura;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "registro_original_id")
    private HistoricoCobrancaJpaEntity registroOriginal;

    @Column(name = "data_contato")
    private LocalDateTime dataContato = LocalDateTime.now();

    @Column(name = "tipo_contato")
    private String tipoContato;

    private String responsavel;

    @Column(length = 1000)
    private String descricao;

    @PreUpdate
    @PreRemove
    private void bloquearAlteracao() {
        throw new IllegalStateException("Historico de cobranca e imutavel; crie um novo registro de correcao.");
    }

    public Integer getId() { return id; }
    public ClienteJpaEntity getCliente() { return cliente; }
    public FaturaJpaEntity getFatura() { return fatura; }
    public HistoricoCobrancaJpaEntity getRegistroOriginal() { return registroOriginal; }
    public LocalDateTime getDataContato() { return dataContato; }
    public String getTipoContato() { return tipoContato; }
    public String getResponsavel() { return responsavel; }
    public String getDescricao() { return descricao; }

    public void setId(Integer id) { this.id = id; }
    public void setCliente(ClienteJpaEntity cliente) { this.cliente = cliente; }
    public void setFatura(FaturaJpaEntity fatura) { this.fatura = fatura; }
    public void setRegistroOriginal(HistoricoCobrancaJpaEntity registroOriginal) { this.registroOriginal = registroOriginal; }
    public void setDataContato(LocalDateTime dataContato) { this.dataContato = dataContato; }
    public void setTipoContato(String tipoContato) { this.tipoContato = tipoContato; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
