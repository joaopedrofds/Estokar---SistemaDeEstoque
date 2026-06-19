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
import java.sql.Timestamp;

@Entity
@Table(name = "contagem_item")
public class ContagemItemJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sessao_id")
    private Integer sessaoId;

    @Column(name = "produto_id")
    private Integer produtoId;

    @Column(name = "quantidade_fisica")
    private Integer quantidadeFisica;

    @Column(name = "auxiliar_id")
    private Integer auxiliarId;

    @Column(name = "auxiliar_nome")
    private String auxiliarNome;

    @Column(name = "data_contagem")
    private Timestamp dataContagem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", insertable = false, updatable = false)
    private ProdutoJpaEntity produto;

    public Integer getId() { return id; }
    public Integer getSessaoId() { return sessaoId; }
    public Integer getProdutoId() { return produtoId; }
    public Integer getQuantidadeFisica() { return quantidadeFisica; }
    public Integer getAuxiliarId() { return auxiliarId; }
    public String getAuxiliarNome() { return auxiliarNome; }
    public Timestamp getDataContagem() { return dataContagem; }
    public ProdutoJpaEntity getProduto() { return produto; }

    public void setSessaoId(Integer sessaoId) { this.sessaoId = sessaoId; }
    public void setProdutoId(Integer produtoId) { this.produtoId = produtoId; }
    public void setQuantidadeFisica(Integer quantidadeFisica) { this.quantidadeFisica = quantidadeFisica; }
    public void setAuxiliarId(Integer auxiliarId) { this.auxiliarId = auxiliarId; }
    public void setAuxiliarNome(String auxiliarNome) { this.auxiliarNome = auxiliarNome; }
    public void setDataContagem(Timestamp dataContagem) { this.dataContagem = dataContagem; }
}
