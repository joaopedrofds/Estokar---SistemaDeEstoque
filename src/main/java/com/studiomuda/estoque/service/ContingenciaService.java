package com.studiomuda.estoque.service;
import com.studiomuda.estoque.jpa.entity.*; import com.studiomuda.estoque.jpa.repository.*; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.util.List;
@Service public class ContingenciaService {
 private final TabelaContingenciaJpaRepository repository; private final TransportadoraJpaRepository transportadoras;
 public ContingenciaService(TabelaContingenciaJpaRepository r,TransportadoraJpaRepository t){repository=r;transportadoras=t;}
 @Transactional(readOnly=true) public List<TabelaContingenciaJpaEntity> listar(){return repository.findAllByOrderByCepInicioAscPesoMinimoAsc();}
 @Transactional public TabelaContingenciaJpaEntity salvar(TabelaContingenciaJpaEntity f,Integer transportadoraId){f.setCepInicio(cep(f.getCepInicio()));f.setCepFim(cep(f.getCepFim()));if(f.getCepInicio().compareTo(f.getCepFim())>0)throw new IllegalArgumentException("CEP inicial deve ser menor ou igual ao final.");if(f.getPesoMinimo()==null||f.getPesoMaximo()==null||f.getPesoMinimo().signum()<0||f.getPesoMinimo().compareTo(f.getPesoMaximo())>0)throw new IllegalArgumentException("Faixa de peso invalida.");if(f.getValorFrete()==null||f.getValorFrete().signum()<=0)throw new IllegalArgumentException("Valor do frete deve ser positivo.");if(transportadoraId!=null)f.setTransportadora(transportadoras.findById(transportadoraId).orElseThrow(()->new IllegalArgumentException("Transportadora nao encontrada.")));return repository.save(f);}
 @Transactional public TabelaContingenciaJpaEntity alternarStatus(Integer id){TabelaContingenciaJpaEntity f=buscar(id);f.setAtivo(!Boolean.TRUE.equals(f.getAtivo()));return repository.save(f);}
 @Transactional(readOnly=true) public TabelaContingenciaJpaEntity buscar(Integer id){return repository.findById(id).orElseThrow(()->new IllegalArgumentException("Faixa de contingencia nao encontrada."));}
 private String cep(String v){String c=v==null?"":v.replaceAll("\\D","");if(!c.matches("\\d{8}"))throw new IllegalArgumentException("CEP deve conter 8 digitos.");return c;}
}
