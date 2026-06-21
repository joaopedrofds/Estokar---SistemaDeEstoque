package com.studiomuda.estoque.conexao;

import com.studiomuda.estoque.security.InterceptadorAutorizacaoDao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Conexao {
    private static final String URL_PADRAO = "jdbc:mysql://localhost:3306/studiomuda";
    private static final String USUARIO_PADRAO = "root";
    private static final String SENHA_PADRAO = "";

    private static final ConfiguracaoBanco CONFIGURACAO_BANCO = carregarConfiguracaoBanco();

    public static Connection getConnection() throws SQLException {
        InterceptadorAutorizacaoDao.validarAcessoPorOrigem();
        return getConnectionSemAutorizacao();
    }

    public static Connection getConnectionSemAutorizacao() throws SQLException {
        return DriverManager.getConnection(
                CONFIGURACAO_BANCO.url,
                CONFIGURACAO_BANCO.usuario,
                CONFIGURACAO_BANCO.senha
        );
    }

    private static ConfiguracaoBanco carregarConfiguracaoBanco() {
        Properties properties = carregarApplicationProperties();

        String url = obterConfiguracao(
                "SPRING_DATASOURCE_URL",
                "spring.datasource.url",
                URL_PADRAO,
                properties
        );

        String usuario = obterConfiguracao(
                "SPRING_DATASOURCE_USERNAME",
                "spring.datasource.username",
                USUARIO_PADRAO,
                properties
        );

        String senha = obterConfiguracao(
                "SPRING_DATASOURCE_PASSWORD",
                "spring.datasource.password",
                SENHA_PADRAO,
                properties
        );

        return new ConfiguracaoBanco(url, usuario, senha);
    }

    private static Properties carregarApplicationProperties() {
        Properties properties = new Properties();

        try (InputStream input = Conexao.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException exception) {
            System.err.println("Nao foi possivel carregar application.properties: " + exception.getMessage());
        }

        return properties;
    }

    private static String obterConfiguracao(String envKey, String propertyKey, String fallbackValue, Properties properties) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue.trim();
        }

        String systemPropertyValue = System.getProperty(propertyKey);
        if (systemPropertyValue != null && !systemPropertyValue.trim().isEmpty()) {
            return systemPropertyValue.trim();
        }

        String propertyFileValue = properties.getProperty(propertyKey);
        if (propertyFileValue != null) {
            return resolverPlaceholder(propertyFileValue.trim());
        }

        return fallbackValue;
    }

    private static String resolverPlaceholder(String valor) {
        if (!valor.startsWith("${") || !valor.endsWith("}")) {
            return valor;
        }
        String conteudo = valor.substring(2, valor.length() - 1);
        int separador = conteudo.indexOf(':');
        String chave = separador >= 0 ? conteudo.substring(0, separador) : conteudo;
        String padrao = separador >= 0 ? conteudo.substring(separador + 1) : "";
        String ambiente = System.getenv(chave);
        return ambiente != null ? ambiente : padrao;
    }

    private static class ConfiguracaoBanco {
        private final String url;
        private final String usuario;
        private final String senha;

        private ConfiguracaoBanco(String url, String usuario, String senha) {
            this.url = url;
            this.usuario = usuario;
            this.senha = senha;
        }
    }
}
