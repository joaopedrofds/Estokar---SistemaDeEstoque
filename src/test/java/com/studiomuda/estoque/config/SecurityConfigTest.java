package com.studiomuda.estoque.config;

import com.studiomuda.estoque.repository.DevolucaoRepository;
import com.studiomuda.estoque.repository.ItemDevolucaoRepository;
import com.studiomuda.estoque.repository.CreditoClienteRepository;
import com.studiomuda.estoque.repository.CupomRepository;
import com.studiomuda.estoque.service.DevolucaoService;
import com.studiomuda.estoque.service.CupomService;
import com.studiomuda.estoque.dao.PedidoDAO;
import com.studiomuda.estoque.dao.ItemPedidoDAO;
import com.studiomuda.estoque.dao.ClienteDAO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration"
})
@Import(TestSecurityConfiguration.class)
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DevolucaoRepository devolucaoRepository;

    @MockBean
    private ItemDevolucaoRepository itemDevolucaoRepository;

    @MockBean
    private CreditoClienteRepository creditoClienteRepository;

    @MockBean
    private CupomRepository cupomRepository;

    @MockBean
    private DevolucaoService devolucaoService;

    @MockBean
    private CupomService cupomService;

    @MockBean
    private PedidoDAO pedidoDAO;

    @MockBean
    private ItemPedidoDAO itemPedidoDAO;

    @MockBean
    private ClienteDAO clienteDAO;

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