package com.studiomuda.estoque.application.produto;

import com.studiomuda.estoque.domain.produto.Produto;
import com.studiomuda.estoque.domain.produto.ProdutoRepository;
import com.studiomuda.estoque.domain.produto.StatusEstoque;
import com.studiomuda.estoque.domain.produto.TipoProduto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarProdutosUseCase {
    private final ProdutoRepository produtoRepository;

    public ListarProdutosUseCase(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> listarTodos() {
        return produtoRepository.listarTodos();
    }

    public List<Produto> buscarComFiltros(String nome, String tipo, String estoque) {
        TipoProduto tipoFiltro = null;
        if (tipo != null && !tipo.trim().isEmpty()) {
            try {
                tipoFiltro = TipoProduto.fromCodigo(tipo);
            } catch (IllegalArgumentException ignored) {
                tipoFiltro = null;
            }
        }
        StatusEstoque estoqueFiltro = null;
        if (estoque != null && !estoque.trim().isEmpty()) {
            estoqueFiltro = StatusEstoque.fromCodigo(estoque);
        }
        return produtoRepository.buscarComFiltros(nome, tipoFiltro, estoqueFiltro);
    }
}
