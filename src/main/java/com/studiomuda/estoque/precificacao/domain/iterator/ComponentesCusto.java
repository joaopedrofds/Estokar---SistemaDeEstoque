package com.studiomuda.estoque.precificacao.domain.iterator;

import com.studiomuda.estoque.precificacao.domain.model.ComponenteCusto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class ComponentesCusto implements Iterable<ComponenteCusto> {
    private final List<ComponenteCusto> componentes = new ArrayList<>();

    public void adicionar(ComponenteCusto componente) {
        if (componente != null) {
            componentes.add(componente);
        }
    }

    public BigDecimal total() {
        BigDecimal total = BigDecimal.ZERO;
        Iterator<ComponenteCusto> iterator = iterator();
        while (iterator.hasNext()) {
            total = total.add(iterator.next().getValor());
        }
        return total;
    }

    public List<ComponenteCusto> comoLista() {
        return Collections.unmodifiableList(componentes);
    }

    @Override
    public Iterator<ComponenteCusto> iterator() {
        return new ComponenteCustoIterator(componentes);
    }
}
