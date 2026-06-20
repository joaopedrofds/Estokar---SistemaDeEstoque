package com.studiomuda.estoque.jpa.entity;
import javax.persistence.*; import java.math.BigDecimal;
@Entity @Table(name="tabela_contingencia")
public class TabelaContingenciaJpaEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
 @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="transportadora_id") private TransportadoraJpaEntity transportadora;
 @Column(name="cep_inicio",nullable=false,length=8) private String cepInicio; @Column(name="cep_fim",nullable=false,length=8) private String cepFim;
 @Column(name="peso_minimo",nullable=false) private BigDecimal pesoMinimo; @Column(name="peso_maximo",nullable=false) private BigDecimal pesoMaximo;
 @Column(name="valor_frete",nullable=false) private BigDecimal valorFrete; @Column(nullable=false) private Boolean ativo=true;
 public Integer getId(){return id;} public void setId(Integer v){id=v;} public TransportadoraJpaEntity getTransportadora(){return transportadora;} public void setTransportadora(TransportadoraJpaEntity v){transportadora=v;}
 public String getCepInicio(){return cepInicio;} public void setCepInicio(String v){cepInicio=v;} public String getCepFim(){return cepFim;} public void setCepFim(String v){cepFim=v;}
 public BigDecimal getPesoMinimo(){return pesoMinimo;} public void setPesoMinimo(BigDecimal v){pesoMinimo=v;} public BigDecimal getPesoMaximo(){return pesoMaximo;} public void setPesoMaximo(BigDecimal v){pesoMaximo=v;}
 public BigDecimal getValorFrete(){return valorFrete;} public void setValorFrete(BigDecimal v){valorFrete=v;} public Boolean getAtivo(){return ativo;} public void setAtivo(Boolean v){ativo=v;}
}
