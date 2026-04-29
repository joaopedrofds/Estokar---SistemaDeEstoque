package com.studiomuda.estoque.application.produto;

import com.studiomuda.estoque.domain.produto.Produto;
import com.studiomuda.estoque.domain.produto.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BuscarProdutoUseCase {
    private final ProdutoRepository produtoRepository;

    public BuscarProdutoUseCase(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Optional<Produto> porId(int id) {
        return produtoRepository.buscarPorId(id);
    }
}
