package com.studiomuda.estoque.service;
import com.studiomuda.estoque.jpa.entity.*; import com.studiomuda.estoque.jpa.repository.*; import com.studiomuda.estoque.proxy.AutorizacaoDespachoProxy; import org.junit.jupiter.api.Test; import java.util.Optional; import static org.junit.jupiter.api.Assertions.*; import static org.mockito.Mockito.*;
class DespachoServiceTest {
 @Test void clienteFinanceiramenteBloqueadoNaoGeraOrdem(){
  PedidoJpaRepository pedidos=mock(PedidoJpaRepository.class);PedidoJpaEntity pedido=new PedidoJpaEntity();ClienteJpaEntity cliente=new ClienteJpaEntity();cliente.setAtivo(false);pedido.setCliente(cliente);when(pedidos.findById(8)).thenReturn(Optional.of(pedido));OrdemDespachoJpaRepository ordens=mock(OrdemDespachoJpaRepository.class);
  DespachoService service=new DespachoService(ordens,pedidos,mock(LogCotacaoJpaRepository.class),mock(TransportadoraJpaRepository.class),new AutorizacaoDespachoProxy());IllegalStateException e=assertThrows(IllegalStateException.class,()->service.gerarOrdemDespacho(8,1,1));assertTrue(e.getMessage().contains("bloqueado"));verify(ordens,never()).save(any());
 }
}
