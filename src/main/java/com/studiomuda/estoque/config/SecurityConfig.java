package com.studiomuda.estoque.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
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
                // Precificação Dinâmica
                .antMatchers("/precificacao/**").hasAnyRole("ADMINISTRADOR", "GERENTE_OPERACIONAL")
                // Cupons uso
                .antMatchers("/cupons/uso/**").hasAnyRole("ADMINISTRADOR", "GERENTE_OPERACIONAL", "OPERADOR_VENDEDOR")
                // Histórico de Preços
                .antMatchers("/produtos/historico/**").hasAnyRole("ADMINISTRADOR", "GERENTE_OPERACIONAL")
                // Dashboard
                .antMatchers("/dashboard/**").hasAnyRole(
                        "DIRETOR", "AUXILIAR",
                        "ADMINISTRADOR", "GERENTE_OPERACIONAL", "OPERADOR_VENDEDOR")
                // Cupons
                .antMatchers("/cupons/**").hasAnyRole(
                        "DIRETOR",
                        "ADMINISTRADOR", "GERENTE_OPERACIONAL", "OPERADOR_VENDEDOR")
                // Pedidos
                .antMatchers("/pedidos/**").hasAnyRole(
                        "DIRETOR",
                        "ADMINISTRADOR", "GERENTE_OPERACIONAL", "OPERADOR_VENDEDOR")
                // Estoque
                .antMatchers("/estoque/**").hasAnyRole(
                        "DIRETOR", "ESTOQUISTA",
                        "ADMINISTRADOR", "GERENTE_OPERACIONAL", "OPERADOR_VENDEDOR")
                // Inventários
                .antMatchers("/inventarios/**").hasAnyRole(
                        "DIRETOR", "ESTOQUISTA", "AUXILIAR",
                        "ADMINISTRADOR", "GERENTE_OPERACIONAL", "OPERADOR_VENDEDOR")
                // Ajustes de Estoque
                .antMatchers("/ajustes-estoque/**").hasAnyRole(
                        "DIRETOR", "ESTOQUISTA",
                        "ADMINISTRADOR", "GERENTE_OPERACIONAL", "OPERADOR_VENDEDOR")
                // Funcionários
                .antMatchers("/funcionarios/**").hasAnyRole(
                        "DIRETOR",
                        "ADMINISTRADOR", "GERENTE_OPERACIONAL")
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
                .accessDeniedHandler((request, response, accessDeniedException) ->
                        response.sendError(HttpServletResponse.SC_FORBIDDEN));

        return http.build();
    }
}