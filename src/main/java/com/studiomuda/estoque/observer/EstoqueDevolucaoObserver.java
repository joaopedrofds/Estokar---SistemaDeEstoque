package com.studiomuda.estoque.observer;

import com.studiomuda.estoque.model.ItemDevolucao;
import com.studiomuda.estoque.model.ItemPedido;
import com.studiomuda.estoque.model.Produto;
import com.studiomuda.estoque.repository.ItemPedidoRepository;
import com.studiomuda.estoque.repository.ProdutoRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

/**
 * ConcreteObserver — ao aprovar devolução:
 * 1. Restaura estoque via JPA (ORM)
 * 2. Remove ou reduz item do pedido via JPA (ORM)
 *
 * Padrão de Design: Observer (GoF) — ConcreteObserver
 * Persistência: ORM via Spring Data JPA
 */
@Component
public class EstoqueDevolucaoObserver implements ObservadorDeDevolucao {

    private final ProdutoRepository produtoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public EstoqueDevolucaoObserver(ProdutoRepository produtoRepository,
                                    ItemPedidoRepository itemPedidoRepository) {
        this.produtoRepository = produtoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    @Override
    public void aoAprovarDevolucao(DevolucaoDomainEvent evento) {
        if (evento.getDevolucao().getItens() == null ||
            evento.getDevolucao().getItens().isEmpty()) {
            System.err.println("[EstoqueDevolucaoObserver] Nenhum item para processar.");
            return;
        }

        int pedidoId = evento.getDevolucao().getPedidoId();

        for (ItemDevolucao itemDev : evento.getDevolucao().getItens()) {
            // 1. Restaurar estoque via ORM
            Optional<Produto> optProduto = produtoRepository.findById(itemDev.getProdutoId());
            if (optProduto.isPresent()) {
                Produto produto = optProduto.get();
                produto.setQuantidade(produto.getQuantidade() + itemDev.getQuantidade());
                produtoRepository.save(produto);
                System.out.println("[EstoqueDevolucaoObserver] ORM: Produto '" +
                    produto.getNome() + "' restaurado: +" + itemDev.getQuantidade() + " unidades.");
            } else {
                System.err.println("[EstoqueDevolucaoObserver] Produto " +
                    itemDev.getProdutoId() + " não encontrado.");
            }

            // 2. Remover ou reduzir item do pedido via ORM
            List<ItemPedido> itensPedido = itemPedidoRepository.findByPedidoId(pedidoId);
            for (ItemPedido itemPedido : itensPedido) {
                if (itemPedido.getProdutoId() == itemDev.getProdutoId()) {
                    int novaQtd = itemPedido.getQuantidade() - itemDev.getQuantidade();
                    if (novaQtd <= 0) {
                        // Remove o item completamente do pedido
                        itemPedidoRepository.delete(itemPedido);
                        System.out.println("[EstoqueDevolucaoObserver] ORM: Item produto " +
                            itemDev.getProdutoId() + " removido do pedido #" + pedidoId);
                    } else {
                        // Reduz a quantidade (devolução parcial)
                        itemPedido.setQuantidade(novaQtd);
                        itemPedidoRepository.save(itemPedido);
                        System.out.println("[EstoqueDevolucaoObserver] ORM: Item produto " +
                            itemDev.getProdutoId() + " reduzido para " + novaQtd +
                            " no pedido #" + pedidoId);
                    }
                    break;
                }
            }
        }
    }
}
