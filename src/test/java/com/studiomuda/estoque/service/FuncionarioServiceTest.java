package com.studiomuda.estoque.service;

import com.studiomuda.estoque.dao.ClienteDAO;
import com.studiomuda.estoque.jpa.entity.FuncionarioJpaEntity;
import com.studiomuda.estoque.jpa.repository.FuncionarioJpaRepository;
import com.studiomuda.estoque.model.Cliente;
import com.studiomuda.estoque.model.Funcionario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FuncionarioServiceTest {

    private FuncionarioJpaRepository funcionarioRepository;
    private ClienteDAO clienteDAO;
    private FuncionarioService service;

    @BeforeEach
    void setUp() {
        funcionarioRepository = mock(FuncionarioJpaRepository.class);
        clienteDAO = mock(ClienteDAO.class);
        service = new FuncionarioService(funcionarioRepository, clienteDAO);
    }

    @Test
    void listarConverteEntidadesParaModel() throws Exception {
        FuncionarioJpaEntity entity = entity(1, "Ana", "12345678901");
        when(funcionarioRepository.findAllByOrderByIdAsc()).thenReturn(List.of(entity));

        List<Funcionario> funcionarios = service.listar();

        assertEquals(1, funcionarios.size());
        assertEquals("Ana", funcionarios.get(0).getNome());
        assertEquals("12345678901", funcionarios.get(0).getCpf());
    }

    @Test
    void buscarPorIdRetornaNullQuandoNaoExiste() throws Exception {
        when(funcionarioRepository.findById(99)).thenReturn(Optional.empty());

        Funcionario funcionario = service.buscarPorId(99);

        assertNull(funcionario);
    }

    @Test
    void cpfDuplicadoConsideraClienteExistente() throws Exception {
        Cliente cliente = new Cliente();
        when(clienteDAO.buscarPorCpfCnpj("12345678901")).thenReturn(cliente);
        when(funcionarioRepository.existsByCpf("12345678901")).thenReturn(false);

        boolean duplicado = service.cpfDuplicado("12345678901", 0);

        assertTrue(duplicado);
    }

    @Test
    void cpfDuplicadoIgnoraProprioRegistroNaEdicao() throws Exception {
        when(clienteDAO.buscarPorCpfCnpj("12345678901")).thenReturn(null);
        when(funcionarioRepository.existsByCpfAndIdNot("12345678901", 7)).thenReturn(false);

        boolean duplicado = service.cpfDuplicado("12345678901", 7);

        assertFalse(duplicado);
    }

    @Test
    void inserirPersisteCpfEDataNascimento() throws Exception {
        Funcionario funcionario = funcionario();

        service.inserir(funcionario);

        verify(funcionarioRepository).save(any(FuncionarioJpaEntity.class));
    }

    @Test
    void atualizarPreservaCpfOriginalComoNoLegado() throws Exception {
        FuncionarioJpaEntity existente = entity(7, "Maria", "99999999999");
        existente.setDataNasc(Date.valueOf("1990-01-01"));
        when(funcionarioRepository.findById(7)).thenReturn(Optional.of(existente));

        Funcionario atualizado = funcionario();
        atualizado.setId(7);
        atualizado.setNome("Maria Atualizada");
        atualizado.setCpf("12345678901");

        service.atualizar(atualizado);

        assertEquals("99999999999", existente.getCpf());
        assertEquals(Date.valueOf("1990-01-01"), existente.getDataNasc());
        assertEquals("Maria Atualizada", existente.getNome());
        verify(funcionarioRepository).save(existente);
    }

    @Test
    void inativarMarcaFuncionarioComoInativo() throws Exception {
        FuncionarioJpaEntity existente = entity(5, "João", "12345678901");
        when(funcionarioRepository.findById(5)).thenReturn(Optional.of(existente));

        service.inativar(5);

        assertFalse(Boolean.TRUE.equals(existente.getAtivo()));
        verify(funcionarioRepository).save(existente);
    }

    @Test
    void buscarPorCpfRetornaFuncionarioConvertido() throws Exception {
        FuncionarioJpaEntity existente = entity(3, "Bruna", "12345678901");
        when(funcionarioRepository.findByCpf("12345678901")).thenReturn(Optional.of(existente));

        Funcionario funcionario = service.buscarPorCpf("12345678901");

        assertNotNull(funcionario);
        assertEquals(3, funcionario.getId());
        assertEquals("Bruna", funcionario.getNome());
    }

    private Funcionario funcionario() {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Carlos");
        funcionario.setCpf("12345678901");
        funcionario.setCargo("Estoquista");
        funcionario.setData_nasc(Date.valueOf("1995-05-20"));
        funcionario.setTelefone("81999999999");
        funcionario.setCep("50000-000");
        funcionario.setRua("Rua A");
        funcionario.setNumero("10");
        funcionario.setBairro("Centro");
        funcionario.setCidade("Recife");
        funcionario.setEstado("PE");
        funcionario.setAtivo(true);
        return funcionario;
    }

    private FuncionarioJpaEntity entity(int id, String nome, String cpf) {
        FuncionarioJpaEntity entity = new FuncionarioJpaEntity();
        entity.setId(id);
        entity.setNome(nome);
        entity.setCpf(cpf);
        entity.setCargo("Diretor");
        entity.setDataNasc(Date.valueOf("1980-10-01"));
        entity.setTelefone("81999999999");
        entity.setCep("50000-000");
        entity.setRua("Rua B");
        entity.setNumero("20");
        entity.setBairro("Centro");
        entity.setCidade("Recife");
        entity.setEstado("PE");
        entity.setAtivo(true);
        return entity;
    }
}
