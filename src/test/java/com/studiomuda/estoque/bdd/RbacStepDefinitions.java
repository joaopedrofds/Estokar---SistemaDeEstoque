package com.studiomuda.estoque.bdd;

import com.studiomuda.estoque.security.OperacaoAcesso;
import com.studiomuda.estoque.security.RecursoAcesso;
import com.studiomuda.estoque.security.dominio.PoliticaDeAcesso;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Steps do RBAC fino (E-11). Exercitam a {@link PoliticaDeAcesso} de dominio puro,
 * que espelha a decisao da matriz {@code permissao_perfil} e a inferencia de
 * operacao do {@code InterceptadorAutorizacaoDao}, sem banco.
 */
public class RbacStepDefinitions {

    private PoliticaDeAcesso politica;
    private boolean acessoConcedido;
    private OperacaoAcesso operacaoInferida;

    @Dado("a matriz de permissoes:")
    public void aMatrizDePermissoes(DataTable tabela) {
        List<PoliticaDeAcesso.Permissao> permissoes = new ArrayList<>();
        for (Map<String, String> linha : tabela.asMaps()) {
            permissoes.add(new PoliticaDeAcesso.Permissao(
                    Integer.parseInt(linha.get("perfil")),
                    RecursoAcesso.valueOf(linha.get("recurso")),
                    OperacaoAcesso.valueOf(linha.get("operacao")),
                    Boolean.parseBoolean(linha.get("permitido"))));
        }
        politica = new PoliticaDeAcesso(permissoes);
    }

    @Quando("^o perfil (\\d+) solicita \"([^\"]+)\" sobre \"([^\"]+)\"$")
    public void oPerfilSolicita(int perfil, String operacao, String recurso) {
        acessoConcedido = politica.permite(
                Collections.singletonList(perfil),
                RecursoAcesso.valueOf(recurso),
                OperacaoAcesso.valueOf(operacao));
    }

    @Quando("^o metodo \"([^\"]+)\" e interceptado$")
    public void oMetodoEInterceptado(String metodo) {
        operacaoInferida = PoliticaDeAcesso.mapearOperacao(metodo);
    }

    @Então("o acesso e concedido")
    public void oAcessoEConcedido() {
        assertTrue(acessoConcedido, "Esperava que o acesso fosse concedido");
    }

    @Então("o acesso e negado")
    public void oAcessoENegado() {
        assertFalse(acessoConcedido, "Esperava que o acesso fosse negado");
    }

    @Então("^a operacao inferida e \"([^\"]+)\"$")
    public void aOperacaoInferidaE(String esperada) {
        assertEquals(OperacaoAcesso.valueOf(esperada), operacaoInferida);
    }
}
