package com.studiomuda.estoque.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                .antMatchers("/dashboard/**").hasAnyRole(
                        "DIRETOR", "AUXILIAR",
                        "ADMINISTRADOR", "GERENTE_OPERACIONAL", "OPERADOR_VENDEDOR")
                .antMatchers("/cupons/**").hasAnyRole(
                        "DIRETOR",
                        "ADMINISTRADOR", "GERENTE_OPERACIONAL", "OPERADOR_VENDEDOR")
                .antMatchers("/pedidos/**").hasAnyRole(
                        "DIRETOR",
                        "ADMINISTRADOR", "GERENTE_OPERACIONAL", "OPERADOR_VENDEDOR")
                .antMatchers("/cobrancas/**").hasAnyRole(
                        "DIRETOR",
                        "ADMINISTRADOR", "GERENTE_OPERACIONAL")
                .antMatchers("/estoque/**").hasAnyRole(
                        "DIRETOR", "ESTOQUISTA",
                        "ADMINISTRADOR", "GERENTE_OPERACIONAL", "OPERADOR_VENDEDOR")
                .antMatchers("/inventarios/**").hasAnyRole(
                        "DIRETOR", "ESTOQUISTA", "AUXILIAR",
                        "ADMINISTRADOR", "GERENTE_OPERACIONAL", "OPERADOR_VENDEDOR")
                .antMatchers("/ajustes-estoque/**").hasAnyRole(
                        "DIRETOR", "ESTOQUISTA",
                        "ADMINISTRADOR", "GERENTE_OPERACIONAL", "OPERADOR_VENDEDOR")
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
