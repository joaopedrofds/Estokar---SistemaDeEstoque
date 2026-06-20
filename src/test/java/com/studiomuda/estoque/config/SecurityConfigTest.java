package com.studiomuda.estoque.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Sobe o contexto completo (web + JPA) contra o banco H2 em memória definido em
 * src/test/resources/application.properties. Não excluímos a auto-configuração de
 * JPA: como a aplicação declara @EnableJpaRepositories, todos os repositories precisam
 * de um EntityManagerFactory real — fornecido pelo H2.
 */
@SpringBootTest
@Import(TestSecurityConfiguration.class)
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRedirecionarParaLoginQuandoNaoAutenticado() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "DIRETOR")
    void devePermitirAcessoDashboardParaDiretor() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ESTOQUISTA")
    void deveBloquearAcessoDashboardParaEstoquista() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "AUXILIAR")
    void deveBloquearAcessoCuponsParaAuxiliar() throws Exception {
        mockMvc.perform(get("/cupons"))
                .andExpect(status().isForbidden());
    }
}
