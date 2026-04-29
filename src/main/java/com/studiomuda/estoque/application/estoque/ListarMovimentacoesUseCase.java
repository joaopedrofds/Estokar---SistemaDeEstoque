package com.studiomuda.estoque.application.estoque;

import com.studiomuda.estoque.domain.estoque.MovimentacaoEstoqueComProduto;
import com.studiomuda.estoque.domain.estoque.MovimentacaoEstoqueRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ListarMovimentacoesUseCase {
    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    public ListarMovimentacoesUseCase(MovimentacaoEstoqueRepository movimentacaoRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
    }

    public List<MovimentacaoEstoqueComProduto> listarTodas() {
        return movimentacaoRepository.listarTodas();
    }

    public List<MovimentacaoEstoqueComProduto> buscarComFiltros(String produto, String tipo,
                                                                 String dataInicio, String dataFim) {
        LocalDate inicio = parse(dataInicio);
        LocalDate fim = parse(dataFim);
        return movimentacaoRepository.buscarComFiltros(produto, tipo, inicio, fim);
    }

    private LocalDate parse(String d) {
        return (d == null || d.trim().isEmpty()) ? null : LocalDate.parse(d.trim());
    }
}
