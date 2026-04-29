package com.studiomuda.estoque.application.pedido;

import com.studiomuda.estoque.application.estoque.RegistrarMovimentacaoUseCase;
import com.studiomuda.estoque.application.estoque.dto.RegistrarMovimentacaoCommand;
import com.studiomuda.estoque.application.pedido.dto.AdicionarItemCommand;
import com.studiomuda.estoque.domain.pedido.ItemPedido;
import com.studiomuda.estoque.domain.pedido.ItemPedidoRepository;
import com.studiomuda.estoque.domain.produto.Produto;
import com.studiomuda.estoque.domain.produto.ProdutoRepository;
import com.studiomuda.estoque.domain.produto.exceptions.ProdutoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AdicionarItemUseCase {
    private final ItemPedidoRepository itemPedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final RegistrarMovimentacaoUseCase registrarMovimentacao;

    public AdicionarItemUseCase(ItemPedidoRepository itemPedidoRepository,
                                 ProdutoRepository produtoRepository,
                                 RegistrarMovimentacaoUseCase registrarMovimentacao) {
        this.itemPedidoRepository = itemPedidoRepository;
        this.produtoRepository = produtoRepository;
        this.registrarMovimentacao = registrarMovimentacao;
    }

    public ItemPedido executar(AdicionarItemCommand cmd) {
        Produto produto = produtoRepository.buscarPorId(cmd.produtoId())
                .orElseThrow(() -> new ProdutoNaoEncontradoException(cmd.produtoId()));

        if (cmd.quantidade() > produto.quantidade()) {
            throw new IllegalStateException(
                    "Estoque insuficiente. Quantidade disponível: " + produto.quantidade());
        }

        ItemPedido item = itemPedidoRepository.salvar(
                ItemPedido.novo(cmd.pedidoId(), cmd.produtoId(), cmd.quantidade()));

        registrarMovimentacao.executar(new RegistrarMovimentacaoCommand(
                cmd.produtoId(),
                "saida",
                cmd.quantidade(),
                "Venda - Pedido #" + cmd.pedidoId(),
                LocalDate.now()));

        return item;
    }
}
