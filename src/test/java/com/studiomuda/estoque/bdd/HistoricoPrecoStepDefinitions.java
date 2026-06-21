package com.studiomuda.estoque.bdd;

import com.studiomuda.estoque.model.HistoricoPreco;
import com.studiomuda.estoque.observer.HistoricoPrecosObserver;
import com.studiomuda.estoque.observer.PrecoDomainEvent;
import com.studiomuda.estoque.repository.HistoricoPrecoRepository;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class HistoricoPrecoStepDefinitions {

    private final HistoricoPrecoRepository repository = mock(HistoricoPrecoRepository.class);
    private final HistoricoPrecosObserver observer = new HistoricoPrecosObserver(repository);
    private PrecoDomainEvent evento;
    private HistoricoPreco historico;

    @Dado("um evento de preco de {string} para {string}")
    public void umEventoDePrecoDePara(String anterior, String novo) {
        evento = new PrecoDomainEvent(
                1, "Produto BDD", Double.parseDouble(anterior), Double.parseDouble(novo), "gestor");
    }

    @Quando("o observer de historico processa o evento")
    public void oObserverDeHistoricoProcessaOEvento() {
        observer.aoAlterarPreco(evento);
    }

    @Entao("um historico deve ser persistido")
    public void umHistoricoDeveSerPersistido() {
        ArgumentCaptor<HistoricoPreco> captor = ArgumentCaptor.forClass(HistoricoPreco.class);
        verify(repository).save(captor.capture());
        historico = captor.getValue();
    }

    @Entao("nenhum historico deve ser persistido")
    public void nenhumHistoricoDeveSerPersistido() {
        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @E("a variacao registrada deve ser {string} por cento")
    public void aVariacaoRegistradaDeveSerPorCento(String percentual) {
        assertEquals(Double.parseDouble(percentual), historico.getPercentualVariacao(), 0.001);
    }

    @E("a variacao registrada deve ser nula")
    public void aVariacaoRegistradaDeveSerNula() {
        assertNull(historico.getPercentualVariacao());
    }
}
