package com.studiomuda.estoque.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
public class PedidoJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "data_requisicao")
    private Date dataRequisicao;

    @Column(name = "data_entrega")
    private Date dataEntrega;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id")
    private ClienteJpaEntity cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "beneficio_categoria_id")
    private BeneficioCategoriaJpaEntity beneficioAplicado;

    @Column(name = "funcionario_id")
    private Integer funcionarioId;

    @Column(name = "cupom_id")
    private Integer cupomId;

    @Column(name = "valor_desconto")
    private Double valorDesconto;

    @Column(name = "status")
    private String status;

    @Column(name = "status_pagamento")
    private String statusPagamento;

    @Column(name = "data_pagamento")
    private Date dataPagamento;

    @Column(name = "cancelamento_solicitante_id")
    private Integer cancelamentoSolicitanteId;

    @Column(name = "cancelamento_solicitante_nome")
    private String cancelamentoSolicitanteNome;

    @Column(name = "justificativa_cancelamento")
    private String justificativaCancelamento;

    @Column(name = "data_cancelamento")
    private Timestamp dataCancelamento;

    @Column(name = "cancelamento_aprovador_id")
    private Integer cancelamentoAprovadorId;

    @Column(name = "cancelamento_aprovador_nome")
    private String cancelamentoAprovadorNome;

    @Column(name = "data_aprovacao_cancelamento")
    private Timestamp dataAprovacaoCancelamento;

    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY)
    private List<ItemPedidoJpaEntity> itens = new ArrayList<>();

    public Integer getId() { return id; }
    public Date getDataRequisicao() { return dataRequisicao; }
    public Date getDataEntrega() { return dataEntrega; }
    public Integer getClienteId() { return cliente != null ? cliente.getId() : null; }
    public ClienteJpaEntity getCliente() { return cliente; }
    public BeneficioCategoriaJpaEntity getBeneficioAplicado() { return beneficioAplicado; }
    public Integer getFuncionarioId() { return funcionarioId; }
    public Integer getCupomId() { return cupomId; }
    public Double getValorDesconto() { return valorDesconto; }
    public String getStatus() { return status; }
    public String getStatusPagamento() { return statusPagamento; }
    public Date getDataPagamento() { return dataPagamento; }
    public Integer getCancelamentoSolicitanteId() { return cancelamentoSolicitanteId; }
    public String getCancelamentoSolicitanteNome() { return cancelamentoSolicitanteNome; }
    public String getJustificativaCancelamento() { return justificativaCancelamento; }
    public Timestamp getDataCancelamento() { return dataCancelamento; }
    public Integer getCancelamentoAprovadorId() { return cancelamentoAprovadorId; }
    public String getCancelamentoAprovadorNome() { return cancelamentoAprovadorNome; }
    public Timestamp getDataAprovacaoCancelamento() { return dataAprovacaoCancelamento; }
    public List<ItemPedidoJpaEntity> getItens() { return itens; }

    public void setCliente(ClienteJpaEntity cliente) { this.cliente = cliente; }
    public void setBeneficioAplicado(BeneficioCategoriaJpaEntity beneficioAplicado) { this.beneficioAplicado = beneficioAplicado; }
    public void setValorDesconto(Double valorDesconto) { this.valorDesconto = valorDesconto; }
    public void setStatus(String status) { this.status = status; }
    public void setCancelamentoSolicitanteId(Integer cancelamentoSolicitanteId) { this.cancelamentoSolicitanteId = cancelamentoSolicitanteId; }
    public void setCancelamentoSolicitanteNome(String cancelamentoSolicitanteNome) { this.cancelamentoSolicitanteNome = cancelamentoSolicitanteNome; }
    public void setJustificativaCancelamento(String justificativaCancelamento) { this.justificativaCancelamento = justificativaCancelamento; }
    public void setDataCancelamento(Timestamp dataCancelamento) { this.dataCancelamento = dataCancelamento; }
    public void setCancelamentoAprovadorId(Integer cancelamentoAprovadorId) { this.cancelamentoAprovadorId = cancelamentoAprovadorId; }
    public void setCancelamentoAprovadorNome(String cancelamentoAprovadorNome) { this.cancelamentoAprovadorNome = cancelamentoAprovadorNome; }
    public void setDataAprovacaoCancelamento(Timestamp dataAprovacaoCancelamento) { this.dataAprovacaoCancelamento = dataAprovacaoCancelamento; }
}
