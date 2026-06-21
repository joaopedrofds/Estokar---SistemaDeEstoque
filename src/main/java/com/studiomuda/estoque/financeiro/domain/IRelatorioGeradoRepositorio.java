package com.studiomuda.estoque.financeiro.domain;

import java.util.List;
import java.util.Optional;

/** Porta de domínio do agregado {@link RelatorioGerado} (E-12). */
public interface IRelatorioGeradoRepositorio {

    /** Persiste o relatório (e suas linhas) e devolve-o reconstruído (com data de geração). */
    RelatorioGerado persistir(RelatorioGerado relatorio);

    Optional<RelatorioGerado> buscarPorId(RelatorioId id);

    /** Histórico de relatórios gerados, mais recentes primeiro, limitado a {@code limite}. */
    List<RelatorioGerado> listarHistorico(int limite);
}
