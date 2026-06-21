package com.studiomuda.estoque.security.dominio;

import com.studiomuda.estoque.security.OperacaoAcesso;
import com.studiomuda.estoque.security.RecursoAcesso;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Matriz de controle de acesso por perfil (E-11), em Java puro.
 *
 * <p>Concentra duas regras que vivem no {@code InterceptadorAutorizacaoDao}:
 * a decisão "perfil × recurso × operação" (espelho da consulta a
 * {@code permissao_perfil}) e a inferência da operação a partir do nome do
 * método interceptado. Mantê-las aqui, sem JDBC, torna o RBAC fino testável
 * isoladamente.</p>
 */
public class PoliticaDeAcesso {

    /** Concessão (ou negação) de uma operação sobre um recurso para um perfil. */
    public static final class Permissao {
        private final int perfilId;
        private final RecursoAcesso recurso;
        private final OperacaoAcesso operacao;
        private final boolean permitido;

        public Permissao(int perfilId, RecursoAcesso recurso, OperacaoAcesso operacao, boolean permitido) {
            if (recurso == null) {
                throw new IllegalArgumentException("Recurso da permissão não pode ser nulo.");
            }
            if (operacao == null) {
                throw new IllegalArgumentException("Operação da permissão não pode ser nula.");
            }
            this.perfilId = perfilId;
            this.recurso = recurso;
            this.operacao = operacao;
            this.permitido = permitido;
        }

        public int getPerfilId() { return perfilId; }
        public RecursoAcesso getRecurso() { return recurso; }
        public OperacaoAcesso getOperacao() { return operacao; }
        public boolean isPermitido() { return permitido; }
    }

    private final List<Permissao> permissoes;

    public PoliticaDeAcesso(List<Permissao> permissoes) {
        Objects.requireNonNull(permissoes, "A lista de permissões não pode ser nula.");
        this.permissoes = Collections.unmodifiableList(List.copyOf(permissoes));
    }

    /**
     * Indica se algum dos perfis informados possui concessão explícita
     * ({@code permitido = true}) para a operação sobre o recurso.
     *
     * <p>Espelha exatamente a consulta de {@code InterceptadorAutorizacaoDao}:
     * exige correspondência exata de recurso e operação (sem hierarquia entre
     * leitura/escrita/aprovação).</p>
     */
    public boolean permite(Collection<Integer> perfis, RecursoAcesso recurso, OperacaoAcesso operacao) {
        if (perfis == null || perfis.isEmpty() || recurso == null || operacao == null) {
            return false;
        }
        return permissoes.stream().anyMatch(p ->
                p.permitido
                        && perfis.contains(p.perfilId)
                        && p.recurso == recurso
                        && p.operacao == operacao);
    }

    /**
     * Consolida as permissões concedidas em um mapa {@code recurso → {operações}}
     * (apenas linhas com {@code permitido = true}), usado pela tela de perfis.
     *
     * <p>Concentra o fold que vivia em {@code PermissaoPerfilDAO.carregarMapaPermissoes}:
     * regra de apresentação da matriz, agora em domínio puro e testável. As chaves
     * usam {@code enum.name()} para casar com os campos do formulário Thymeleaf.</p>
     */
    public static Map<String, Set<String>> montarMapaPermitidas(Collection<Permissao> permissoes) {
        Map<String, Set<String>> mapa = new HashMap<>();
        if (permissoes == null) {
            return mapa;
        }
        for (Permissao p : permissoes) {
            if (!p.isPermitido()) {
                continue;
            }
            mapa.computeIfAbsent(p.getRecurso().name(), chave -> new HashSet<>())
                    .add(p.getOperacao().name());
        }
        return mapa;
    }

    /**
     * Infere a operação de acesso a partir do nome do método de DAO/serviço
     * interceptado. Espelho da regra usada no interceptor de autorização.
     */
    public static OperacaoAcesso mapearOperacao(String nomeMetodo) {
        String metodo = nomeMetodo == null ? "" : nomeMetodo.toLowerCase(Locale.ROOT);
        if (metodo.contains("aprovar")
                || metodo.contains("rejeitar")
                || metodo.contains("alterarstatus")) {
            return OperacaoAcesso.APROVACAO;
        }
        if (metodo.startsWith("inserir")
                || metodo.startsWith("salvar")
                || metodo.startsWith("atualizar")
                || metodo.startsWith("deletar")
                || metodo.startsWith("registrar")
                || metodo.startsWith("criar")
                || metodo.startsWith("cadastrar")
                || metodo.startsWith("agendar")
                || metodo.startsWith("bloquear")
                || metodo.startsWith("adicionar")
                || metodo.startsWith("remover")
                || metodo.startsWith("excluir")
                || metodo.startsWith("gerar")) {
            return OperacaoAcesso.ESCRITA;
        }
        // Demais prefixos de consulta (listar, buscar, obter, get, ...) e o
        // padrão de segurança recaem em LEITURA.
        return OperacaoAcesso.LEITURA;
    }
}
