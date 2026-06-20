package com.studiomuda.estoque.bdd;

import com.studiomuda.estoque.config.TestSecurityConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import io.cucumber.spring.CucumberContextConfiguration;

/**
 * Configuração do contexto Spring para os cenários BDD (Cucumber).
 *
 * O contexto sobe completo (web + JPA) contra o banco H2 em memória definido em
 * src/test/resources/application.properties (ddl-auto=create-drop). Não excluímos
 * a auto-configuração de JPA: como a aplicação declara @EnableJpaRepositories, todos
 * os repositories precisam de um EntityManagerFactory real — fornecido pelo H2.
 */
@CucumberContextConfiguration
@SpringBootTest
@Import(TestSecurityConfiguration.class)
@AutoConfigureMockMvc
public class CucumberSpringConfiguration {
}
