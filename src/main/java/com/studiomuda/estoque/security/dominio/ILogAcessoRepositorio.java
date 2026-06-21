package com.studiomuda.estoque.security.dominio;

import java.util.List;

/** Porta de domínio (somente leitura) do registro de auditoria {@link LogAcesso} (E-11). */
public interface ILogAcessoRepositorio {

    /**
     * Registros mais recentes, opcionalmente filtrados por resultado
     * (PERMITIDO/NEGADO; nulo/vazio = todos), limitados a {@code limite}.
     */
    List<LogAcesso> listarRecentes(String resultado, int limite);
}
