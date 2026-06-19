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
        UserDetails diretor = User.withUsername("diretor")
                .password("{noop}Diretor@123")
                .roles("DIRETOR")
                .build();

        UserDetails auxiliar = User.withUsername("auxiliar")
                .password("{noop}Auxiliar@123")
                .roles("AUXILIAR")
                .build();

        UserDetails estoquista = User.withUsername("estoquista")
                .password("{noop}Estoque@123")
                .roles("ESTOQUISTA")
                .build();

        return new InMemoryUserDetailsManager(diretor, auxiliar, estoquista);
    }
}
