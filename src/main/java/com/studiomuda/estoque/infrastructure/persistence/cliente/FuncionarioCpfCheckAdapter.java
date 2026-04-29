package com.studiomuda.estoque.infrastructure.persistence.cliente;

import com.studiomuda.estoque.application.cliente.ports.FuncionarioCpfCheckPort;
import com.studiomuda.estoque.dao.FuncionarioDAO;
import com.studiomuda.estoque.domain.cliente.CpfCnpj;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class FuncionarioCpfCheckAdapter implements FuncionarioCpfCheckPort {
    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAO();

    @Override
    public boolean existeFuncionarioComCpf(CpfCnpj cpfCnpj) {
        try {
            return funcionarioDAO.buscarPorCpf(cpfCnpj.digitos()) != null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar CPF de funcionário: " + e.getMessage(), e);
        }
    }
}
