package com.studiomuda.estoque.service;

import com.studiomuda.estoque.model.Produto;
import com.studiomuda.estoque.repository.ProdutoRepository;
import com.studiomuda.estoque.observer.ObservadorDePreco;
import com.studiomuda.estoque.observer.PrecoDomainEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de domínio — orquestra atualização de produtos e notifica observers.
 * Padrão de Design: Observer (GoF) — ConcreteSubject
 * Nível Tático DDD: Domain Service
 * Camada: Aplicação (Arquitetura Limpa)
 * Persistência: ORM via Spring Data JPA
 */
@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final List<ObservadorDePreco> observadores = new ArrayList<>();

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public void registrarObservador(ObservadorDePreco obs) { observadores.add(obs); }
    public void removerObservador(ObservadorDePreco obs)   { observadores.remove(obs); }

    private void notificar(PrecoDomainEvent evento) {
        for (ObservadorDePreco obs : observadores) obs.aoAlterarPreco(evento);
    }

    /**
     * Atualiza o produto e dispara evento Observer se o preço mudou.
     */
    @Transactional
    public void atualizar(Produto produto, String usuarioLogin) {
        Optional<Produto> optAtual = produtoRepository.findById(produto.getId());
        if (optAtual.isPresent()) {
            Produto atual = optAtual.get();
            if (Double.compare(atual.getValor(), produto.getValor()) != 0) {
                produtoRepository.save(produto);
                notificar(new PrecoDomainEvent(
                    produto.getId(),
                    produto.getNome(),
                    atual.getValor(),
                    produto.getValor(),
                    usuarioLogin != null ? usuarioLogin : "sistema"
                ));
            } else {
                produtoRepository.save(produto);
            }
        }
    }

    public Optional<Produto> buscarPorId(int id) {
        return produtoRepository.findById(id);
    }

    public List<Produto> listar() {
        return produtoRepository.findAll();
    }

    public void salvar(Produto produto) {
        produtoRepository.save(produto);
    }

    public void deletar(int id) {
        produtoRepository.deleteById(id);
    }
}