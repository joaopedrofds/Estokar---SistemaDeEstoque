package com.studiomuda.estoque.proxy;
import com.studiomuda.estoque.jpa.entity.TransportadoraJpaEntity; import com.studiomuda.estoque.jpa.repository.TransportadoraJpaRepository; import org.springframework.stereotype.Component; import java.math.*; import java.util.List;
@Component
public class TransportadoraApiClient implements ServicoCotacaoFrete {
 private final TransportadoraJpaRepository repository;
 public TransportadoraApiClient(TransportadoraJpaRepository repository){this.repository=repository;}
 public CotacaoResultado cotar(ParametrosCotacao p,Integer usuarioId){
  List<TransportadoraJpaEntity> ativas=repository.findByAtivoTrueOrderByNomeAsc(); if(ativas.isEmpty()) throw new IllegalStateException("Nenhuma transportadora ativa respondeu a cotacao.");
  // Stub deterministico que representa a API externa enquanto nao ha parceiro contratado.
  BigDecimal volume=p.getComprimento().multiply(p.getLargura()).multiply(p.getAltura()).divide(new BigDecimal("6000"),4,RoundingMode.HALF_UP);
  BigDecimal pesoCubado=p.getPeso().max(volume); BigDecimal valor=new BigDecimal("12.50").add(pesoCubado.multiply(new BigDecimal("2.35"))).setScale(2,RoundingMode.HALF_UP);
  return new CotacaoResultado(valor,"API",ativas.get(0),null);
 }
}
