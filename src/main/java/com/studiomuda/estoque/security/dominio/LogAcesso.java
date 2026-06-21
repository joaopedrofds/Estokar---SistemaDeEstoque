package com.studiomuda.estoque.security.dominio;

import java.time.LocalDateTime;

/**
 * Registro de auditoria de uma tentativa de acesso (E-11) — entidade de domínio
 * puro, imutável. É somente-leitura pela aplicação: a gravação ocorre no
 * {@code InterceptadorAutorizacaoDao} (JDBC, auto-increment), por isso a
 * identidade permanece {@code int} legado (não migrada para VO/UUID).
 *
 * <p>{@code dataHora} é atribuída pelo banco ({@code DEFAULT CURRENT_TIMESTAMP}).</p>
 */
public class LogAcesso {

    private final int id;
    private final Integer usuarioId;
    private final String username;
    private final String recurso;
    private final String operacao;
    private final String resultado;
    private final String detalhe;
    private final LocalDateTime dataHora;

    public LogAcesso(int id, Integer usuarioId, String username, String recurso, String operacao,
                     String resultado, String detalhe, LocalDateTime dataHora) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.username = username;
        this.recurso = recurso;
        this.operacao = operacao;
        this.resultado = resultado;
        this.detalhe = detalhe;
        this.dataHora = dataHora;
    }

    public int getId() { return id; }
    public Integer getUsuarioId() { return usuarioId; }
    public String getUsername() { return username; }
    public String getRecurso() { return recurso; }
    public String getOperacao() { return operacao; }
    public String getResultado() { return resultado; }
    public String getDetalhe() { return detalhe; }
    public LocalDateTime getDataHora() { return dataHora; }
}
