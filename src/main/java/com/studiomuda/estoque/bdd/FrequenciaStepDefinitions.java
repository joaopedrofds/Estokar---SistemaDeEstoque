package com.studiomuda.estoque.bdd;

import io.cucumber.java.pt.*;
import static org.junit.jupiter.api.Assertions.*;

public class FrequenciaStepDefinitions {
    private int mediaDias;
    private String classificacao;

    @Dado("que o intervalo médio de compras do cliente é de {int} dias")
    public void intervaloMedioDeCompras(Integer dias) {
        this.mediaDias = dias;
    }

    @Quando("o algoritmo de classificação é executado")
    public void algoritmoDeClassificacaoExecutado() {
        if (this.mediaDias < 15) {
            this.classificacao = "VIP";
        } else if (this.mediaDias <= 30) {
            this.classificacao = "REGULAR";
        } else {
            this.classificacao = "EM RISCO";
        }
    }

    @Então("o cliente deve receber a etiqueta {string}")
    public void clienteRecebeEtiqueta(String etiquetaEsperada) {
        assertEquals(etiquetaEsperada, this.classificacao);
    }
}
