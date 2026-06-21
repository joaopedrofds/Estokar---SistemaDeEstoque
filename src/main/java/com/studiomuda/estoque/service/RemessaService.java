package com.studiomuda.estoque.service;

import com.studiomuda.estoque.jpa.entity.AgendamentoRemessaJpaEntity;
import com.studiomuda.estoque.jpa.entity.CalendarioExcecaoJpaEntity;
import com.studiomuda.estoque.jpa.entity.DistribuidoraJpaEntity;
import com.studiomuda.estoque.jpa.entity.DocaJpaEntity;
import com.studiomuda.estoque.jpa.repository.AgendamentoRemessaJpaRepository;
import com.studiomuda.estoque.jpa.repository.CalendarioExcecaoJpaRepository;
import com.studiomuda.estoque.jpa.repository.DistribuidoraJpaRepository;
import com.studiomuda.estoque.jpa.repository.DocaJpaRepository;
import com.studiomuda.estoque.model.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RemessaService {
    private static final String[] JANELAS_PADRAO = {"08:00", "10:00", "13:00", "15:00", "17:00"};

    private final DocaJpaRepository docaRepo;
    private final DistribuidoraJpaRepository distribuidoraRepo;
    private final CalendarioExcecaoJpaRepository calendarioRepo;
    private final AgendamentoRemessaJpaRepository agendamentoRepo;

    public RemessaService(DocaJpaRepository docaRepo,
                          DistribuidoraJpaRepository distribuidoraRepo,
                          CalendarioExcecaoJpaRepository calendarioRepo,
                          AgendamentoRemessaJpaRepository agendamentoRepo) {
        this.docaRepo = docaRepo;
        this.distribuidoraRepo = distribuidoraRepo;
        this.calendarioRepo = calendarioRepo;
        this.agendamentoRepo = agendamentoRepo;
    }

    public ResultadoAgendamentoRemessa agendar(AgendamentoRemessa agendamento) {
        validarAgendamento(agendamento);

        DocaJpaEntity docaEntity = docaRepo.findById(agendamento.getDocaId()).orElse(null);
        DistribuidoraJpaEntity distribuidoraEntity = distribuidoraRepo.findById(agendamento.getDistribuidoraId()).orElse(null);

        Doca doca = docaEntity != null ? toModel(docaEntity) : null;
        Distribuidora distribuidora = distribuidoraEntity != null ? toModel(distribuidoraEntity) : null;

        if (doca == null || !doca.isAtiva()) {
            return ResultadoAgendamentoRemessa.conflito("Doca indisponivel para agendamento.", sugerirJanelas(agendamento, distribuidora));
        }
        if (distribuidora == null || !distribuidora.isAtiva()) {
            return ResultadoAgendamentoRemessa.conflito("Distribuidora indisponivel para agendamento.", new ArrayList<>());
        }

        String conflito = identificarConflito(agendamento, doca);
        if (conflito != null) {
            return ResultadoAgendamentoRemessa.conflito(conflito, sugerirJanelas(agendamento, distribuidora));
        }

        AgendamentoRemessaJpaEntity entity = new AgendamentoRemessaJpaEntity();
        entity.setDoca(docaEntity);
        entity.setDistribuidora(distribuidoraEntity);
        entity.setData(agendamento.getData());
        entity.setHorario(agendamento.getHorario());
        entity.setVolumePaletes(agendamento.getVolumePaletes());
        entity.setStatus(AgendamentoRemessa.STATUS_CONFIRMADO);
        agendamentoRepo.save(entity);

        return ResultadoAgendamentoRemessa.sucesso("Remessa agendada com sucesso.");
    }

    private void validarAgendamento(AgendamentoRemessa agendamento) {
        if (agendamento.getDocaId() <= 0) {
            throw new IllegalArgumentException("Selecione uma doca valida.");
        }
        if (agendamento.getDistribuidoraId() <= 0) {
            throw new IllegalArgumentException("Selecione uma distribuidora valida.");
        }
        if (agendamento.getData() == null) {
            throw new IllegalArgumentException("Informe a data da remessa.");
        }
        if (agendamento.getHorario() == null || agendamento.getHorario().trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o horario da remessa.");
        }
        if (agendamento.getVolumePaletes() <= 0) {
            throw new IllegalArgumentException("O volume deve ser maior que zero.");
        }
    }

    private String identificarConflito(AgendamentoRemessa agendamento, Doca doca) {
        if (calendarioRepo.existsByDataAndAtivaTrue(agendamento.getData())) {
            return "A data selecionada esta bloqueada no calendario logistico.";
        }

        int volumeJaAgendado = agendamentoRepo.somarVolumeConfirmado(
                agendamento.getDocaId(), agendamento.getData(), AgendamentoRemessa.STATUS_CONFIRMADO);
        if (volumeJaAgendado + agendamento.getVolumePaletes() > doca.getCapacidadePaletesDiaria()) {
            return "A capacidade diaria da doca sera excedida.";
        }

        if (agendamentoRepo.existsByDoca_IdAndDataAndHorarioAndStatus(
                agendamento.getDocaId(), agendamento.getData(), agendamento.getHorario(), AgendamentoRemessa.STATUS_CONFIRMADO)) {
            return "Ja existe uma remessa confirmada para esta doca no horario selecionado.";
        }

        return null;
    }

    public List<SugestaoJanelaRemessa> sugerirJanelas(AgendamentoRemessa agendamento, Distribuidora distribuidora) {
        List<SugestaoJanelaRemessa> sugestoes = new ArrayList<>();
        LocalDate dataInicial = agendamento.getData().toLocalDate();
        int diasBusca = distribuidora != null && "ALTA".equalsIgnoreCase(distribuidora.getNivelPrioridade()) ? 3 : 7;

        List<Doca> docasAtivas = listarDocasAtivas();

        for (int dia = 0; dia <= diasBusca && sugestoes.size() < 3; dia++) {
            Date data = Date.valueOf(dataInicial.plusDays(dia));
            if (calendarioRepo.existsByDataAndAtivaTrue(data)) {
                continue;
            }
            for (Doca doca : docasAtivas) {
                int volumeJaAgendado = agendamentoRepo.somarVolumeConfirmado(
                        doca.getId(), data, AgendamentoRemessa.STATUS_CONFIRMADO);
                int capacidadeDisponivel = doca.getCapacidadePaletesDiaria() - volumeJaAgendado;
                if (capacidadeDisponivel < agendamento.getVolumePaletes()) {
                    continue;
                }
                for (String janela : JANELAS_PADRAO) {
                    if (!agendamentoRepo.existsByDoca_IdAndDataAndHorarioAndStatus(
                            doca.getId(), data, janela, AgendamentoRemessa.STATUS_CONFIRMADO)) {
                        sugestoes.add(new SugestaoJanelaRemessa(doca.getId(), doca.getNome(), data, janela, capacidadeDisponivel));
                        break;
                    }
                }
                if (sugestoes.size() >= 3) {
                    break;
                }
            }
        }

        sugestoes.sort(Comparator.comparing(SugestaoJanelaRemessa::getData)
                .thenComparing(SugestaoJanelaRemessa::getHorario)
                .thenComparing(SugestaoJanelaRemessa::getDocaNome));
        return sugestoes.size() > 3 ? sugestoes.subList(0, 3) : sugestoes;
    }

    public void cadastrarDoca(String nome, int capacidadePaletesDiaria) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o nome da doca.");
        }
        if (capacidadePaletesDiaria <= 0) {
            throw new IllegalArgumentException("A capacidade deve ser maior que zero.");
        }

        DocaJpaEntity entity = new DocaJpaEntity();
        entity.setNome(nome.trim());
        entity.setCapacidadePaletesDiaria(capacidadePaletesDiaria);
        entity.setAtiva(true);
        docaRepo.save(entity);
    }

    public void cadastrarDistribuidora(String nome, String nivelPrioridade) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o nome da distribuidora.");
        }
        if (nivelPrioridade == null || nivelPrioridade.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe a prioridade da distribuidora.");
        }

        DistribuidoraJpaEntity entity = new DistribuidoraJpaEntity();
        entity.setNome(nome.trim());
        entity.setNivelPrioridade(nivelPrioridade.trim().toUpperCase());
        entity.setAtiva(true);
        distribuidoraRepo.save(entity);
    }

    public void cadastrarExcecao(Date data, String motivo) {
        if (data == null) {
            throw new IllegalArgumentException("Informe a data bloqueada.");
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o motivo da excecao.");
        }

        CalendarioExcecaoJpaEntity entity = new CalendarioExcecaoJpaEntity();
        entity.setData(data);
        entity.setMotivo(motivo.trim());
        entity.setAtiva(true);
        calendarioRepo.save(entity);
    }

    public List<Doca> listarDocasAtivas() {
        return docaRepo.findByAtivaTrueOrderByNomeAsc().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<Distribuidora> listarDistribuidorasAtivas() {
        return distribuidoraRepo.findByAtivaTrueOrderByNomeAsc().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<AgendamentoRemessa> listarAgendamentos() {
        return agendamentoRepo.listarComRelacionamentos().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    // ---- entity → domain model mappers ----

    private Doca toModel(DocaJpaEntity e) {
        return new Doca(e.getId(), e.getNome(), e.getCapacidadePaletesDiaria(), e.isAtiva());
    }

    private Distribuidora toModel(DistribuidoraJpaEntity e) {
        return new Distribuidora(e.getId(), e.getNome(), e.getNivelPrioridade(), e.isAtiva());
    }

    private AgendamentoRemessa toModel(AgendamentoRemessaJpaEntity e) {
        AgendamentoRemessa a = new AgendamentoRemessa();
        a.setId(e.getId());
        a.setCodigoAgendamento(e.getCodigoAgendamento());
        a.setDocaId(e.getDoca().getId());
        a.setDistribuidoraId(e.getDistribuidora().getId());
        a.setData(e.getData());
        a.setHorario(e.getHorario());
        a.setVolumePaletes(e.getVolumePaletes());
        a.setStatus(e.getStatus());
        a.setDocaNome(e.getDoca().getNome());
        a.setDistribuidoraNome(e.getDistribuidora().getNome());
        a.setPrioridadeDistribuidora(e.getDistribuidora().getNivelPrioridade());
        return a;
    }
}
