package com.studiomuda.estoque.jpa.entity;
import javax.persistence.*;
@Entity @Table(name="transportadora")
public class TransportadoraJpaEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
 @Column(nullable=false) private String nome; @Column(nullable=false) private Boolean ativo=true;
 @Column(name="prazo_medio_dias",nullable=false) private Integer prazoMedioDias=5; private String observacoes;
 public Integer getId(){return id;} public void setId(Integer v){id=v;} public String getNome(){return nome;} public void setNome(String v){nome=v;}
 public Boolean getAtivo(){return ativo;} public void setAtivo(Boolean v){ativo=v;} public Integer getPrazoMedioDias(){return prazoMedioDias;} public void setPrazoMedioDias(Integer v){prazoMedioDias=v;}
 public String getObservacoes(){return observacoes;} public void setObservacoes(String v){observacoes=v;}
}
