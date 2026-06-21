package com.studiomuda.estoque.indicadores.domain;

import java.util.List;
import java.util.Optional;

/**
 * Porta de domínio do agregado {@link MetaIndicador} (E-13). Definida no
 * domínio (Java puro), implementada na infraestrutura por
 * {@code MetaIndicadorRepositorioJpa} — padrão Repository do PetCollar.
 */
public interface IMetaIndicadorRepositorio {

    void salvar(MetaIndicador meta);

    Optional<MetaIndicador> buscarPorId(MetaIndicadorId id);

    /**
     * Metas ativas e vigentes ({@code vigencia_inicio <= hoje <= vigencia_fim})
     * de um indicador, mais recente primeiro.
     */
    List<MetaIndicador> buscarVigentesPorIndicador(IndicadorId indicadorId);

    /** Desativa todas as metas do indicador exceto a informada. */
    void desativarOutrasMetas(IndicadorId indicadorId, MetaIndicadorId metaAtivaId);
}
