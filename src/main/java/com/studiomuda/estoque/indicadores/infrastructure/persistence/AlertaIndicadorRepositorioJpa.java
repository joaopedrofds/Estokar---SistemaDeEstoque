package com.studiomuda.estoque.indicadores.infrastructure.persistence;

import com.studiomuda.estoque.indicadores.domain.AlertaId;
import com.studiomuda.estoque.indicadores.domain.AlertaIndicador;
import com.studiomuda.estoque.indicadores.domain.IAlertaIndicadorRepositorio;
import com.studiomuda.estoque.indicadores.domain.IndicadorId;
import com.studiomuda.estoque.indicadores.domain.StatusAlerta;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter JPA que implementa a porta {@link IAlertaIndicadorRepositorio},
 * traduzindo domínio ↔ {@link AlertaIndicadorJpa} via {@code fromDomain}/{@code toDomain}.
 */
@Repository
public class AlertaIndicadorRepositorioJpa implements IAlertaIndicadorRepositorio {

    private final AlertaIndicadorJpaRepository jpa;

    public AlertaIndicadorRepositorioJpa(AlertaIndicadorJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void salvar(AlertaIndicador alerta) {
        jpa.save(AlertaIndicadorJpa.fromDomain(alerta));
    }

    @Override
    public Optional<AlertaIndicador> buscarPorId(AlertaId id) {
        return jpa.findById(id.getValor())
                .map(AlertaIndicadorJpa::toDomain);
    }

    @Override
    public Optional<AlertaIndicador> buscarAtivoPorIndicador(IndicadorId indicadorId) {
        return Optional.ofNullable(
                        jpa.findFirstByIndicadorIdAndStatusOrderByDataAlertaDesc(indicadorId.getValor(), "ATIVO"))
                .map(AlertaIndicadorJpa::toDomain);
    }

    @Override
    public List<AlertaIndicador> listarPorStatus(StatusAlerta status) {
        return jpa.findByStatusOrderByDataAlertaDesc(status.name()).stream()
                .map(AlertaIndicadorJpa::toDomain)
                .toList();
    }
}
