package com.studiomuda.estoque.application.produto;

import com.studiomuda.estoque.application.produto.dto.SalvarProdutoCommand;
import com.studiomuda.estoque.domain.produto.Produto;
import com.studiomuda.estoque.domain.produto.ProdutoRepository;
import com.studiomuda.estoque.domain.produto.TipoProduto;
import org.springframework.stereotype.Service;

@Service
public class CadastrarProdutoUseCase {
    private final ProdutoRepository produtoRepository;

    public CadastrarProdutoUseCase(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto executar(SalvarProdutoCommand cmd) {
        TipoProduto tipo = TipoProduto.fromCodigo(cmd.tipo());
        Produto produto = Produto.novo(cmd.nome(), cmd.descricao(), tipo, cmd.quantidade(), cmd.valor());
        return produtoRepository.salvar(produto);
    }
}
