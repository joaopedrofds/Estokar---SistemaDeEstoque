package com.studiomuda.estoque.application.pedido;

import com.studiomuda.estoque.application.estoque.RegistrarMovimentacaoUseCase;
import com.studiomuda.estoque.application.estoque.dto.RegistrarMovimentacaoCommand;
import com.studiomuda.estoque.domain.pedido.ItemPedido;
import com.studiomuda.estoque.domain.pedido.ItemPedidoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class RemoverItemUseCase {
    private final ItemPedidoRepository itemPedidoRepository;
    private final RegistrarMovimentacaoUseCase registrarMovimentacao;

    public RemoverItemUseCase(ItemPedidoRepository itemPedidoRepository,
                               RegistrarMovimentacaoUseCase registrarMovimentacao) {
        this.itemPedidoRepository = itemPedidoRepository;
        this.registrarMovimentacao = registrarMovimentacao;
    }

    public int executar(int itemId) {
        ItemPedido item = itemPedidoRepository.buscarPorId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado"));

        registrarMovimentacao.executar(new RegistrarMovimentacaoCommand(
                item.produtoId(),
                "entrada",
                item.quantidade(),
                "Estorno - Cancelamento Item Pedido #" + item.pedidoId(),
                LocalDate.now()));

        itemPedidoRepository.remover(itemId);
        return item.pedidoId();
    }
}
