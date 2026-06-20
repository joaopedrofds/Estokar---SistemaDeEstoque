package com.studiomuda.estoque.proxy;
import com.studiomuda.estoque.jpa.entity.ClienteJpaEntity; import org.springframework.stereotype.Component; import java.util.Locale;
@Component public class AutorizacaoDespachoProxy {
 public void validar(ClienteJpaEntity cliente){
  if(cliente==null) throw new IllegalStateException("Pedido sem cliente vinculado.");
  if(Boolean.FALSE.equals(cliente.getAtivo())) throw new IllegalStateException("Despacho bloqueado: cliente inadimplente ou inativo.");
  String faixa=cliente.getFaixaFidelidade()==null?"":cliente.getFaixaFidelidade().getNome().toUpperCase(Locale.ROOT);
  if("INATIVO".equals(faixa)) throw new IllegalStateException("Despacho bloqueado: cliente esta na faixa Inativo.");
 }
}
