package com.studiomuda.estoque.strategy;

import com.studiomuda.estoque.model.CreditoCliente;
import com.studiomuda.estoque.model.Devolucao;
import com.studiomuda.estoque.model.ItemDevolucao;
import com.studiomuda.estoque.repository.CreditoClienteRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CreditoLojaStrategyTest {

    @Test
    void geraCreditoAtivoComSaldoEValidade() {
        CreditoClienteRepository repository = mock(CreditoClienteRepository.class);
        CreditoLojaStrategy strategy = new CreditoLojaStrategy(repository);

        ItemDevolucao item = new ItemDevolucao();
        item.setQuantidade(2);
        item.setValorUnitario(35.0);

        Devolucao devolucao = new Devolucao();
        devolucao.setId(9);
        devolucao.setClienteId(4);
        devolucao.setItens(Collections.singletonList(item));

        strategy.executar(devolucao);

        ArgumentCaptor<CreditoCliente> captor = ArgumentCaptor.forClass(CreditoCliente.class);
        verify(repository).save(captor.capture());
        CreditoCliente credito = captor.getValue();
        assertEquals(70.0, credito.getValor(), 0.001);
        assertEquals(70.0, credito.getSaldo(), 0.001);
        assertEquals("ATIVO", credito.getStatus());
        assertNotNull(credito.getValidade());
    }
}
