package com.studiomuda.estoque.indicadores.application.dto;

import com.studiomuda.estoque.indicadores.domain.SnapshotIndicador;
import com.studiomuda.estoque.indicadores.domain.IndicadorOperacional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Objeto de apresentação do histórico de snapshots (E-13). Combina o snapshot de
 * domínio com o nome/código do indicador (dado de exibição que não pertence ao
 * agregado), mantendo os templates desacoplados das entidades — padrão de View/DTO
 * do PetCollar ({@code de(...)}).
 */
public class SnapshotIndicadorView {

    private final String id;
    private final String indicadorNome;
    private final String indicadorCodigo;
    private final double valorCalculado;
    private final LocalDate periodoInicio;
    private final LocalDate periodoFim;
    private final LocalDateTime dataExecucao;
    private final String executadoPor;
    private final String detalheRastreio;

    private SnapshotIndicadorView(String id, String indicadorNome, String indicadorCodigo,
                                  double valorCalculado, LocalDate periodoInicio, LocalDate periodoFim,
                                  LocalDateTime dataExecucao, String executadoPor, String detalheRastreio) {
        this.id = id;
        this.indicadorNome = indicadorNome;
        this.indicadorCodigo = indicadorCodigo;
        this.valorCalculado = valorCalculado;
        this.periodoInicio = periodoInicio;
        this.periodoFim = periodoFim;
        this.dataExecucao = dataExecucao;
        this.executadoPor = executadoPor;
        this.detalheRastreio = detalheRastreio;
    }

    public static SnapshotIndicadorView de(SnapshotIndicador s, IndicadorOperacional indicador) {
        return new SnapshotIndicadorView(
                s.getId().getValor(),
                indicador != null ? indicador.getNome() : null,
                indicador != null ? indicador.getCodigo() : null,
                s.getValorCalculado(),
                s.getPeriodoInicio(),
                s.getPeriodoFim(),
                s.getDataExecucao(),
                s.getExecutadoPor(),
                s.getDetalheRastreio());
    }

    public String getId() { return id; }
    public String getIndicadorNome() { return indicadorNome; }
    public String getIndicadorCodigo() { return indicadorCodigo; }
    public double getValorCalculado() { return valorCalculado; }
    public LocalDate getPeriodoInicio() { return periodoInicio; }
    public LocalDate getPeriodoFim() { return periodoFim; }
    public LocalDateTime getDataExecucao() { return dataExecucao; }
    public String getExecutadoPor() { return executadoPor; }
    public String getDetalheRastreio() { return detalheRastreio; }
}
