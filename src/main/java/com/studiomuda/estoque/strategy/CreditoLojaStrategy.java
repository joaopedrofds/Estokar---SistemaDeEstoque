package com.studiomuda.estoque.strategy;

import com.studiomuda.estoque.model.CreditoCliente;
import com.studiomuda.estoque.model.Devolucao;
import com.studiomuda.estoque.repository.CreditoClienteRepository;
import com.studiomuda.estoque.util.SpringContextUtil;
import java.math.BigDecimal;

/**
 * ConcreteStrategy — gera crédito em loja para o cliente usar em compras futuras.
 * Padrão de Design: Strategy (GoF) — ConcreteStrategy
 * Migrado para usar JPA Repository (CreditoClienteRepository).
 */
public class CreditoLojaStrategy implements RestituicaoStrategy {

    @Override
    public void executar(Devolucao devolucao) {
        // Calcular valor do crédito
        double valorCredito = calcularValorCredito(devolucao);

        // Criar registro de crédito
        CreditoCliente credito = new CreditoCliente();
        credito.setClienteId(devolucao.getClienteId());
        credito.setDevolucaoId(devolucao.getId());
        credito.setValor(valorCredito);
        credito.setStatus("DISPONIVEL");

        // Usar JPA Repository via SpringContextUtil
        CreditoClienteRepository creditoRepo = SpringContextUtil.getBean(CreditoClienteRepository.class);
        creditoRepo.save(credito);

        System.out.println("[CreditoLojaStrategy] Crédito de " + valorCredito +
                " gerado para cliente #" + devolucao.getClienteId() +
                " (devolução #" + devolucao.getId() + ")");
    }

    private double calcularValorCredito(Devolucao devolucao) {
        if (devolucao.getItens() == null) return 0;
        return devolucao.getItens().stream()
                .mapToDouble(item -> item.getQuantidade() * item.getValorUnitario())
                .sum();
    }

    @Override
    public String descricao() { return "Crédito em loja"; }
}