<<<<<<< HEAD
package com.studiomuda.estoque.service;

import com.studiomuda.estoque.dao.RemessaDAO;
import com.studiomuda.estoque.model.*;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
=======
package com.studiomuda.estoque.service;

import com.studiomuda.estoque.dao.RemessaDAO;
import com.studiomuda.estoque.jpa.entity.AgendamentoRemessaJpaEntity;
import com.studiomuda.estoque.jpa.entity.CalendarioExcecaoJpaEntity;
import com.studiomuda.estoque.jpa.entity.DistribuidoraJpaEntity;
import com.studiomuda.estoque.jpa.entity.DocaJpaEntity;
import com.studiomuda.estoque.jpa.repository.AgendamentoRemessaJpaRepository;
import com.studiomuda.estoque.jpa.repository.CalendarioExcecaoJpaRepository;
import com.studiomuda.estoque.jpa.repository.DistribuidoraJpaRepository;
import com.studiomuda.estoque.jpa.repository.DocaJpaRepository;
import com.studiomuda.estoque.model.AgendamentoRemessa;
import com.studiomuda.estoque.model.CalendarioExcecao;
import com.studiomuda.estoque.model.Distribuidora;
import com.studiomuda.estoque.model.Doca;
import com.studiomuda.estoque.model.ResultadoAgendamentoRemessa;
import com.studiomuda.estoque.model.SugestaoJanelaRemessa;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RemessaService {
    private static final String[] JANELAS_PADRAO = {"08:00", "10:00", "13:00", "15:00", "17:00"};

    private final ObjectProvider<DocaJpaRepository> docaRepositoryProvider;
    private final ObjectProvider<DistribuidoraJpaRepository> distribuidoraRepositoryProvider;
    private final ObjectProvider<CalendarioExcecaoJpaRepository> calendarioRepositoryProvider;
    private final ObjectProvider<AgendamentoRemessaJpaRepository> agendamentoRepositoryProvider;
    private final RemessaDAO legacyRemessaDAO;

    @Autowired
    public RemessaService(ObjectProvider<DocaJpaRepository> docaRepositoryProvider,
                          ObjectProvider<DistribuidoraJpaRepository> distribuidoraRepositoryProvider,
                          ObjectProvider<CalendarioExcecaoJpaRepository> calendarioRepositoryProvider,
                          ObjectProvider<AgendamentoRemessaJpaRepository> agendamentoRepositoryProvider) {
        this.docaRepositoryProvider = docaRepositoryProvider;
        this.distribuidoraRepositoryProvider = distribuidoraRepositoryProvider;
        this.calendarioRepositoryProvider = calendarioRepositoryProvider;
        this.agendamentoRepositoryProvider = agendamentoRepositoryProvider;
        this.legacyRemessaDAO = null;
    }

    public RemessaService(RemessaDAO remessaDAO) {
        this.docaRepositoryProvider = null;
        this.distribuidoraRepositoryProvider = null;
        this.calendarioRepositoryProvider = null;
        this.agendamentoRepositoryProvider = null;
        this.legacyRemessaDAO = remessaDAO;
    }

    @Transactional
    public ResultadoAgendamentoRemessa agendar(AgendamentoRemessa agendamento) {
        if (legacyRemessaDAO != null) {
            return agendarComLegacyDao(agendamento);
        }
        validarAgendamento(agendamento);

        DocaJpaEntity doca = obterDocaRepository().findById(agendamento.getDocaId()).orElse(null);
        DistribuidoraJpaEntity distribuidora = obterDistribuidoraRepository().findById(agendamento.getDistribuidoraId()).orElse(null);
        if (doca == null || !doca.isAtiva()) {
            return ResultadoAgendamentoRemessa.conflito("Doca indisponivel para agendamento.", sugerirJanelas(agendamento, mapearDistribuidora(distribuidora)));
        }
        if (distribuidora == null || !distribuidora.isAtiva()) {
            return ResultadoAgendamentoRemessa.conflito("Distribuidora indisponivel para agendamento.", new ArrayList<>());
        }

        String conflito = identificarConflito(agendamento, doca);
        if (conflito != null) {
            return ResultadoAgendamentoRemessa.conflito(conflito, sugerirJanelas(agendamento, mapearDistribuidora(distribuidora)));
        }

        AgendamentoRemessaJpaEntity entidade = new AgendamentoRemessaJpaEntity();
        entidade.setCodigoAgendamento("REM-" + UUID.randomUUID());
        entidade.setDoca(doca);
        entidade.setDistribuidora(distribuidora);
        entidade.setData(agendamento.getData());
        entidade.setHorario(agendamento.getHorario());
        entidade.setVolumePaletes(agendamento.getVolumePaletes());
        entidade.setStatus(AgendamentoRemessa.STATUS_CONFIRMADO);
        obterAgendamentoRepository().save(entidade);
        return ResultadoAgendamentoRemessa.sucesso("Remessa agendada com sucesso.");
    }

    @Transactional
    public void cadastrarDoca(String nome, int capacidadePaletesDiaria) {
        if (legacyRemessaDAO != null) {
            cadastrarDocaLegacy(nome, capacidadePaletesDiaria);
            return;
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o nome da doca.");
        }
        if (capacidadePaletesDiaria <= 0) {
            throw new IllegalArgumentException("A capacidade deve ser maior que zero.");
        }

        DocaJpaEntity doca = new DocaJpaEntity();
        doca.setNome(nome.trim());
        doca.setCapacidadePaletesDiaria(capacidadePaletesDiaria);
        doca.setAtiva(true);
        obterDocaRepository().save(doca);
    }

    @Transactional
    public void cadastrarDistribuidora(String nome, String nivelPrioridade) {
        if (legacyRemessaDAO != null) {
            cadastrarDistribuidoraLegacy(nome, nivelPrioridade);
            return;
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o nome da distribuidora.");
        }
        if (nivelPrioridade == null || nivelPrioridade.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe a prioridade da distribuidora.");
        }

        DistribuidoraJpaEntity distribuidora = new DistribuidoraJpaEntity();
        distribuidora.setNome(nome.trim());
        distribuidora.setNivelPrioridade(nivelPrioridade.trim().toUpperCase());
        distribuidora.setAtiva(true);
        obterDistribuidoraRepository().save(distribuidora);
    }

    @Transactional
    public void cadastrarExcecao(Date data, String motivo) {
        if (legacyRemessaDAO != null) {
            cadastrarExcecaoLegacy(data, motivo);
            return;
        }
        if (data == null) {
            throw new IllegalArgumentException("Informe a data bloqueada.");
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o motivo da excecao.");
        }

        CalendarioExcecaoJpaEntity excecao = new CalendarioExcecaoJpaEntity();
        excecao.setData(data);
        excecao.setMotivo(motivo.trim());
        excecao.setAtiva(true);
        obterCalendarioRepository().save(excecao);
    }

    @Transactional(readOnly = true)
    public List<Doca> listarDocasAtivas() {
        if (legacyRemessaDAO != null) {
            try {
                return legacyRemessaDAO.listarDocasAtivas();
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
        return obterDocaRepository().findByAtivaTrueOrderByNomeAsc().stream()
                .map(this::mapearDoca)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Distribuidora> listarDistribuidorasAtivas() {
        if (legacyRemessaDAO != null) {
            try {
                return legacyRemessaDAO.listarDistribuidorasAtivas();
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
        return obterDistribuidoraRepository().findByAtivaTrueOrderByNomeAsc().stream()
                .map(this::mapearDistribuidora)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AgendamentoRemessa> listarAgendamentos() {
        if (legacyRemessaDAO != null) {
            try {
                return legacyRemessaDAO.listarAgendamentos();
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
        return obterAgendamentoRepository().listarComRelacionamentos().stream()
                .map(this::mapearAgendamento)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SugestaoJanelaRemessa> sugerirJanelas(AgendamentoRemessa agendamento, Distribuidora distribuidora) {
        if (legacyRemessaDAO != null) {
            return sugerirJanelasComLegacyDao(agendamento, distribuidora);
        }
        List<SugestaoJanelaRemessa> sugestoes = new ArrayList<>();
        LocalDate dataInicial = agendamento.getData().toLocalDate();
        int diasBusca = distribuidora != null && "ALTA".equalsIgnoreCase(distribuidora.getNivelPrioridade()) ? 3 : 7;

        for (int dia = 0; dia <= diasBusca && sugestoes.size() < 3; dia++) {
            Date data = Date.valueOf(dataInicial.plusDays(dia));
            if (obterCalendarioRepository().existsByDataAndAtivaTrue(data)) {
                continue;
            }
            for (DocaJpaEntity doca : obterDocaRepository().findByAtivaTrueOrderByNomeAsc()) {
                int volumeJaAgendado = obterVolumeConfirmado(doca.getId(), data);
                int capacidadeDisponivel = doca.getCapacidadePaletesDiaria() - volumeJaAgendado;
                if (capacidadeDisponivel < agendamento.getVolumePaletes()) {
                    continue;
                }
                for (String janela : JANELAS_PADRAO) {
                    if (!obterAgendamentoRepository().existsByDoca_IdAndDataAndHorarioAndStatus(doca.getId(), data, janela, AgendamentoRemessa.STATUS_CONFIRMADO)) {
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

    private ResultadoAgendamentoRemessa agendarComLegacyDao(AgendamentoRemessa agendamento) {
        validarAgendamento(agendamento);

        try {
            Doca doca = legacyRemessaDAO.buscarDocaPorId(agendamento.getDocaId());
            Distribuidora distribuidora = legacyRemessaDAO.buscarDistribuidoraPorId(agendamento.getDistribuidoraId());
            if (doca == null || !doca.isAtiva()) {
                return ResultadoAgendamentoRemessa.conflito("Doca indisponivel para agendamento.", sugerirJanelasComLegacyDao(agendamento, distribuidora));
            }
            if (distribuidora == null || !distribuidora.isAtiva()) {
                return ResultadoAgendamentoRemessa.conflito("Distribuidora indisponivel para agendamento.", new ArrayList<>());
            }

            String conflito = identificarConflitoComLegacyDao(agendamento, doca);
            if (conflito != null) {
                return ResultadoAgendamentoRemessa.conflito(conflito, sugerirJanelasComLegacyDao(agendamento, distribuidora));
            }

            legacyRemessaDAO.inserirAgendamento(agendamento);
            return ResultadoAgendamentoRemessa.sucesso("Remessa agendada com sucesso.");
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
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

    private String identificarConflito(AgendamentoRemessa agendamento, DocaJpaEntity doca) {
        if (obterCalendarioRepository().existsByDataAndAtivaTrue(agendamento.getData())) {
            return "A data selecionada esta bloqueada no calendario logistico.";
        }

        int volumeJaAgendado = obterVolumeConfirmado(doca.getId(), agendamento.getData());
        if (volumeJaAgendado + agendamento.getVolumePaletes() > doca.getCapacidadePaletesDiaria()) {
            return "A capacidade diaria da doca sera excedida.";
        }

        if (obterAgendamentoRepository().existsByDoca_IdAndDataAndHorarioAndStatus(doca.getId(), agendamento.getData(), agendamento.getHorario(), AgendamentoRemessa.STATUS_CONFIRMADO)) {
            return "Ja existe uma remessa confirmada para esta doca no horario selecionado.";
        }

        return null;
    }

    private String identificarConflitoComLegacyDao(AgendamentoRemessa agendamento, Doca doca) throws Exception {
        if (legacyRemessaDAO.existeExcecaoAtiva(agendamento.getData())) {
            return "A data selecionada esta bloqueada no calendario logistico.";
        }

        int volumeJaAgendado = legacyRemessaDAO.somarVolumeConfirmado(agendamento.getDocaId(), agendamento.getData());
        if (volumeJaAgendado + agendamento.getVolumePaletes() > doca.getCapacidadePaletesDiaria()) {
            return "A capacidade diaria da doca sera excedida.";
        }

        if (legacyRemessaDAO.existeAgendamentoNoHorario(agendamento.getDocaId(), agendamento.getData(), agendamento.getHorario())) {
            return "Ja existe uma remessa confirmada para esta doca no horario selecionado.";
        }

        return null;
    }

    private int obterVolumeConfirmado(Integer docaId, Date data) {
        Integer total = obterAgendamentoRepository().somarVolumeConfirmado(docaId, data, AgendamentoRemessa.STATUS_CONFIRMADO);
        return total != null ? total : 0;
    }

    private List<SugestaoJanelaRemessa> sugerirJanelasComLegacyDao(AgendamentoRemessa agendamento, Distribuidora distribuidora) {
        try {
            List<SugestaoJanelaRemessa> sugestoes = new ArrayList<>();
            LocalDate dataInicial = agendamento.getData().toLocalDate();
            int diasBusca = distribuidora != null && "ALTA".equalsIgnoreCase(distribuidora.getNivelPrioridade()) ? 3 : 7;

            for (int dia = 0; dia <= diasBusca && sugestoes.size() < 3; dia++) {
                Date data = Date.valueOf(dataInicial.plusDays(dia));
                if (legacyRemessaDAO.existeExcecaoAtiva(data)) {
                    continue;
                }
                for (Doca doca : legacyRemessaDAO.listarDocasAtivas()) {
                    int volumeJaAgendado = legacyRemessaDAO.somarVolumeConfirmado(doca.getId(), data);
                    int capacidadeDisponivel = doca.getCapacidadePaletesDiaria() - volumeJaAgendado;
                    if (capacidadeDisponivel < agendamento.getVolumePaletes()) {
                        continue;
                    }
                    for (String janela : JANELAS_PADRAO) {
                        if (!legacyRemessaDAO.existeAgendamentoNoHorario(doca.getId(), data, janela)) {
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
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private void cadastrarDocaLegacy(String nome, int capacidadePaletesDiaria) {
        try {
            Doca doca = new Doca();
            doca.setNome(nome.trim());
            doca.setCapacidadePaletesDiaria(capacidadePaletesDiaria);
            legacyRemessaDAO.inserirDoca(doca);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private void cadastrarDistribuidoraLegacy(String nome, String nivelPrioridade) {
        try {
            Distribuidora distribuidora = new Distribuidora();
            distribuidora.setNome(nome.trim());
            distribuidora.setNivelPrioridade(nivelPrioridade.trim().toUpperCase());
            legacyRemessaDAO.inserirDistribuidora(distribuidora);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private void cadastrarExcecaoLegacy(Date data, String motivo) {
        try {
            CalendarioExcecao excecao = new CalendarioExcecao();
            excecao.setData(data);
            excecao.setMotivo(motivo.trim());
            legacyRemessaDAO.inserirExcecao(excecao);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private Doca mapearDoca(DocaJpaEntity entidade) {
        Doca doca = new Doca();
        doca.setId(entidade.getId() != null ? entidade.getId() : 0);
        doca.setNome(entidade.getNome());
        doca.setCapacidadePaletesDiaria(entidade.getCapacidadePaletesDiaria() != null ? entidade.getCapacidadePaletesDiaria() : 0);
        doca.setAtiva(entidade.isAtiva());
        return doca;
    }

    private Distribuidora mapearDistribuidora(DistribuidoraJpaEntity entidade) {
        Distribuidora distribuidora = new Distribuidora();
        if (entidade == null) {
            return distribuidora;
        }
        distribuidora.setId(entidade.getId() != null ? entidade.getId() : 0);
        distribuidora.setNome(entidade.getNome());
        distribuidora.setNivelPrioridade(entidade.getNivelPrioridade());
        distribuidora.setAtiva(entidade.isAtiva());
        return distribuidora;
    }

    private AgendamentoRemessa mapearAgendamento(AgendamentoRemessaJpaEntity entidade) {
        AgendamentoRemessa agendamento = new AgendamentoRemessa();
        agendamento.setId(entidade.getId() != null ? entidade.getId() : 0);
        agendamento.setCodigoAgendamento(entidade.getCodigoAgendamento());
        agendamento.setDocaId(entidade.getDoca().getId() != null ? entidade.getDoca().getId() : 0);
        agendamento.setDistribuidoraId(entidade.getDistribuidora().getId() != null ? entidade.getDistribuidora().getId() : 0);
        agendamento.setData(entidade.getData());
        agendamento.setHorario(entidade.getHorario());
        agendamento.setVolumePaletes(entidade.getVolumePaletes() != null ? entidade.getVolumePaletes() : 0);
        agendamento.setStatus(entidade.getStatus());
        agendamento.setDocaNome(entidade.getDoca().getNome());
        agendamento.setDistribuidoraNome(entidade.getDistribuidora().getNome());
        agendamento.setPrioridadeDistribuidora(entidade.getDistribuidora().getNivelPrioridade());
        return agendamento;
    }

    private DocaJpaRepository obterDocaRepository() {
        DocaJpaRepository repository = docaRepositoryProvider.getIfAvailable();
        if (repository == null) {
            throw new IllegalStateException("Repositorio de docas indisponivel.");
        }
        return repository;
    }

    private DistribuidoraJpaRepository obterDistribuidoraRepository() {
        DistribuidoraJpaRepository repository = distribuidoraRepositoryProvider.getIfAvailable();
        if (repository == null) {
            throw new IllegalStateException("Repositorio de distribuidoras indisponivel.");
        }
        return repository;
    }

    private CalendarioExcecaoJpaRepository obterCalendarioRepository() {
        CalendarioExcecaoJpaRepository repository = calendarioRepositoryProvider.getIfAvailable();
        if (repository == null) {
            throw new IllegalStateException("Repositorio de calendario indisponivel.");
        }
        return repository;
    }

    private AgendamentoRemessaJpaRepository obterAgendamentoRepository() {
        AgendamentoRemessaJpaRepository repository = agendamentoRepositoryProvider.getIfAvailable();
        if (repository == null) {
            throw new IllegalStateException("Repositorio de agendamentos indisponivel.");
        }
        return repository;
    }
}
>>>>>>> ad03230 (Refatorando e atualizando Suprimentos e Remessas)
