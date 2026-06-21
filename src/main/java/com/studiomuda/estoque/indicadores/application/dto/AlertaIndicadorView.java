package com.studiomuda.estoque.indicadores.application.dto;

import com.studiomuda.estoque.indicadores.domain.AlertaIndicador;
import com.studiomuda.estoque.indicadores.domain.IndicadorOperacional;

import java.time.LocalDateTime;

/**
 * Objeto de apresentação de um alerta (E-13). Combina o alerta de domínio com o
 * nome/código do indicador (dado de exibição) e expõe enums como {@code String}
 * para os templates Thymeleaf (que comparam {@code tipoViolacao}/{@code status}
 * textualmente) — padrão de View/DTO do PetCollar ({@code de(...)}).
 */
public class AlertaIndicadorView {

    private final String id;
    private final String indicadorNome;
    private final String indicadorCodigo;
    private final String tipoViolacao;
    private final double valorEsperado;
    private final double valorEncontrado;
    private final String mensagem;
    private final String status;
    private final String resolvidoPor;
    private final String observacao;
    private final LocalDateTime dataAlerta;
    private final LocalDateTime dataResolucao;

    private AlertaIndicadorView(String id, String indicadorNome, String indicadorCodigo, String tipoViolacao,
                               double valorEsperado, double valorEncontrado, String mensagem, String status,
                               String resolvidoPor, String observacao, LocalDateTime dataAlerta,
                               LocalDateTime dataResolucao) {
        this.id = id;
        this.indicadorNome = indicadorNome;
        this.indicadorCodigo = indicadorCodigo;
        this.tipoViolacao = tipoViolacao;
        this.valorEsperado = valorEsperado;
        this.valorEncontrado = valorEncontrado;
        this.mensagem = mensagem;
        this.status = status;
        this.resolvidoPor = resolvidoPor;
        this.observacao = observacao;
        this.dataAlerta = dataAlerta;
        this.dataResolucao = dataResolucao;
    }

    public static AlertaIndicadorView de(AlertaIndicador a, IndicadorOperacional indicador) {
        return new AlertaIndicadorView(
                a.getId().getValor(),
                indicador != null ? indicador.getNome() : null,
                indicador != null ? indicador.getCodigo() : null,
                a.getTipoViolacao().name(),
                a.getValorEsperado(),
                a.getValorEncontrado(),
                a.getMensagem(),
                a.getStatus().name(),
                a.getResolvidoPor(),
                a.getObservacao(),
                a.getDataAlerta(),
                a.getDataResolucao());
    }

    public String getId() { return id; }
    public String getIndicadorNome() { return indicadorNome; }
    public String getIndicadorCodigo() { return indicadorCodigo; }
    public String getTipoViolacao() { return tipoViolacao; }
    public double getValorEsperado() { return valorEsperado; }
    public double getValorEncontrado() { return valorEncontrado; }
    public String getMensagem() { return mensagem; }
    public String getStatus() { return status; }
    public String getResolvidoPor() { return resolvidoPor; }
    public String getObservacao() { return observacao; }
    public LocalDateTime getDataAlerta() { return dataAlerta; }
    public LocalDateTime getDataResolucao() { return dataResolucao; }
}
