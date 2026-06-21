package com.studiomuda.estoque.service;

import com.studiomuda.estoque.model.AlertaReposicao;
import com.studiomuda.estoque.model.ParametroEstoque;
import com.studiomuda.estoque.observer.EstoqueAlteradoDomainEvent;
import com.studiomuda.estoque.observer.ObservadorDeEstoque;
import com.studiomuda.estoque.repository.AlertaReposicaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertaReposicaoService {

    private final AlertaReposicaoRepository alertaReposicaoRepository;
    private final List<ObservadorDeEstoque> observadores;

    public AlertaReposicaoService(AlertaReposicaoRepository alertaReposicaoRepository,
                                  List<ObservadorDeEstoque> observadores) {
        this.alertaReposicaoRepository = alertaReposicaoRepository;
        this.observadores = List.copyOf(observadores);
    }

    @Transactional
    public int sincronizarAlertas(List<ParametroEstoque> parametros) {
        int alertasAtivos = 0;
        for (ParametroEstoque parametro : parametros) {
            notificar(new EstoqueAlteradoDomainEvent(parametro));
            if (parametro.isReposicaoNecessaria()) {
                alertasAtivos++;
            }
        }
        return alertasAtivos;
    }

    @Transactional(readOnly = true)
    public List<AlertaReposicao> listarAtivos() {
        return alertaReposicaoRepository.findByStatusOrderByCriadoEmDesc(AlertaReposicao.STATUS_ATIVO);
    }

    @Transactional(readOnly = true)
    public List<AlertaReposicao> listarResolvidos() {
        return alertaReposicaoRepository.findByStatusOrderByCriadoEmDesc(AlertaReposicao.STATUS_RESOLVIDO);
    }

    @Transactional
    public void resolver(Integer id, String observacao) {
        AlertaReposicao alerta = alertaReposicaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alerta de reposicao nao encontrado."));
        alerta.setStatus(AlertaReposicao.STATUS_RESOLVIDO);
        alerta.setObservacao(observacao == null || observacao.isBlank()
                ? "Resolvido manualmente pelo operador."
                : observacao.trim());
        alerta.setResolvidoEm(LocalDateTime.now());
        alertaReposicaoRepository.save(alerta);
    }

    private void notificar(EstoqueAlteradoDomainEvent evento) {
        for (ObservadorDeEstoque observador : observadores) {
            observador.aoAlterarEstoque(evento);
        }
    }
}
