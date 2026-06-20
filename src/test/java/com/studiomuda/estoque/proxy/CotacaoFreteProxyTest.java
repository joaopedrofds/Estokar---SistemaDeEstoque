package com.studiomuda.estoque.proxy;
import com.studiomuda.estoque.jpa.entity.*; import com.studiomuda.estoque.jpa.repository.*;
import org.junit.jupiter.api.Test; import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.*; import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*; import static org.mockito.ArgumentMatchers.*; import static org.mockito.Mockito.*;
class CotacaoFreteProxyTest {
 @Test void segundaCotacaoIdenticaUsaCacheSemNovaChamadaExterna(){
  TransportadoraApiClient api=mock(TransportadoraApiClient.class); LogCotacaoJpaRepository logs=mock(LogCotacaoJpaRepository.class); TabelaContingenciaJpaRepository tab=mock(TabelaContingenciaJpaRepository.class);
  TransportadoraJpaEntity t=new TransportadoraJpaEntity();t.setId(1);t.setNome("Teste"); when(api.cotar(any(),eq(7))).thenReturn(new CotacaoResultado(new BigDecimal("25.00"),"API",t,null)); when(logs.contarCotacoesExternas(eq(7),any())).thenReturn(0L);
  AtomicReference<LogCotacaoJpaEntity> salvo=new AtomicReference<>(); when(logs.buscarCacheRecente(anyString(),any())).thenAnswer(i->salvo.get()==null?Collections.emptyList():Collections.singletonList(salvo.get())); when(logs.save(any())).thenAnswer(i->{LogCotacaoJpaEntity l=i.getArgument(0);l.setId(10);salvo.set(l);return l;});
  CotacaoFreteProxy proxy=new CotacaoFreteProxy(api,logs,tab,mock(ClienteJpaRepository.class),mock(PedidoJpaRepository.class)); ParametrosCotacao p=parametros();
  assertEquals("API",proxy.cotar(p,7).getOrigem()); assertEquals("CACHE",proxy.cotar(p,7).getOrigem()); verify(api,times(1)).cotar(any(),eq(7)); verify(logs,times(1)).save(any());
 }
 @Test void falhaDaApiRetornaFaixaDeContingencia(){
  TransportadoraApiClient api=mock(TransportadoraApiClient.class); LogCotacaoJpaRepository logs=mock(LogCotacaoJpaRepository.class); TabelaContingenciaJpaRepository tab=mock(TabelaContingenciaJpaRepository.class); when(logs.contarCotacoesExternas(anyInt(),any())).thenReturn(0L);when(logs.buscarCacheRecente(anyString(),any())).thenReturn(Collections.emptyList());when(api.cotar(any(),anyInt())).thenThrow(new IllegalStateException("timeout"));
  TabelaContingenciaJpaEntity faixa=new TabelaContingenciaJpaEntity();faixa.setValorFrete(new BigDecimal("39.90"));when(tab.buscarAplicaveis(eq("60000000"),any())).thenReturn(Collections.singletonList(faixa));when(logs.save(any())).thenAnswer(i->{LogCotacaoJpaEntity l=i.getArgument(0);l.setId(3);return l;});
  CotacaoResultado r=new CotacaoFreteProxy(api,logs,tab,mock(ClienteJpaRepository.class),mock(PedidoJpaRepository.class)).cotar(parametros(),2);assertEquals("CONTINGENCIA",r.getOrigem());assertEquals(new BigDecimal("39.90"),r.getValor());
 }
 @Test void quinquagesimaPrimeiraCotacaoExternaEhBloqueada(){
  LogCotacaoJpaRepository logs=mock(LogCotacaoJpaRepository.class);when(logs.contarCotacoesExternas(eq(4),any(LocalDateTime.class))).thenReturn(50L);TransportadoraApiClient api=mock(TransportadoraApiClient.class);
  CotacaoFreteProxy proxy=new CotacaoFreteProxy(api,logs,mock(TabelaContingenciaJpaRepository.class),mock(ClienteJpaRepository.class),mock(PedidoJpaRepository.class));assertThrows(LimiteCotacaoExcedidoException.class,()->proxy.cotar(parametros(),4));verifyNoInteractions(api);
 }
 private ParametrosCotacao parametros(){ParametrosCotacao p=new ParametrosCotacao();p.setCepDestino("60000-000");p.setPeso(new BigDecimal("2"));p.setComprimento(new BigDecimal("20"));p.setLargura(new BigDecimal("15"));p.setAltura(new BigDecimal("10"));return p;}
}
