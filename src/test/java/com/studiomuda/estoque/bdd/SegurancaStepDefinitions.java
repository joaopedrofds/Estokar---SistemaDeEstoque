package com.studiomuda.estoque.bdd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public class SegurancaStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    private MvcResult resultado;
    private String papel;

    @Dado("que nao estou autenticado")
    public void queNaoEstouAutenticado() {
        papel = null;
    }

    @Dado("que estou autenticado com o papel {string}")
    public void queEstouAutenticadoComOPapel(String papel) {
        this.papel = papel;
    }

    @Quando("acesso a rota {string}")
    public void acessoARota(String rota) throws Exception {
        MockHttpServletRequestBuilder request = get(rota);
        if (papel != null) {
            request.with(user(papel.toLowerCase()).roles(papel));
        }
        resultado = mockMvc.perform(request).andReturn();
    }

    @Quando("consulto a permissao da rota {string}")
    public void consultoAPermissaoDaRota(String rota) throws Exception {
        MockHttpServletRequestBuilder request = options(rota);
        if (papel != null) {
            request.with(user(papel.toLowerCase()).roles(papel));
        }
        resultado = mockMvc.perform(request).andReturn();
    }

    @Quando("envio login com usuario {string} e senha {string}")
    public void envioLoginComUsuarioESenha(String usuario, String senha) throws Exception {
        resultado = mockMvc.perform(post("/login")
                        .param("username", usuario)
                        .param("password", senha))
                .andReturn();
    }

    @Entao("sou redirecionado para {string}")
    public void souRedirecionadoPara(String destino) {
        assertThat(resultado.getResponse().getStatus(), equalTo(302));
        assertThat(resultado.getResponse().getRedirectedUrl(), equalTo(destino));
    }

    @Entao("sou redirecionado para a pagina de login")
    public void souRedirecionadoParaAPaginaDeLogin() {
        assertThat(resultado.getResponse().getStatus(), equalTo(302));
        assertThat(resultado.getResponse().getRedirectedUrl(), containsString("/login"));
    }

    @Entao("o acesso e permitido")
    public void oAcessoEPermitido() {
        assertThat(resultado.getResponse().getStatus(), equalTo(200));
    }

    @Entao("o acesso e bloqueado")
    public void oAcessoEBloqueado() {
        assertThat(resultado.getResponse().getStatus(), equalTo(403));
    }

}
