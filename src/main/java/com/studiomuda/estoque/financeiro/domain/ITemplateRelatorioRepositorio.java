package com.studiomuda.estoque.financeiro.domain;

import java.util.List;
import java.util.Optional;

/** Porta de domínio do agregado {@link TemplateRelatorio} (E-12). */
public interface ITemplateRelatorioRepositorio {

    void salvar(TemplateRelatorio template);

    Optional<TemplateRelatorio> buscarPorId(TemplateId id);

    List<TemplateRelatorio> listarTodos();

    List<TemplateRelatorio> listarAtivos();
}
