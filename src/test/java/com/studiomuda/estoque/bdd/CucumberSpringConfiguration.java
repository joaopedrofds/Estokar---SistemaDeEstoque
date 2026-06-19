package com.studiomuda.estoque.bdd;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
@org.springframework.boot.test.mock.mockito.MockBean(classes = {
        com.studiomuda.estoque.service.CupomService.class,
        com.studiomuda.estoque.repository.CupomRepository.class,
        com.studiomuda.estoque.repository.DevolucaoRepository.class,
        com.studiomuda.estoque.repository.ItemDevolucaoRepository.class,
        com.studiomuda.estoque.repository.CreditoClienteRepository.class,
        com.studiomuda.estoque.service.DevolucaoService.class,
        com.studiomuda.estoque.repository.ProdutoRepository.class,
        com.studiomuda.estoque.repository.HistoricoPrecoRepository.class,
        com.studiomuda.estoque.repository.ItemPedidoRepository.class
})
public class CucumberSpringConfiguration {
}
