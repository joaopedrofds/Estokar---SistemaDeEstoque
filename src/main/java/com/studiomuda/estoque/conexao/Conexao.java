package com.studiomuda.estoque.conexao;

import com.studiomuda.estoque.security.InterceptadorAutorizacaoDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private static final String URL = "jdbc:mysql://localhost:3306/studiomuda";
    private static final String USUARIO = "root";
    private static final String SENHA = "";

    public static Connection getConnection() throws SQLException {
        InterceptadorAutorizacaoDao.validarAcessoPorOrigem();
        return getConnectionSemAutorizacao();
    }

    public static Connection getConnectionSemAutorizacao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
