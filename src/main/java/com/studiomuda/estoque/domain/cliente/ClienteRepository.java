package com.studiomuda.estoque.domain.cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository {

    Cliente salvar(Cliente cliente);

    void atualizar(Cliente cliente);

    Optional<Cliente> buscarPorId(int id);

    Optional<Cliente> buscarPorCpfCnpj(CpfCnpj cpfCnpj);

    List<Cliente> listarTodos();

    List<Cliente> listarAtivos();

    List<Cliente> listarInativos();

    List<Cliente> buscarComFiltros(String nome, TipoPessoa tipo, Boolean ativo);

    void desativar(int id);
}
