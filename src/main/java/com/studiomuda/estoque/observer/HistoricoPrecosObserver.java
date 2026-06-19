package com.studiomuda.estoque.observer;

import com.studiomuda.estoque.model.HistoricoPreco;
import com.studiomuda.estoque.repository.HistoricoPrecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ConcreteObserver — escuta eventos de preço e persiste o histórico via JPA.
 * Padrão de Design: Observer (GoF) — ConcreteObserver
 * Persistência: ORM via Spring Data JPA
 */
@Component
public class HistoricoPrecosObserver implements ObservadorDePreco {

    @Autowired private HistoricoPrecoRepository repository;

    @Override
    public void aoAlterarPreco(PrecoDomainEvent evento) {
        if (!evento.precoMudou()) return;

        Double percentual = calcularVariacao(evento.getPrecoAnterior(), evento.getPrecoNovo());
        HistoricoPreco h = new HistoricoPreco(
            evento.getProdutoId(),
            evento.getPrecoAnterior(),
            evento.getPrecoNovo(),
            percentual,
            evento.getUsuarioResponsavel()
        );

        repository.save(h);
        System.out.println("[HistoricoPrecosObserver] JPA: Histórico de preço salvo para produto " +
                evento.getProdutoId() + " — " + evento.getPrecoAnterior() + " → " + evento.getPrecoNovo());
    }

    private Double calcularVariacao(double anterior, double novo) {
        if (Double.compare(anterior, 0.0) == 0) return null;
        return ((novo - anterior) / anterior) * 100.0;
    }
}