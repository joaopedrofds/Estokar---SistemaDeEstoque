package com.studiomuda.estoque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.studiomuda.estoque.repository")
@ComponentScan(basePackages = {"com.studiomuda.estoque.repository", "com.studiomuda.estoque.dao", "com.studiomuda.estoque.service", "com.studiomuda.estoque.controller", "com.studiomuda.estoque.config", "com.studiomuda.estoque.security", "com.studiomuda.estoque.observer", "com.studiomuda.estoque.strategy"})
public class EstoqueApplication {

	public static void main(String[] args) {
		SpringApplication.run(EstoqueApplication.class, args);
	}

}
