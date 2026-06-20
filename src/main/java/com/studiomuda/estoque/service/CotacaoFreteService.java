package com.studiomuda.estoque.service;
import com.studiomuda.estoque.jpa.entity.LogCotacaoJpaEntity; import com.studiomuda.estoque.jpa.repository.LogCotacaoJpaRepository; import com.studiomuda.estoque.proxy.*; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.util.List;
@Service public class CotacaoFreteService {
 private final CotacaoFreteProxy proxy; private final LogCotacaoJpaRepository repository;
 public CotacaoFreteService(CotacaoFreteProxy proxy,LogCotacaoJpaRepository repository){this.proxy=proxy;this.repository=repository;}
 @Transactional public CotacaoResultado cotar(ParametrosCotacao parametros,Integer usuarioId){return proxy.cotar(parametros,usuarioId);}
 @Transactional(readOnly=true) public List<LogCotacaoJpaEntity> listarHistorico(){return repository.findAllByOrderByDataCotacaoDesc();}
}
