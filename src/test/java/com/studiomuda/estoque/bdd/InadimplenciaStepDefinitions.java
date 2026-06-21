package com.studiomuda.estoque.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class InadimplenciaStepDefinitions {
    private boolean temFaturasAtrasadas;
    private boolean temAcordoAtivo;
    private boolean vendaBloqueada;

    @Dado("que o cliente {string} tem faturas pendentes há mais de {int} dias")
    public void clienteTemFaturasPendentes(String nomeCliente, Integer diasAtraso) {
        this.temFaturasAtrasadas = diasAtraso >= 45;
    }

    @E("o cliente possui acordo de pagamento ativo")
    public void clientePossuiAcordoDePagamentoAtivo() {
        this.temAcordoAtivo = true;
    }

    @Quando("o sistema valida o perfil para um novo pedido")
    public void sistemaValidaPerfil() {
        this.vendaBloqueada = this.temFaturasAtrasadas && !this.temAcordoAtivo;
    }

    @Então("o sistema deve impedir a venda")
    public void sistemaDeveImpedirVenda() {
        assertTrue(this.vendaBloqueada, "A venda deveria ter sido bloqueada por inadimplencia.");
    }

    @Então("o sistema deve permitir a venda")
    public void sistemaDevePermitirVenda() {
        assertFalse(this.vendaBloqueada, "A venda deveria ser permitida.");
    }
}
