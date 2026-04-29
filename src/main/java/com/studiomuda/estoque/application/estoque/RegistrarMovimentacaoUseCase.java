package com.studiomuda.estoque.application.estoque;

import com.studiomuda.estoque.application.estoque.dto.RegistrarMovimentacaoCommand;
import com.studiomuda.estoque.domain.estoque.MovimentacaoEstoque;
import com.studiomuda.estoque.domain.estoque.MovimentacaoEstoqueRepository;
import com.studiomuda.estoque.domain.estoque.TipoMovimentacao;
import com.studiomuda.estoque.domain.produto.Produto;
import com.studiomuda.estoque.domain.produto.ProdutoRepository;
import com.studiomuda.estoque.domain.produto.exceptions.ProdutoNaoEncontradoException;
import org.springframework.stereotype.Service;

@Service
public class RegistrarMovimentacaoUseCase {
    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;

    public RegistrarMovimentacaoUseCase(MovimentacaoEstoqueRepository movimentacaoRepository,
                                        ProdutoRepository produtoRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
    }

    public MovimentacaoEstoque executar(RegistrarMovimentacaoCommand cmd) {
        Produto produto = produtoRepository.buscarPorId(cmd.produtoId())
                .orElseThrow(() -> new ProdutoNaoEncontradoException(cmd.produtoId()));

        TipoMovimentacao tipo = TipoMovimentacao.fromCodigo(cmd.tipo());
        // Valida via domínio: SAIDA com quantidade > estoque dispara IllegalStateException
        if (tipo == TipoMovimentacao.SAIDA) {
            produto.decrementarEstoque(cmd.quantidade());
        } else {
            produto.incrementarEstoque(cmd.quantidade());
        }

        MovimentacaoEstoque mov = new MovimentacaoEstoque(0, cmd.produtoId(), tipo,
                cmd.quantidade(), cmd.motivo(), cmd.data());
        return movimentacaoRepository.registrar(mov);
    }
}
