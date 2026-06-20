package com.studiomuda.estoque.service;
import com.studiomuda.estoque.jpa.entity.TransportadoraJpaEntity; import com.studiomuda.estoque.jpa.repository.TransportadoraJpaRepository; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.util.List;
@Service public class TransportadoraService {
 private final TransportadoraJpaRepository repository; public TransportadoraService(TransportadoraJpaRepository r){repository=r;}
 @Transactional(readOnly=true) public List<TransportadoraJpaEntity> listar(){return repository.findAllByOrderByNomeAsc();}
 @Transactional(readOnly=true) public List<TransportadoraJpaEntity> listarAtivas(){return repository.findByAtivoTrueOrderByNomeAsc();}
 @Transactional public TransportadoraJpaEntity salvar(TransportadoraJpaEntity t){if(t.getNome()==null||t.getNome().trim().isEmpty())throw new IllegalArgumentException("Informe o nome da transportadora.");if(t.getPrazoMedioDias()==null||t.getPrazoMedioDias()<1)throw new IllegalArgumentException("Prazo medio deve ser positivo.");return repository.save(t);}
 @Transactional public TransportadoraJpaEntity alternarStatus(Integer id){TransportadoraJpaEntity t=buscar(id);t.setAtivo(!Boolean.TRUE.equals(t.getAtivo()));return repository.save(t);}
 @Transactional(readOnly=true) public TransportadoraJpaEntity buscar(Integer id){return repository.findById(id).orElseThrow(()->new IllegalArgumentException("Transportadora nao encontrada."));}
}
