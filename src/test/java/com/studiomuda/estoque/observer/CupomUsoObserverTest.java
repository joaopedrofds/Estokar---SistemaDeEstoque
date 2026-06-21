package com.studiomuda.estoque.observer;

import com.studiomuda.estoque.model.Cupom;
import com.studiomuda.estoque.model.CupomUso;
import com.studiomuda.estoque.repository.CupomRepository;
import com.studiomuda.estoque.repository.CupomUsoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CupomUsoObserverTest {

    @Test
    void registraUsoEIncrementaContadorDoCupom() {
        CupomUsoRepository usoRepository = mock(CupomUsoRepository.class);
        CupomRepository cupomRepository = mock(CupomRepository.class);
        Cupom cupom = new Cupom();
        cupom.setId(2);
        cupom.setUsosRealizados(3);
        when(cupomRepository.findById(2)).thenReturn(Optional.of(cupom));

        CupomUsoObserver observer = new CupomUsoObserver(usoRepository, cupomRepository);
        observer.aoAplicarCupom(new CupomDomainEvent(2, 7, 4, 15.0));

        ArgumentCaptor<CupomUso> captor = ArgumentCaptor.forClass(CupomUso.class);
        verify(usoRepository).save(captor.capture());
        assertEquals(7, captor.getValue().getPedidoId());
        assertEquals(15.0, captor.getValue().getValorDesconto(), 0.001);
        assertEquals(4, cupom.getUsosRealizados());
        verify(cupomRepository).save(cupom);
    }
}
