package com.studiomuda.estoque.application.produto;

import com.studiomuda.estoque.domain.produto.ProdutoRepository;
import org.springframework.stereotype.Service;

@Service
public class RemoverProdutoUseCase {
    private final ProdutoRepository produtoRepository;

    public RemoverProdutoUseCase(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public void executar(int id) {
        produtoRepository.remover(id);
    }
}
