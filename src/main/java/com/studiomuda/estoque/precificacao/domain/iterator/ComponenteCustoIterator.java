package com.studiomuda.estoque.precificacao.domain.iterator;

import com.studiomuda.estoque.precificacao.domain.model.ComponenteCusto;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


public class ComponenteCustoIterator implements Iterator<ComponenteCusto> {
    private final List<ComponenteCusto> componentes;
    private int indiceAtual = 0;

    public ComponenteCustoIterator(List<ComponenteCusto> componentes) {
        this.componentes = componentes;
    }

    @Override
    public boolean hasNext() {
        return indiceAtual < componentes.size();
    }

    @Override
    public ComponenteCusto next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Não há mais componentes de custo para iterar.");
        }
        return componentes.get(indiceAtual++);
    }
}
