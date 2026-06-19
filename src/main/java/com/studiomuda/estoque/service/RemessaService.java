package com.studiomuda.estoque.service;

import com.studiomuda.estoque.dao.RemessaDAO;
import com.studiomuda.estoque.model.*;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RemessaService {
    private static final String[] JANELAS_PADRAO = {"08:00", "10:00", "13:00", "15:00", "17:00"};

    private final RemessaDAO remessaDAO;

    public RemessaService() {
        this(new RemessaDAO());
    }

    public RemessaService(RemessaDAO remessaDAO) {
        this.remessaDAO = remessaDAO;
    }

    public ResultadoAgendamentoRemessa agendar(AgendamentoRemessa agendamento) throws SQLException {
        validarAgendamento(agendamento);

        Doca doca = remessaDAO.buscarDocaPorId(agendamento.getDocaId());
        Distribuidora distribuidora = remessaDAO.buscarDistribuidoraPorId(agendamento.getDistribuidoraId());
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

        remessaDAO.inserirAgendamento(agendamento);
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

    private String identificarConflito(AgendamentoRemessa agendamento, Doca doca) throws SQLException {
        if (remessaDAO.existeExcecaoAtiva(agendamento.getData())) {
            return "A data selecionada esta bloqueada no calendario logistico.";
        }

        int volumeJaAgendado = remessaDAO.somarVolumeConfirmado(agendamento.getDocaId(), agendamento.getData());
        if (volumeJaAgendado + agendamento.getVolumePaletes() > doca.getCapacidadePaletesDiaria()) {
            return "A capacidade diaria da doca sera excedida.";
        }

        if (remessaDAO.existeAgendamentoNoHorario(agendamento.getDocaId(), agendamento.getData(), agendamento.getHorario())) {
            return "Ja existe uma remessa confirmada para esta doca no horario selecionado.";
        }

        return null;
    }

    public List<SugestaoJanelaRemessa> sugerirJanelas(AgendamentoRemessa agendamento, Distribuidora distribuidora) throws SQLException {
        List<SugestaoJanelaRemessa> sugestoes = new ArrayList<>();
        LocalDate dataInicial = agendamento.getData().toLocalDate();
        int diasBusca = distribuidora != null && "ALTA".equalsIgnoreCase(distribuidora.getNivelPrioridade()) ? 3 : 7;

        for (int dia = 0; dia <= diasBusca && sugestoes.size() < 3; dia++) {
            Date data = Date.valueOf(dataInicial.plusDays(dia));
            if (remessaDAO.existeExcecaoAtiva(data)) {
                continue;
            }
            for (Doca doca : remessaDAO.listarDocasAtivas()) {
                int volumeJaAgendado = remessaDAO.somarVolumeConfirmado(doca.getId(), data);
                int capacidadeDisponivel = doca.getCapacidadePaletesDiaria() - volumeJaAgendado;
                if (capacidadeDisponivel < agendamento.getVolumePaletes()) {
                    continue;
                }
                for (String janela : JANELAS_PADRAO) {
                    if (!remessaDAO.existeAgendamentoNoHorario(doca.getId(), data, janela)) {
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

    public void cadastrarDoca(String nome, int capacidadePaletesDiaria) throws SQLException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o nome da doca.");
        }
        if (capacidadePaletesDiaria <= 0) {
            throw new IllegalArgumentException("A capacidade deve ser maior que zero.");
        }

        Doca doca = new Doca();
        doca.setNome(nome.trim());
        doca.setCapacidadePaletesDiaria(capacidadePaletesDiaria);
        remessaDAO.inserirDoca(doca);
    }

    public void cadastrarDistribuidora(String nome, String nivelPrioridade) throws SQLException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o nome da distribuidora.");
        }
        if (nivelPrioridade == null || nivelPrioridade.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe a prioridade da distribuidora.");
        }

        Distribuidora distribuidora = new Distribuidora();
        distribuidora.setNome(nome.trim());
        distribuidora.setNivelPrioridade(nivelPrioridade.trim().toUpperCase());
        remessaDAO.inserirDistribuidora(distribuidora);
    }

    public void cadastrarExcecao(Date data, String motivo) throws SQLException {
        if (data == null) {
            throw new IllegalArgumentException("Informe a data bloqueada.");
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o motivo da excecao.");
        }

        CalendarioExcecao excecao = new CalendarioExcecao();
        excecao.setData(data);
        excecao.setMotivo(motivo.trim());
        remessaDAO.inserirExcecao(excecao);
    }

    public List<Doca> listarDocasAtivas() throws SQLException {
        return remessaDAO.listarDocasAtivas();
    }

    public List<Distribuidora> listarDistribuidorasAtivas() throws SQLException {
        return remessaDAO.listarDistribuidorasAtivas();
    }

    public List<AgendamentoRemessa> listarAgendamentos() throws SQLException {
        return remessaDAO.listarAgendamentos();
    }
}
