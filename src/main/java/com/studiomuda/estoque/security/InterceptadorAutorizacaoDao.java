package com.studiomuda.estoque.security;

import com.studiomuda.estoque.conexao.Conexao;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class InterceptadorAutorizacaoDao {
    private static final String MENSAGEM_ACESSO_NEGADO = "Acesso insuficiente para esta operação.";

    private InterceptadorAutorizacaoDao() {
    }

    public static void validarAcessoPorOrigem() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken
                || !(authentication.getPrincipal() instanceof UsuarioAutenticado)) {
            return;
        }

        UsuarioAutenticado usuario = (UsuarioAutenticado) authentication.getPrincipal();
        if (usuario.getPerfilIds().isEmpty()) {
            throw new AcessoNegadoException(MENSAGEM_ACESSO_NEGADO);
        }

        StackTraceElement origem = encontrarOrigem();
        if (origem == null) {
            return;
        }

        RecursoAcesso recurso = mapearRecurso(origem.getClassName());
        OperacaoAcesso operacao = mapearOperacao(origem.getMethodName());
        if (recurso == null || operacao == null) {
            return;
        }

        String detalhe = origem.getClassName() + "#" + origem.getMethodName();
        boolean permitido = possuiPermissao(usuario.getPerfilIds(), recurso, operacao);
        registrarTentativa(usuario.getId(), usuario.getUsername(), recurso, operacao, permitido, detalhe);
        if (!permitido) {
            throw new AcessoNegadoException(MENSAGEM_ACESSO_NEGADO);
        }
    }

    private static StackTraceElement encontrarOrigem() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        StackTraceElement ultimaController = null;
        for (StackTraceElement frame : stack) {
            String classe = frame.getClassName();
            if (!classe.startsWith("com.studiomuda.estoque.")) {
                continue;
            }
            if (classe.startsWith("com.studiomuda.estoque.security.")
                    || classe.equals("com.studiomuda.estoque.conexao.Conexao")) {
                continue;
            }
            if (classe.contains(".controller.")) {
                ultimaController = frame;
            }
            if (classe.contains(".service.")) {
                // Se já encontramos um controller, use o controller; senão, use o service
                if (ultimaController != null) {
                    return ultimaController;
                }
            }
            if (classe.contains(".dao.")) {
                // Se já encontramos um controller ou service, use eles; senão, use o DAO
                if (ultimaController != null) {
                    return ultimaController;
                }
            }
        }
        return ultimaController;
    }

    private static RecursoAcesso mapearRecurso(String nomeClasseCompleto) {
        String nomeClasse = nomeClasseCompleto.substring(nomeClasseCompleto.lastIndexOf('.') + 1);
        if (nomeClasse.contains("Produto")) {
            return RecursoAcesso.PRODUTO;
        }
        if (nomeClasse.contains("Cupom")) {
            return RecursoAcesso.CUPOM;
        }
        if (nomeClasse.contains("Pedido") || nomeClasse.contains("ItemPedido")) {
            return RecursoAcesso.PEDIDO;
        }
        if (nomeClasse.contains("Estoque") || nomeClasse.contains("Movimentacao")) {
            return RecursoAcesso.ESTOQUE;
        }
        if (nomeClasse.contains("Suprimento")) {
            return RecursoAcesso.SUPRIMENTO;
        }
        if (nomeClasse.contains("Remessa") || nomeClasse.contains("Doca") || nomeClasse.contains("Distribuidora")) {
            return RecursoAcesso.REMESSA;
        }
        if (nomeClasse.contains("Cliente")) {
            return RecursoAcesso.CLIENTE;
        }
        if (nomeClasse.contains("Funcionario")) {
            return RecursoAcesso.FUNCIONARIO;
        }
        if (nomeClasse.contains("Dashboard")) {
            return RecursoAcesso.DASHBOARD;
        }
        if (nomeClasse.contains("Kpi")) {
            return RecursoAcesso.KPI;
        }
        if (nomeClasse.contains("Devolucao")) {
            return RecursoAcesso.DEVOLUCAO;
        }
        if (nomeClasse.contains("Financeiro")
                || nomeClasse.contains("Relatorio")
                || nomeClasse.contains("CategoriaFinanceira")
                || nomeClasse.contains("TemplateRelatorio")
                || nomeClasse.contains("LancamentoAjuste")) {
            return RecursoAcesso.FINANCEIRO;
        }
        if (nomeClasse.contains("Acesso")
                || nomeClasse.contains("Seguranca")
                || nomeClasse.contains("Security")
                || nomeClasse.contains("Perfil")
                || nomeClasse.contains("Permissao")) {
            return RecursoAcesso.ACESSO;
        }
        if (nomeClasse.contains("Home")) {
            return RecursoAcesso.HOME;
        }
        return RecursoAcesso.API;
    }

    private static OperacaoAcesso mapearOperacao(String nomeMetodo) {
        String metodo = nomeMetodo.toLowerCase(Locale.ROOT);
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
        if (metodo.startsWith("listar")
                || metodo.startsWith("buscar")
                || metodo.startsWith("obter")
                || metodo.startsWith("consultar")
                || metodo.startsWith("calcular")
                || metodo.startsWith("somar")
                || metodo.startsWith("existe")
                || metodo.startsWith("verificar")
                || metodo.startsWith("dashboard")
                || metodo.startsWith("get")
                || metodo.startsWith("count")
                || metodo.startsWith("contar")
                || metodo.startsWith("form")) {
            return OperacaoAcesso.LEITURA;
        }
        return OperacaoAcesso.LEITURA;
    }

    private static boolean possuiPermissao(List<Integer> perfilIds, RecursoAcesso recurso, OperacaoAcesso operacao) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) " +
                        "FROM permissao_perfil " +
                        "WHERE perfil_id IN (");

        for (int i = 0; i < perfilIds.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("?");
        }
        sql.append(") AND recurso = ? AND operacao IN (");

        List<String> operacoesPermitidas = operacoesAceitas(operacao);
        for (int i = 0; i < operacoesPermitidas.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("?");
        }
        sql.append(") AND permitido = TRUE");

        try (Connection conn = Conexao.getConnectionSemAutorizacao();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int indice = 1;
            for (Integer perfilId : perfilIds) {
                stmt.setInt(indice++, perfilId);
            }
            stmt.setString(indice++, recurso.name());
            for (String operacaoPermitida : operacoesPermitidas) {
                stmt.setString(indice++, operacaoPermitida);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new AcessoNegadoException(MENSAGEM_ACESSO_NEGADO);
        }
    }

    private static List<String> operacoesAceitas(OperacaoAcesso operacao) {
        if (operacao == OperacaoAcesso.LEITURA) {
            return Arrays.asList(OperacaoAcesso.LEITURA.name());
        }
        if (operacao == OperacaoAcesso.ESCRITA) {
            return Arrays.asList(OperacaoAcesso.ESCRITA.name());
        }
        return Arrays.asList(OperacaoAcesso.APROVACAO.name());
    }

    private static void registrarTentativa(Integer usuarioId,
                                           String username,
                                           RecursoAcesso recurso,
                                           OperacaoAcesso operacao,
                                           boolean permitido,
                                           String detalhe) {
        String sql = "INSERT INTO log_acesso (usuario_id, username, recurso, operacao, resultado, detalhe) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnectionSemAutorizacao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (usuarioId != null && usuarioId > 0) {
                stmt.setInt(1, usuarioId);
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }
            stmt.setString(2, username);
            stmt.setString(3, recurso.name());
            stmt.setString(4, operacao.name());
            stmt.setString(5, permitido ? "PERMITIDO" : "NEGADO");
            stmt.setString(6, detalhe);
            stmt.executeUpdate();
        } catch (SQLException ignored) {
            // Falha de log não deve bloquear o fluxo principal.
        }
    }
}
