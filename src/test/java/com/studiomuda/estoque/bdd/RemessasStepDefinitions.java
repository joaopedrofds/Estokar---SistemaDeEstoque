package com.studiomuda.estoque.bdd;

import com.studiomuda.estoque.dao.RemessaDAO;
import com.studiomuda.estoque.model.*;
import com.studiomuda.estoque.service.RemessaService;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RemessasStepDefinitions {
    private final RemessaDAO remessaDAO = mock(RemessaDAO.class);
    private final RemessaService remessaService = new RemessaService(remessaDAO);
    private final Date data = Date.valueOf(LocalDate.of(2026, 6, 10));
    private Doca docaPrincipal;
    private Doca docaAlternativa;
    private ResultadoAgendamentoRemessa resultado;

    @Dado("que a doca {string} suporta {int} paletes por dia")
    public void docaSuportaPaletesPorDia(String nome, Integer capacidade) throws Exception {
        docaPrincipal = new Doca(1, nome, capacidade, true);
        Distribuidora distribuidora = new Distribuidora(1, "Verde Express", "ALTA", true);

        when(remessaDAO.buscarDocaPorId(1)).thenReturn(docaPrincipal);
        when(remessaDAO.buscarDistribuidoraPorId(1)).thenReturn(distribuidora);
        when(remessaDAO.existeExcecaoAtiva(any(Date.class))).thenReturn(false);
        when(remessaDAO.existeAgendamentoNoHorario(anyInt(), any(Date.class), anyString())).thenReturn(false);
    }

    @E("ja existem {int} paletes agendados para essa doca")
    public void existemPaletesAgendados(Integer volumeAgendado) throws Exception {
        when(remessaDAO.somarVolumeConfirmado(1, data)).thenReturn(volumeAgendado);
    }

    @E("existe uma doca alternativa {string} com capacidade disponivel")
    public void existeDocaAlternativa(String nome) throws Exception {
        docaAlternativa = new Doca(2, nome, 12, true);
        when(remessaDAO.listarDocasAtivas()).thenReturn(Arrays.asList(docaPrincipal, docaAlternativa));
        when(remessaDAO.somarVolumeConfirmado(2, data)).thenReturn(0);
    }

    @Quando("solicito uma remessa de {int} paletes para a doca principal")
    public void solicitoRemessa(Integer volume) throws Exception {
        AgendamentoRemessa agendamento = new AgendamentoRemessa();
        agendamento.setDocaId(1);
        agendamento.setDistribuidoraId(1);
        agendamento.setData(data);
        agendamento.setHorario("08:00");
        agendamento.setVolumePaletes(volume);

        resultado = remessaService.agendar(agendamento);
    }

    @Então("o agendamento da remessa deve ser bloqueado")
    public void agendamentoDeveSerBloqueado() {
        assertFalse(resultado.isSucesso());
    }

    @E("o sistema deve sugerir uma janela alternativa na doca {string}")
    public void sistemaDeveSugerirJanelaAlternativa(String nomeDoca) {
        assertTrue(resultado.getSugestoes().stream()
                .anyMatch(sugestao -> nomeDoca.equals(sugestao.getDocaNome())));
    }
}
