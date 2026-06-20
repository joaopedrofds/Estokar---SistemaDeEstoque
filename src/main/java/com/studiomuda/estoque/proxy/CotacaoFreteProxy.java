package com.studiomuda.estoque.proxy;

import com.studiomuda.estoque.jpa.entity.*;
import com.studiomuda.estoque.jpa.repository.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class CotacaoFreteProxy implements ServicoCotacaoFrete {
 private final ServicoCotacaoFrete real; private final LogCotacaoJpaRepository logs; private final TabelaContingenciaJpaRepository contingencias; private final ClienteJpaRepository clientes; private final PedidoJpaRepository pedidos;
 public CotacaoFreteProxy(TransportadoraApiClient real,LogCotacaoJpaRepository logs,TabelaContingenciaJpaRepository contingencias,ClienteJpaRepository clientes,PedidoJpaRepository pedidos){this.real=real;this.logs=logs;this.contingencias=contingencias;this.clientes=clientes;this.pedidos=pedidos;}
 @Override public CotacaoResultado cotar(ParametrosCotacao p,Integer usuarioId){
  validar(p,usuarioId); LocalDateTime agora=LocalDateTime.now();
  if(logs.contarCotacoesExternas(usuarioId,agora.minusHours(1))>=50) throw new LimiteCotacaoExcedidoException();
  p.setCepDestino(p.getCepDestino().replaceAll("\\D","")); String hash=HashCotacao.gerar(p.getCepDestino(),p.getPeso(),p.getComprimento(),p.getLargura(),p.getAltura());
  List<LogCotacaoJpaEntity> cache=logs.buscarCacheRecente(hash,agora.minusMinutes(30));
  if(!cache.isEmpty()){LogCotacaoJpaEntity l=cache.get(0);return new CotacaoResultado(l.getValorCotado(),"CACHE",l.getTransportadora(),l.getId());}
  CotacaoResultado resultado;
  try{resultado=real.cotar(p,usuarioId);}catch(RuntimeException falha){
   List<TabelaContingenciaJpaEntity> faixas=contingencias.buscarAplicaveis(p.getCepDestino(),p.getPeso());
   if(faixas.isEmpty()) throw new IllegalStateException("Transportadora indisponivel e nenhuma faixa de contingencia atende ao CEP/peso informado.");
   TabelaContingenciaJpaEntity faixa=faixas.get(0); resultado=new CotacaoResultado(faixa.getValorFrete(),"CONTINGENCIA",faixa.getTransportadora(),null);
  }
  LogCotacaoJpaEntity log=new LogCotacaoJpaEntity(); log.setUsuarioId(usuarioId); log.setCepDestino(p.getCepDestino()); log.setPeso(p.getPeso()); log.setComprimento(p.getComprimento()); log.setLargura(p.getLargura()); log.setAltura(p.getAltura()); log.setHashParametros(hash); log.setValorCotado(resultado.getValor()); log.setOrigemResultado(resultado.getOrigem()); log.setTransportadora(resultado.getTransportadora()); log.setDataCotacao(agora);
  if(p.getClienteId()!=null) log.setCliente(clientes.findById(p.getClienteId()).orElseThrow(()->new IllegalArgumentException("Cliente nao encontrado.")));
  if(p.getPedidoId()!=null) log.setPedido(pedidos.findById(p.getPedidoId()).orElseThrow(()->new IllegalArgumentException("Pedido nao encontrado.")));
  log=logs.save(log); return new CotacaoResultado(resultado.getValor(),resultado.getOrigem(),resultado.getTransportadora(),log.getId());
 }
 private void validar(ParametrosCotacao p,Integer usuarioId){
  if(usuarioId==null) throw new IllegalArgumentException("Usuario solicitante nao identificado."); if(p==null||p.getCepDestino()==null||!p.getCepDestino().replaceAll("\\D","").matches("\\d{8}")) throw new IllegalArgumentException("Informe um CEP valido com 8 digitos.");
  if(naoPositivo(p.getPeso())||naoPositivo(p.getComprimento())||naoPositivo(p.getLargura())||naoPositivo(p.getAltura())) throw new IllegalArgumentException("Peso e dimensoes devem ser maiores que zero.");
 }
 private boolean naoPositivo(BigDecimal v){return v==null||v.signum()<=0;}
}
