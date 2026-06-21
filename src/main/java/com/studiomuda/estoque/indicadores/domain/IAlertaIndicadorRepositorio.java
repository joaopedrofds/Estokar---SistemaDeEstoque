package com.studiomuda.estoque.indicadores.domain;

import java.util.List;
import java.util.Optional;

/**
 * Porta de domínio do agregado {@link AlertaIndicador} (E-13). Definida no
 * domínio (Java puro), implementada na infraestrutura por
 * {@code AlertaIndicadorRepositorioJpa}.
 */
public interface IAlertaIndicadorRepositorio {

    void salvar(AlertaIndicador alerta);

    Optional<AlertaIndicador> buscarPorId(AlertaId id);

    /** Alerta ATIVO mais recente de um indicador, se houver. */
    Optional<AlertaIndicador> buscarAtivoPorIndicador(IndicadorId indicadorId);

    /** Alertas de um status, mais recentes primeiro. */
    List<AlertaIndicador> listarPorStatus(StatusAlerta status);
}
