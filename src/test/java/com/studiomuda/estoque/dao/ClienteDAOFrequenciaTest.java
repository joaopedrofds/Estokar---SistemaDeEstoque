package com.studiomuda.estoque.dao;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClienteDAOFrequenciaTest {

    private final ClienteDAO clienteDAO = new ClienteDAO(new PedidoDAO());

    @Test
    void deveClassificarClientesConformeMediaDiasEntreCompras() {
        assertEquals("Cliente VIP", clienteDAO.classificarFrequencia(14.99));
        assertEquals("Regular", clienteDAO.classificarFrequencia(15.0));
        assertEquals("Regular", clienteDAO.classificarFrequencia(30.0));
        assertEquals("Em Risco/Inativo", clienteDAO.classificarFrequencia(30.01));
    }

    @Test
    void deveCalcularMediaExataComMultiplasCompras() {
        List<LocalDate> datas = Arrays.asList(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 11),
                LocalDate.of(2026, 1, 26)
        );

        double media = clienteDAO.calcularMediaDiasEntreCompras(datas, LocalDate.of(2026, 2, 1));

        assertEquals(12.5, media);
    }

    @Test
    void deveUsarDataReferenciaQuandoExisteApenasUmaCompra() {
        List<LocalDate> datas = Arrays.asList(LocalDate.of(2026, 1, 1));

        double media = clienteDAO.calcularMediaDiasEntreCompras(datas, LocalDate.of(2026, 1, 31));

        assertEquals(30.0, media);
    }
}
