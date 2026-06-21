package com.studiomuda.estoque.bdd;

import com.studiomuda.estoque.jpa.entity.AgendamentoRemessaJpaEntity;
import com.studiomuda.estoque.jpa.entity.DistribuidoraJpaEntity;
import com.studiomuda.estoque.jpa.entity.DocaJpaEntity;
import com.studiomuda.estoque.jpa.repository.AgendamentoRemessaJpaRepository;
import com.studiomuda.estoque.jpa.repository.CalendarioExcecaoJpaRepository;
import com.studiomuda.estoque.jpa.repository.DistribuidoraJpaRepository;
import com.studiomuda.estoque.jpa.repository.DocaJpaRepository;
import com.studiomuda.estoque.model.*;
import com.studiomuda.estoque.service.RemessaService;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RemessasStepDefinitions {
    private final DocaJpaRepository docaRepo = mock(DocaJpaRepository.class);
    private final DistribuidoraJpaRepository distribuidoraRepo = mock(DistribuidoraJpaRepository.class);
    private final CalendarioExcecaoJpaRepository calendarioRepo = mock(CalendarioExcecaoJpaRepository.class);
    private final AgendamentoRemessaJpaRepository agendamentoRepo = mock(AgendamentoRemessaJpaRepository.class);
    private final RemessaService remessaService = new RemessaService(docaRepo, distribuidoraRepo, calendarioRepo, agendamentoRepo);

    private final Date data = Date.valueOf(LocalDate.of(2026, 6, 10));
    private DocaJpaEntity docaPrincipalEntity;
    private DocaJpaEntity docaAlternativaEntity;
    private ResultadoAgendamentoRemessa resultado;

    @Dado("que a doca {string} suporta {int} paletes por dia")
    public void docaSuportaPaletesPorDia(String nome, Integer capacidade) {
        docaPrincipalEntity = criarDocaEntity(1, nome, capacidade, true);
        DistribuidoraJpaEntity distribuidoraEntity = criarDistribuidoraEntity(1, "Verde Express", "ALTA", true);

        when(docaRepo.findById(1)).thenReturn(Optional.of(docaPrincipalEntity));
        when(distribuidoraRepo.findById(1)).thenReturn(Optional.of(distribuidoraEntity));
        when(calendarioRepo.existsByDataAndAtivaTrue(any(Date.class))).thenReturn(false);
        when(agendamentoRepo.existsByDoca_IdAndDataAndHorarioAndStatus(anyInt(), any(Date.class), anyString(), anyString())).thenReturn(false);
        when(agendamentoRepo.somarVolumeConfirmado(anyInt(), any(Date.class), anyString())).thenReturn(0);
        when(agendamentoRepo.save(any(AgendamentoRemessaJpaEntity.class))).thenAnswer(i -> i.getArgument(0));
    }

    @E("ja existem {int} paletes agendados para essa doca")
    public void existemPaletesAgendados(Integer volumeAgendado) {
        when(agendamentoRepo.somarVolumeConfirmado(eq(1), eq(data), eq(AgendamentoRemessa.STATUS_CONFIRMADO)))
                .thenReturn(volumeAgendado);
    }

    @E("existe uma doca alternativa {string} com capacidade disponivel")
    public void existeDocaAlternativa(String nome) {
        docaAlternativaEntity = criarDocaEntity(2, nome, 12, true);
        when(docaRepo.findByAtivaTrueOrderByNomeAsc())
                .thenReturn(Arrays.asList(docaPrincipalEntity, docaAlternativaEntity));
        when(agendamentoRepo.somarVolumeConfirmado(eq(2), any(Date.class), anyString())).thenReturn(0);
    }

    @E("o horario solicitado ja esta ocupado")
    public void horarioSolicitadoJaEstaOcupado() {
        when(agendamentoRepo.existsByDoca_IdAndDataAndHorarioAndStatus(
                eq(1), eq(data), eq("08:00"), eq(AgendamentoRemessa.STATUS_CONFIRMADO))).thenReturn(true);
    }

    @Quando("solicito uma remessa de {int} paletes para a doca principal")
    public void solicitoRemessa(Integer volume) {
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

    @Então("o agendamento da remessa deve ser confirmado")
    public void agendamentoDeveSerConfirmado() {
        assertTrue(resultado.isSucesso());
    }

    @E("o sistema deve sugerir uma janela alternativa na doca {string}")
    public void sistemaDeveSugerirJanelaAlternativa(String nomeDoca) {
        assertTrue(resultado.getSugestoes().stream()
                .anyMatch(sugestao -> nomeDoca.equals(sugestao.getDocaNome())));
    }

    // ---- helpers ----

    private static DocaJpaEntity criarDocaEntity(int id, String nome, int capacidade, boolean ativa) {
        DocaJpaEntity e = new DocaJpaEntity();
        // id is generated, but for tests we need to set it via reflection or a test constructor.
        // Use a subclass to expose the id field.
        try {
            java.lang.reflect.Field f = DocaJpaEntity.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(e, id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        e.setNome(nome);
        e.setCapacidadePaletesDiaria(capacidade);
        e.setAtiva(ativa);
        return e;
    }

    private static DistribuidoraJpaEntity criarDistribuidoraEntity(int id, String nome, String nivel, boolean ativa) {
        DistribuidoraJpaEntity e = new DistribuidoraJpaEntity();
        try {
            java.lang.reflect.Field f = DistribuidoraJpaEntity.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(e, id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        e.setNome(nome);
        e.setNivelPrioridade(nivel);
        e.setAtiva(ativa);
        return e;
    }
}
