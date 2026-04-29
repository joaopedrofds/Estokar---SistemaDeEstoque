package com.studiomuda.estoque.domain.produto;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository {

    Produto salvar(Produto produto);

    void atualizar(Produto produto);

    Optional<Produto> buscarPorId(int id);

    List<Produto> listarTodos();

    List<Produto> buscarComFiltros(String nome, TipoProduto tipo, StatusEstoque estoque);

    void remover(int id);
}
