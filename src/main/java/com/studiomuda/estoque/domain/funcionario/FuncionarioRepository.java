package com.studiomuda.estoque.domain.funcionario;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository {

    Funcionario salvar(Funcionario funcionario);

    void atualizar(Funcionario funcionario);

    Optional<Funcionario> buscarPorId(int id);

    Optional<Funcionario> buscarPorCpf(Cpf cpf);

    List<Funcionario> listarTodos();

    List<Funcionario> buscarComFiltros(String nome, Cargo cargo, Boolean ativo);

    void desativar(int id);
}
