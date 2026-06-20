package com.studiomuda.estoque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {
        "com.studiomuda.estoque.model",
        "com.studiomuda.estoque.jpa.entity",
        "com.studiomuda.estoque.precificacao.infrastructure.persistence.entity"
})
@EnableJpaRepositories(basePackages = {
        "com.studiomuda.estoque.repository",
        "com.studiomuda.estoque.jpa.repository",
        "com.studiomuda.estoque.precificacao.infrastructure.persistence.repository"
})
@ComponentScan(basePackages = {
        "com.studiomuda.estoque.repository",
        "com.studiomuda.estoque.dao",
        "com.studiomuda.estoque.service",
        "com.studiomuda.estoque.controller",
        "com.studiomuda.estoque.config",
        "com.studiomuda.estoque.security",
        "com.studiomuda.estoque.observer",
        "com.studiomuda.estoque.strategy",
        "com.studiomuda.estoque.proxy",
        "com.studiomuda.estoque.precificacao"
})
public class EstoqueApplication {

	public static void main(String[] args) {
		SpringApplication.run(EstoqueApplication.class, args);
	}

}
