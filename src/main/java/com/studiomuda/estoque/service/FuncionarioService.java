package com.studiomuda.estoque.service;

import com.studiomuda.estoque.dao.ClienteDAO;
import com.studiomuda.estoque.jpa.entity.FuncionarioJpaEntity;
import com.studiomuda.estoque.jpa.repository.FuncionarioJpaRepository;
import com.studiomuda.estoque.model.Cliente;
import com.studiomuda.estoque.model.Funcionario;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FuncionarioService {

    private final FuncionarioJpaRepository funcionarioRepository;
    private final ClienteDAO clienteDAO;

    public FuncionarioService(FuncionarioJpaRepository funcionarioRepository, ClienteDAO clienteDAO) {
        this.funcionarioRepository = funcionarioRepository;
        this.clienteDAO = clienteDAO;
    }

    public List<Funcionario> listar() throws SQLException {
        return funcionarioRepository.findAllByOrderByIdAsc().stream()
                .map(this::toModel)
                .toList();
    }

    public Funcionario buscarPorId(int id) throws SQLException {
        return funcionarioRepository.findById(id)
                .map(this::toModel)
                .orElse(null);
    }

    public Funcionario buscarPorCpf(String cpf) throws SQLException {
        if (isBlank(cpf)) {
            return null;
        }
        return funcionarioRepository.findByCpf(cpf)
                .map(this::toModel)
                .orElse(null);
    }

    public boolean cpfDuplicado(String cpfLimpo, int idAtual) throws SQLException {
        Cliente existenteCliente = clienteDAO.buscarPorCpfCnpj(cpfLimpo);
        boolean cpfEmUsoPorOutroFuncionario = idAtual == 0
                ? funcionarioRepository.existsByCpf(cpfLimpo)
                : funcionarioRepository.existsByCpfAndIdNot(cpfLimpo, idAtual);

        return cpfEmUsoPorOutroFuncionario || existenteCliente != null;
    }

    public void inserir(Funcionario funcionario) throws SQLException {
        FuncionarioJpaEntity entity = new FuncionarioJpaEntity();
        applyCamposInsercao(funcionario, entity);
        funcionarioRepository.save(entity);
    }

    public void atualizar(Funcionario funcionario) throws SQLException {
        Optional<FuncionarioJpaEntity> existente = funcionarioRepository.findById(funcionario.getId());
        if (existente.isEmpty()) {
            return;
        }

        FuncionarioJpaEntity entity = existente.get();
        applyCamposAtualizacao(funcionario, entity);
        funcionarioRepository.save(entity);
    }

    public void inativar(int id) throws SQLException {
        Optional<FuncionarioJpaEntity> existente = funcionarioRepository.findById(id);
        if (existente.isPresent()) {
            FuncionarioJpaEntity entity = existente.get();
            entity.setAtivo(false);
            funcionarioRepository.save(entity);
        }
    }

    public Map<String, List<String>> getFiltros() throws SQLException {
        Map<String, List<String>> filtros = new HashMap<>();
        filtros.put("cargos", List.of("Diretor", "Auxiliar", "Estoquista"));
        filtros.put("status", funcionarioRepository.listarStatusDistintos());
        return filtros;
    }

    private void applyCamposInsercao(Funcionario funcionario, FuncionarioJpaEntity entity) {
        entity.setNome(funcionario.getNome());
        entity.setCpf(funcionario.getCpf());
        entity.setCargo(funcionario.getCargo());
        entity.setDataNasc(funcionario.getData_nasc());
        entity.setTelefone(funcionario.getTelefone());
        entity.setCep(funcionario.getCep());
        entity.setRua(funcionario.getRua());
        entity.setNumero(funcionario.getNumero());
        entity.setBairro(funcionario.getBairro());
        entity.setCidade(funcionario.getCidade());
        entity.setEstado(funcionario.getEstado());
        entity.setAtivo(funcionario.isAtivo());
    }

    private void applyCamposAtualizacao(Funcionario funcionario, FuncionarioJpaEntity entity) {
        entity.setNome(funcionario.getNome());
        entity.setCargo(funcionario.getCargo());
        entity.setTelefone(funcionario.getTelefone());
        entity.setCep(funcionario.getCep());
        entity.setRua(funcionario.getRua());
        entity.setNumero(funcionario.getNumero());
        entity.setBairro(funcionario.getBairro());
        entity.setCidade(funcionario.getCidade());
        entity.setEstado(funcionario.getEstado());
        entity.setAtivo(funcionario.isAtivo());
    }

    private Funcionario toModel(FuncionarioJpaEntity entity) {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(entity.getId() != null ? entity.getId() : 0);
        funcionario.setNome(entity.getNome());
        funcionario.setCpf(entity.getCpf());
        funcionario.setCargo(entity.getCargo());
        funcionario.setData_nasc(entity.getDataNasc());
        funcionario.setTelefone(entity.getTelefone());
        funcionario.setCep(entity.getCep());
        funcionario.setRua(entity.getRua());
        funcionario.setNumero(entity.getNumero());
        funcionario.setBairro(entity.getBairro());
        funcionario.setCidade(entity.getCidade());
        funcionario.setEstado(entity.getEstado());
        funcionario.setAtivo(Boolean.TRUE.equals(entity.getAtivo()));
        return funcionario;
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
