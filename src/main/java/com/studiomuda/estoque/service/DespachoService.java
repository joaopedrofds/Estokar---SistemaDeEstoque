package com.studiomuda.estoque.service;
import com.studiomuda.estoque.jpa.entity.*; import com.studiomuda.estoque.jpa.repository.*; import com.studiomuda.estoque.proxy.AutorizacaoDespachoProxy; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.time.LocalDateTime; import java.util.List;
@Service public class DespachoService {
 private final OrdemDespachoJpaRepository ordens; private final PedidoJpaRepository pedidos; private final LogCotacaoJpaRepository logs; private final TransportadoraJpaRepository transportadoras; private final AutorizacaoDespachoProxy autorizacao;
 public DespachoService(OrdemDespachoJpaRepository o,PedidoJpaRepository p,LogCotacaoJpaRepository l,TransportadoraJpaRepository t,AutorizacaoDespachoProxy a){ordens=o;pedidos=p;logs=l;transportadoras=t;autorizacao=a;}
 @Transactional public OrdemDespachoJpaEntity gerarOrdemDespacho(Integer pedidoId,Integer logId,Integer transportadoraId){
  PedidoJpaEntity pedido=pedidos.findById(pedidoId).orElseThrow(()->new IllegalArgumentException("Pedido nao encontrado.")); autorizacao.validar(pedido.getCliente()); if(ordens.existsByPedidoId(pedidoId))throw new IllegalStateException("Ja existe uma ordem de despacho para este pedido.");
  LogCotacaoJpaEntity log=logs.findById(logId).orElseThrow(()->new IllegalArgumentException("Cotacao nao encontrada.")); if(log.getPedido()!=null&&!pedidoId.equals(log.getPedido().getId()))throw new IllegalStateException("A cotacao selecionada pertence a outro pedido.");
  TransportadoraJpaEntity t=transportadoras.findById(transportadoraId).orElseThrow(()->new IllegalArgumentException("Transportadora nao encontrada.")); OrdemDespachoJpaEntity o=new OrdemDespachoJpaEntity();o.setPedido(pedido);o.setLogCotacao(log);o.setTransportadora(t);o.setValorFrete(log.getValorCotado());o.setCodigoRastreio("EST-"+pedidoId+"-"+System.currentTimeMillis());o.setDataDespacho(LocalDateTime.now());o.setDataEntregaPrevista(LocalDateTime.now().plusDays(t.getPrazoMedioDias()));return ordens.save(o);
 }
 @Transactional(readOnly=true) public List<OrdemDespachoJpaEntity> listar(){return ordens.findAllByOrderByIdDesc();}
}
