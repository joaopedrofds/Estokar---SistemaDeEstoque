package com.studiomuda.estoque.security.infrastructure.persistence;

import com.studiomuda.estoque.security.dominio.LogAcesso;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Entidade JPA do {@link LogAcesso} (E-11). Mapeia a tabela {@code log_acesso}
 * para leitura; a escrita continua no interceptor (JDBC). Id {@code int}
 * auto-increment (legado, gravado fora da JPA); {@code data_hora} é DB-generated.
 */
@Entity
@Table(name = "log_acesso")
public class LogAcessoJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "username")
    private String username;

    @Column(name = "recurso")
    private String recurso;

    @Column(name = "operacao")
    private String operacao;

    @Column(name = "resultado")
    private String resultado;

    @Column(name = "detalhe")
    private String detalhe;

    @Column(name = "data_hora", insertable = false, updatable = false)
    private LocalDateTime dataHora;

    protected LogAcessoJpa() {}

    public LogAcesso toDomain() {
        return new LogAcesso(id == null ? 0 : id, usuarioId, username, recurso, operacao,
                resultado, detalhe, dataHora);
    }
}
