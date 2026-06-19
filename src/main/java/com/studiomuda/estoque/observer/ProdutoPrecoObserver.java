package com.studiomuda.estoque.observer;

import com.studiomuda.estoque.model.Produto;
import com.studiomuda.estoque.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * ConcreteObserver — atualiza produto.valor ao confirmar preço simulado.
 * Integra com o HistoricoPrecosObserver já existente via PrecoDomainEvent.
 *
 * Padrão de Design: Observer (GoF) — ConcreteObserver
 * Persistência: ORM via Spring Data JPA
 */
@Component
public class ProdutoPrecoObserver implements ObservadorDePreco {

    @Autowired private ProdutoRepository repo;

    @Override
    public void aoAlterarPreco(PrecoDomainEvent evento) {
        Optional<Produto> opt = repo.findById(evento.getProdutoId());
        if (opt.isPresent()) {
            Produto produto = opt.get();
            produto.setValor(evento.getPrecoNovo());
            repo.save(produto);
            System.out.println("[ProdutoPrecoObserver] ORM: Produto '" +
                produto.getNome() + "' atualizado para R$ " + evento.getPrecoNovo());
        }
    }
}