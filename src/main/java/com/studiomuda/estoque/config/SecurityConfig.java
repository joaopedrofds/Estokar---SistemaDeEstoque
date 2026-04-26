package com.studiomuda.estoque.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails diretor = User.builder()
                .username("diretor")
                .password(passwordEncoder.encode("Diretor@123"))
                .roles("DIRETOR")
                .build();

        UserDetails auxiliar = User.builder()
                .username("auxiliar")
                .password(passwordEncoder.encode("Auxiliar@123"))
                .roles("AUXILIAR")
                .build();

        UserDetails estoquista = User.builder()
                .username("estoquista")
                .password(passwordEncoder.encode("Estoque@123"))
                .roles("ESTOQUISTA")
                .build();

        return new InMemoryUserDetailsManager(diretor, auxiliar, estoquista);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/login", "/error", "/erro", "/css/**", "/js/**", "/favicon.ico").permitAll()
                .antMatchers("/funcionarios/**", "/cupons/**").hasRole("DIRETOR")
                .antMatchers("/dashboard/**", "/clientes/**", "/pedidos/**").hasAnyRole("DIRETOR", "AUXILIAR")
                .antMatchers("/estoque/**").hasAnyRole("DIRETOR", "ESTOQUISTA")
                .antMatchers("/produtos/**").hasAnyRole("DIRETOR", "AUXILIAR", "ESTOQUISTA")
                .antMatchers("/api/kpis/recalcular").hasRole("DIRETOR")
                .antMatchers("/api/**").authenticated()
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
