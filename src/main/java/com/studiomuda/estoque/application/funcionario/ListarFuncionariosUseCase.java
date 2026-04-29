package com.studiomuda.estoque.application.funcionario;

import com.studiomuda.estoque.domain.funcionario.Cargo;
import com.studiomuda.estoque.domain.funcionario.Funcionario;
import com.studiomuda.estoque.domain.funcionario.FuncionarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarFuncionariosUseCase {
    private final FuncionarioRepository funcionarioRepository;

    public ListarFuncionariosUseCase(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.listarTodos();
    }

    public List<Funcionario> buscarComFiltros(String nome, String cargo, String status) {
        Cargo cargoFiltro = (cargo == null || cargo.trim().isEmpty()) ? null
                : Cargo.desdeRotulo(cargo);
        Boolean ativoFiltro = null;
        if (status != null && !status.trim().isEmpty()) {
            if ("ativo".equalsIgnoreCase(status)) ativoFiltro = Boolean.TRUE;
            else if ("inativo".equalsIgnoreCase(status)) ativoFiltro = Boolean.FALSE;
        }
        return funcionarioRepository.buscarComFiltros(nome, cargoFiltro, ativoFiltro);
    }
}
