package com.studiomuda.estoque.observer;

import com.studiomuda.estoque.model.AlertaReposicao;
import com.studiomuda.estoque.model.ParametroEstoque;
import com.studiomuda.estoque.repository.AlertaReposicaoRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class AlertaReposicaoObserver implements ObservadorDeEstoque {

    private final AlertaReposicaoRepository alertaReposicaoRepository;

    public AlertaReposicaoObserver(AlertaReposicaoRepository alertaReposicaoRepository) {
        this.alertaReposicaoRepository = alertaReposicaoRepository;
    }

    @Override
    public void aoAlterarEstoque(EstoqueAlteradoDomainEvent evento) {
        ParametroEstoque parametro = evento.getParametro();
        Optional<AlertaReposicao> alertaAtivo = alertaReposicaoRepository.findByProdutoIdAndStatus(
                parametro.getProdutoId(),
                AlertaReposicao.STATUS_ATIVO
        );

        if (parametro.isReposicaoNecessaria()) {
            AlertaReposicao alerta = alertaAtivo.orElseGet(AlertaReposicao::new);
            alerta.setProdutoId(parametro.getProdutoId());
            alerta.setProdutoNome(parametro.getProdutoNome());
            alerta.setFornecedorNome(parametro.getFornecedorNome());
            alerta.setEstoqueAtual(parametro.getEstoqueAtual());
            alerta.setPontoPedido(parametro.getPontoPedido());
            alerta.setQuantidadeSugerida(parametro.calcularQuantidadeSugerida());
            alerta.setStatus(AlertaReposicao.STATUS_ATIVO);
            alerta.setObservacao(null);
            alerta.setResolvidoEm(null);
            alertaReposicaoRepository.save(alerta);
            return;
        }

        if (alertaAtivo.isPresent()) {
            AlertaReposicao alerta = alertaAtivo.get();
            alerta.setStatus(AlertaReposicao.STATUS_RESOLVIDO);
            alerta.setObservacao("Resolvido automaticamente apos recuperacao do estoque.");
            alerta.setResolvidoEm(LocalDateTime.now());
            alerta.setEstoqueAtual(parametro.getEstoqueAtual());
            alerta.setPontoPedido(parametro.getPontoPedido());
            alerta.setQuantidadeSugerida(0);
            alertaReposicaoRepository.save(alerta);
        }
    }
}
