package com.studiomuda.estoque.proxy;
import com.studiomuda.estoque.jpa.entity.TransportadoraJpaEntity; import java.math.BigDecimal;
public class CotacaoResultado {
 private final BigDecimal valor; private final String origem; private final TransportadoraJpaEntity transportadora; private final Integer logCotacaoId;
 public CotacaoResultado(BigDecimal valor,String origem,TransportadoraJpaEntity transportadora,Integer logCotacaoId){this.valor=valor;this.origem=origem;this.transportadora=transportadora;this.logCotacaoId=logCotacaoId;}
 public BigDecimal getValor(){return valor;} public String getOrigem(){return origem;} public TransportadoraJpaEntity getTransportadora(){return transportadora;} public Integer getLogCotacaoId(){return logCotacaoId;}
}
