package com.studiomuda.estoque.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class LoggingConfig {
    
    @EventListener(ApplicationReadyEvent.class)
    public void configureLoggingOnStartup() {
        try {
            // Criar diretório de logs se não existir
            Path logsPath = Paths.get("logs");
            if (!Files.exists(logsPath)) {
                Files.createDirectories(logsPath);
                System.out.println("Diretório de logs criado em: " + logsPath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Erro ao criar diretório de logs: " + e.getMessage());
        }
    }
}
