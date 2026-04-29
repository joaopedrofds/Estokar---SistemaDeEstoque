package com.studiomuda.estoque.application.produto;

import com.studiomuda.estoque.application.produto.dto.SalvarProdutoCommand;
import com.studiomuda.estoque.domain.produto.Produto;
import com.studiomuda.estoque.domain.produto.ProdutoRepository;
import com.studiomuda.estoque.domain.produto.TipoProduto;
import com.studiomuda.estoque.domain.produto.exceptions.ProdutoNaoEncontradoException;
import org.springframework.stereotype.Service;

@Service
public class AtualizarProdutoUseCase {
    private final ProdutoRepository produtoRepository;

    public AtualizarProdutoUseCase(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public void executar(SalvarProdutoCommand cmd) {
        Produto existente = produtoRepository.buscarPorId(cmd.id())
                .orElseThrow(() -> new ProdutoNaoEncontradoException(cmd.id()));
        TipoProduto tipo = TipoProduto.fromCodigo(cmd.tipo());
        existente.atualizarDados(cmd.nome(), cmd.descricao(), tipo, cmd.quantidade(), cmd.valor());
        produtoRepository.atualizar(existente);
    }
}
