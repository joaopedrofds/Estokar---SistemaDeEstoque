package com.studiomuda.estoque.application.estoque;

import com.studiomuda.estoque.domain.estoque.MovimentacaoEstoque;
import com.studiomuda.estoque.domain.estoque.MovimentacaoEstoqueRepository;
import com.studiomuda.estoque.domain.estoque.TipoMovimentacao;
import com.studiomuda.estoque.domain.estoque.exceptions.MovimentacaoNaoEncontradaException;
import com.studiomuda.estoque.domain.produto.Produto;
import com.studiomuda.estoque.domain.produto.ProdutoRepository;
import com.studiomuda.estoque.domain.produto.exceptions.ProdutoNaoEncontradoException;
import org.springframework.stereotype.Service;

@Service
public class ExcluirMovimentacaoUseCase {
    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;

    public ExcluirMovimentacaoUseCase(MovimentacaoEstoqueRepository movimentacaoRepository,
                                      ProdutoRepository produtoRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
    }

    public void executar(int id) {
        MovimentacaoEstoque mov = movimentacaoRepository.buscarPorId(id)
                .orElseThrow(() -> new MovimentacaoNaoEncontradaException(id));

        Produto produto = produtoRepository.buscarPorId(mov.produtoId())
                .orElseThrow(() -> new ProdutoNaoEncontradoException(mov.produtoId()));

        if (mov.tipo() == TipoMovimentacao.ENTRADA && produto.quantidade() < mov.quantidade()) {
            throw new IllegalStateException(
                    "Não é possível excluir esta movimentação pois resultaria em estoque negativo. Estoque atual: "
                            + produto.quantidade());
        }

        movimentacaoRepository.removerComEstorno(mov);
    }
}
