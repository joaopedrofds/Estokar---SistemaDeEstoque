package com.studiomuda.estoque.config;

import com.studiomuda.estoque.security.DatabaseUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final DatabaseUserDetailsService userDetailsService;

    public SecurityConfig(DatabaseUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(userDetailsService)
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/login", "/error", "/erro", "/css/**", "/js/**", "/favicon.ico").permitAll()
                // Devoluções — operador solicita, gestor/admin aprova ou rejeita
                .antMatchers(HttpMethod.POST, "/devolucoes/*/aprovar").hasAnyRole("ADMINISTRADOR", "GERENTE_OPERACIONAL")
                .antMatchers(HttpMethod.POST, "/devolucoes/*/rejeitar").hasAnyRole("ADMINISTRADOR", "GERENTE_OPERACIONAL")
                .antMatchers("/devolucoes/creditos").hasAnyRole("ADMINISTRADOR", "GERENTE_OPERACIONAL")
                .antMatchers("/devolucoes/**").hasAnyRole("ADMINISTRADOR", "GERENTE_OPERACIONAL", "OPERADOR_VENDEDOR")
                .antMatchers("/devolucoes").hasAnyRole("ADMINISTRADOR", "GERENTE_OPERACIONAL", "OPERADOR_VENDEDOR")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedPage("/error");

        return http.build();
    }
}
