package com.studiomuda.estoque.service;

import com.studiomuda.estoque.model.Cupom;
import com.studiomuda.estoque.repository.CupomRepository;
import com.studiomuda.estoque.observer.CupomDomainEvent;
import com.studiomuda.estoque.observer.ObservadorDeCupom;
import com.studiomuda.estoque.strategy.ContextoDesconto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de domínio — validação e aplicação de cupons.
 * Padrão de Design: Observer (GoF) — ConcreteSubject
 * Padrão de Design: Strategy (GoF) — Context
 * Nível Tático DDD: Domain Service
 * Camada: Aplicação (Arquitetura Limpa)
 * Persistência: ORM via Spring Data JPA
 */
@Service
public class CupomService {

    private final CupomRepository cupomRepository;
    private final List<ObservadorDeCupom> observadores;

    public CupomService(CupomRepository cupomRepository,
                        List<ObservadorDeCupom> observadores) {
        this.cupomRepository = cupomRepository;
        this.observadores = List.copyOf(observadores);
    }

    private void notificar(CupomDomainEvent evento) {
        for (ObservadorDeCupom obs : observadores) obs.aoAplicarCupom(evento);
    }

    /**
     * Aplica cupom ao pedido e registra uso via Observer.
     */
    @Transactional
    public double aplicarCupom(int cupomId, int pedidoId, int clienteId,
                                double valorTotalPedido) {
        Optional<Cupom> opt = cupomRepository.findById(cupomId);
        if (opt.isEmpty()) throw new IllegalStateException("Cupom não encontrado.");

        Cupom cupom = opt.get();
        if (!cupom.isAtivo()) throw new IllegalStateException("Cupom inativo.");
        if (!cupom.isValido()) throw new IllegalStateException("Cupom expirado ou fora do período de validade.");
        if (cupom.isEsgotado()) throw new IllegalStateException("Cupom esgotado — limite de usos atingido.");
        if (!cupom.podeSerUsadoPor(clienteId)) throw new IllegalStateException("Este cupom é exclusivo para outro cliente.");

        ContextoDesconto contexto = new ContextoDesconto(cupom.getTipoDesconto());
        double desconto = contexto.calcular(valorTotalPedido, cupom.getValor());

        notificar(new CupomDomainEvent(cupomId, pedidoId, clienteId, desconto));

        return desconto;
    }

    public Cupom validarCupom(int cupomId, int clienteId) {
        Optional<Cupom> opt = cupomRepository.findById(cupomId);
        if (opt.isEmpty()) return null;
        Cupom cupom = opt.get();
        if (!cupom.isAtivo() || !cupom.isValido() ||
            cupom.isEsgotado() || !cupom.podeSerUsadoPor(clienteId)) return null;
        return cupom;
    }

    public Optional<Cupom> buscarPorId(int id) {
        return cupomRepository.findById(id);
    }

    public List<Cupom> listarTodos() {
        return cupomRepository.findAll();
    }

    public List<Cupom> listarAtivos() {
        return cupomRepository.findByAtivoTrueOrderByDataInicioDesc();
    }

    @Transactional
    public void salvar(Cupom cupom) {
        cupomRepository.save(cupom);
    }

    @Transactional
    public void deletar(int id) {
        cupomRepository.deleteById(id);
    }
}
