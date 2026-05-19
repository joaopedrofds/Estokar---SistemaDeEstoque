package com.studiomuda.estoque.bdd;

import io.cucumber.java.pt.*;
import static org.junit.jupiter.api.Assertions.*;

public class InadimplenciaStepDefinitions {
    private boolean temFaturasAtrasadas;
    private boolean vendaBloqueada;

    @Dado("que o cliente {string} tem faturas pendentes há mais de {int} dias")
    public void clienteTemFaturasPendentes(String nomeCliente, Integer diasAtraso) {
        this.temFaturasAtrasadas = (diasAtraso >= 45);
    }

    @Quando("o sistema valida o perfil para um novo pedido")
    public void sistemaValidaPerfil() {
        if (this.temFaturasAtrasadas) {
            this.vendaBloqueada = true;
        } else {
            this.vendaBloqueada = false;
        }
    }

    @Então("o sistema deve impedir a venda")
    public void sistemaDeveImpedirVenda() {
        assertTrue(this.vendaBloqueada, "A venda deveria ter sido bloqueada por inadimplência.");
    }
}
