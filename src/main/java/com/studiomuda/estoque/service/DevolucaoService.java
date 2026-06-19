package com.studiomuda.estoque.service;

import com.studiomuda.estoque.repository.DevolucaoRepository;
import com.studiomuda.estoque.repository.ItemDevolucaoRepository;
import com.studiomuda.estoque.repository.CreditoClienteRepository;
import com.studiomuda.estoque.model.Devolucao;
import com.studiomuda.estoque.model.ItemDevolucao;
import com.studiomuda.estoque.model.CreditoCliente;
import com.studiomuda.estoque.observer.CreditoClienteObserver;
import com.studiomuda.estoque.observer.DevolucaoDomainEvent;
import com.studiomuda.estoque.observer.EstoqueDevolucaoObserver;
import com.studiomuda.estoque.observer.ObservadorDeDevolucao;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de domínio para devoluções.
 * Subject do Observer + orquestrador do fluxo de aprovação.
 *
 * Padrão de Design: Observer (GoF) — ConcreteSubject
 * Nível Tático DDD: Domain Service
 * Camada: Aplicação (Arquitetura Limpa)
 */
@Service
public class DevolucaoService {

    @Autowired
    private DevolucaoRepository devolucaoRepository;

    @Autowired
    private ItemDevolucaoRepository itemRepository;

    @Autowired
    private CreditoClienteRepository creditoRepository;

    private final List<ObservadorDeDevolucao> observadores = new ArrayList<>();

    public DevolucaoService() {
        registrarObservador(new EstoqueDevolucaoObserver());
        registrarObservador(new CreditoClienteObserver());
    }

    public void registrarObservador(ObservadorDeDevolucao obs) { observadores.add(obs); }

    private void notificar(DevolucaoDomainEvent evento) {
        for (ObservadorDeDevolucao obs : observadores) obs.aoAprovarDevolucao(evento);
    }

    /**
     * Solicita uma nova devolução — persiste com status PENDENTE.
     */
    @Transactional
    public Devolucao solicitar(Devolucao devolucao, List<ItemDevolucao> itens) {
        // Definir defaults explícitos antes de salvar
        if (devolucao.getStatus() == null || devolucao.getStatus().isBlank()) {
            devolucao.setStatus("PENDENTE");
        }
        if (devolucao.getDataSolicitacao() == null) {
            devolucao.setDataSolicitacao(LocalDateTime.now());
        }
        if (devolucao.getTipoRestituicao() == null || devolucao.getTipoRestituicao().isBlank()) {
            devolucao.setTipoRestituicao("CREDITO_LOJA");
        }

        devolucao = devolucaoRepository.save(devolucao);
        for (ItemDevolucao item : itens) {
            item.setDevolucaoId(devolucao.getId());
            itemRepository.save(item);
        }
        devolucao.setItens(itens);
        return devolucao;
    }

    /**
     * Aprova a devolução — dispara Observer que restaura estoque e executa Strategy.
     * Idempotência: valida que está PENDENTE antes de aprovar.
     */
    @Transactional
    public void aprovar(int devolucaoId, String observacaoGestor) {
        Devolucao devolucao = devolucaoRepository.findById(devolucaoId).orElse(null);
        if (devolucao == null) throw new IllegalStateException("Devolução não encontrada.");
        if (!devolucao.isPendente()) throw new IllegalStateException(
                "Devolução não pode ser aprovada — status atual: " + devolucao.getStatus());

        devolucao.setStatus("APROVADA");
        devolucao.setObservacaoGestor(observacaoGestor);
        devolucao.setDataResolucao(LocalDateTime.now());
        devolucaoRepository.save(devolucao);

        List<ItemDevolucao> itens = itemRepository.findByDevolucaoId(devolucaoId);
        devolucao.setItens(itens);

        notificar(new DevolucaoDomainEvent(devolucao));
    }

    /**
     * Rejeita a devolução — sem efeito no estoque.
     */
    @Transactional
    public void rejeitar(int devolucaoId, String observacaoGestor) {
        Devolucao devolucao = devolucaoRepository.findById(devolucaoId).orElse(null);
        if (devolucao == null) throw new IllegalStateException("Devolução não encontrada.");
        if (!devolucao.isPendente()) throw new IllegalStateException(
                "Devolução já foi processada — status: " + devolucao.getStatus());

        devolucao.setStatus("REJEITADA");
        devolucao.setObservacaoGestor(observacaoGestor);
        devolucao.setDataResolucao(LocalDateTime.now());
        devolucaoRepository.save(devolucao);
    }

    public Devolucao buscarPorId(int id) {
        return devolucaoRepository.findById(id).orElse(null);
    }

    public List<Devolucao> listar() {
        return devolucaoRepository.findAll();
    }

    public List<Devolucao> listarPorStatus(String status) {
        return devolucaoRepository.findByStatusOrderByDataSolicitacaoDesc(status);
    }
}