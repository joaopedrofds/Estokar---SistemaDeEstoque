package com.studiomuda.estoque.strategy;

import com.studiomuda.estoque.model.Devolucao;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContextoRestituicaoTest {

    @Test
    void selecionaTrocaQuandoTipoForTroca() {
        RestituicaoStrategy credito = novaStrategy("credito");
        RestituicaoStrategy troca = novaStrategy("troca");
        RestituicaoStrategy estorno = novaStrategy("estorno");

        ContextoRestituicao contexto = new ContextoRestituicao(credito, troca, estorno);
        assertEquals("troca", contexto.selecionar("TROCA").descricao());
    }

    @Test
    void selecionaCreditoComoPadrao() {
        RestituicaoStrategy credito = novaStrategy("credito");
        RestituicaoStrategy troca = novaStrategy("troca");
        RestituicaoStrategy estorno = novaStrategy("estorno");

        ContextoRestituicao contexto = new ContextoRestituicao(credito, troca, estorno);
        assertEquals("credito", contexto.selecionar("CREDITO_LOJA").descricao());
    }

    private RestituicaoStrategy novaStrategy(String descricao) {
        return new RestituicaoStrategy() {
            @Override
            public void executar(Devolucao devolucao) throws SQLException {
            }

            @Override
            public String descricao() {
                return descricao;
            }
        };
    }
}
