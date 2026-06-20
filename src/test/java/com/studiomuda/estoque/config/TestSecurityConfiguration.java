package com.studiomuda.estoque.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class TestSecurityConfiguration {

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withUsername("admin")
                .password("{noop}Admin@123")
                .roles("ADMINISTRADOR")
                .build();

        UserDetails gerente = User.withUsername("gerente")
                .password("{noop}Gerente@123")
                .roles("GERENTE_OPERACIONAL")
                .build();

        UserDetails operador = User.withUsername("operador")
                .password("{noop}Operador@123")
                .roles("OPERADOR_VENDEDOR")
                .build();

        return new InMemoryUserDetailsManager(admin, gerente, operador);
    }
}
