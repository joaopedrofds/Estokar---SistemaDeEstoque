package com.studiomuda.estoque.observer;

import com.studiomuda.estoque.model.Cupom;
import com.studiomuda.estoque.model.CupomUso;
import com.studiomuda.estoque.repository.CupomRepository;
import com.studiomuda.estoque.repository.CupomUsoRepository;
import com.studiomuda.estoque.util.SpringContextUtil;
import java.time.LocalDateTime;

/**
 * Observer que registra o uso do cupom e incrementa o contador de usos.
 * Padrão de Design: Observer (GoF) — ConcreteObserver
 * Substitui o antigo CupomUsoDAO por JPA Repository.
 */
public class CupomUsoObserver implements ObservadorDeCupom {

    @Override
    public void aoAplicarCupom(CupomDomainEvent event) {
        CupomUso uso = new CupomUso();
        uso.setCupomId(event.getCupomId());
        uso.setPedidoId(event.getPedidoId());
        uso.setClienteId(event.getClienteId());
        uso.setValorDesconto(event.getValorDesconto());
        uso.setDataUso(LocalDateTime.now());

        // Usar JPA Repository via SpringContextUtil (observer não é bean Spring)
        CupomUsoRepository usoRepo = SpringContextUtil.getBean(CupomUsoRepository.class);
        usoRepo.save(uso);

        // Incrementar contador de usos no cupom
        CupomRepository cupomRepo = SpringContextUtil.getBean(CupomRepository.class);
        cupomRepo.findById(event.getCupomId()).ifPresent(cupom -> {
            cupom.setUsosRealizados(cupom.getUsosRealizados() + 1);
            cupomRepo.save(cupom);
        });
    }
}