package com.studiomuda.estoque.domain.estoque;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MovimentacaoEstoqueRepository {

    /**
     * Persiste a movimentação E ajusta o estoque do produto na mesma transação.
     */
    MovimentacaoEstoque registrar(MovimentacaoEstoque movimentacao);

    /**
     * Remove a movimentação E estorna o ajuste de estoque na mesma transação.
     */
    void removerComEstorno(MovimentacaoEstoque movimentacao);

    Optional<MovimentacaoEstoque> buscarPorId(int id);

    void atualizarMetadados(int id, String motivo, LocalDate data);

    List<MovimentacaoEstoqueComProduto> listarTodas();

    List<MovimentacaoEstoqueComProduto> buscarComFiltros(String produtoNome, String tipo,
                                                          LocalDate dataInicio, LocalDate dataFim);
}
