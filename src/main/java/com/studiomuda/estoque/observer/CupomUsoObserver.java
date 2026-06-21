package com.studiomuda.estoque.observer;

import com.studiomuda.estoque.model.Cupom;
import com.studiomuda.estoque.model.CupomUso;
import com.studiomuda.estoque.repository.CupomRepository;
import com.studiomuda.estoque.repository.CupomUsoRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Observer que registra o uso do cupom e incrementa o contador de usos.
 * Padrão de Design: Observer (GoF) — ConcreteObserver
 * Substitui o antigo CupomUsoDAO por JPA Repository.
 */
@Component
public class CupomUsoObserver implements ObservadorDeCupom {

    private final CupomUsoRepository usoRepo;
    private final CupomRepository cupomRepo;

    public CupomUsoObserver(CupomUsoRepository usoRepo, CupomRepository cupomRepo) {
        this.usoRepo = usoRepo;
        this.cupomRepo = cupomRepo;
    }

    @Override
    public void aoAplicarCupom(CupomDomainEvent event) {
        CupomUso uso = new CupomUso();
        uso.setCupomId(event.getCupomId());
        uso.setPedidoId(event.getPedidoId());
        uso.setClienteId(event.getClienteId());
        uso.setValorDesconto(event.getValorDesconto());
        uso.setDataUso(LocalDateTime.now());

        usoRepo.save(uso);

        // Incrementar contador de usos no cupom
        cupomRepo.findById(event.getCupomId()).ifPresent(cupom -> {
            cupom.setUsosRealizados(cupom.getUsosRealizados() + 1);
            cupomRepo.save(cupom);
        });
    }
}
