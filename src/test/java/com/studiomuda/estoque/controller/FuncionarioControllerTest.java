package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.model.Funcionario;
import com.studiomuda.estoque.service.FuncionarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FuncionarioControllerTest {

    private FuncionarioController controller;
    private FuncionarioService funcionarioService;

    @BeforeEach
    void setUp() {
        funcionarioService = mock(FuncionarioService.class);
        controller = new FuncionarioController(funcionarioService);
    }

    @Test
    void listarFuncionariosCarregaListaNoModel() throws Exception {
        List<Funcionario> funcionarios = List.of(funcionario(1, "Ana"));
        when(funcionarioService.listar()).thenReturn(funcionarios);
        Model model = new ExtendedModelMap();

        String view = controller.listarFuncionarios(model);

        assertEquals("funcionarios/lista", view);
        assertSame(funcionarios, model.getAttribute("funcionarios"));
    }

    @Test
    void salvarFuncionarioRetornaErroQuandoCpfDuplicado() throws Exception {
        Funcionario funcionario = funcionario(0, "Ana");
        funcionario.setCpf("123.456.789-01");
        Model model = new ExtendedModelMap();
        when(funcionarioService.cpfDuplicado("12345678901", 0)).thenReturn(true);

        String view = controller.salvarFuncionario(funcionario, "1990-10-10", model);

        assertEquals("funcionarios/form", view);
        assertEquals("Já existe um cliente ou funcionário com esse CPF/CNPJ cadastrado.", model.getAttribute("mensagemErro"));
        assertSame(funcionario, model.getAttribute("funcionario"));
    }

    @Test
    void salvarFuncionarioNovoChamaInserir() throws Exception {
        Funcionario funcionario = funcionario(0, "Carlos");
        funcionario.setCpf("123.456.789-01");
        Model model = new ExtendedModelMap();
        when(funcionarioService.cpfDuplicado("12345678901", 0)).thenReturn(false);

        String view = controller.salvarFuncionario(funcionario, "1995-05-20", model);

        assertEquals("redirect:/funcionarios", view);
        assertEquals("12345678901", funcionario.getCpf());
        assertEquals(Date.valueOf("1995-05-20"), funcionario.getData_nasc());
    }

    @Test
    void listarFuncionariosRetornaErroQuandoServiceFalha() throws Exception {
        Model model = new ExtendedModelMap();
        when(funcionarioService.listar()).thenThrow(new java.sql.SQLException("falha listagem"));

        String view = controller.listarFuncionarios(model);

        assertEquals("erro", view);
        assertTrue(model.getAttribute("mensagemErro").toString().contains("falha listagem"));
    }

    @Test
    void getFiltrosFuncionariosDelegaAoService() throws Exception {
        Map<String, List<String>> filtros = Map.of("status", List.of("ativo", "inativo"));
        when(funcionarioService.getFiltros()).thenReturn(filtros);

        Map<String, List<String>> response = controller.getFiltrosFuncionarios();

        assertSame(filtros, response);
    }

    @Test
    void excluirFuncionarioRedirecionaParaErroQuandoFalha() throws Exception {
        doThrow(new java.sql.SQLException("erro ao excluir")).when(funcionarioService).inativar(3);

        String view = controller.excluirFuncionario(3);

        assertEquals("redirect:/erro?mensagem=erro ao excluir", view);
    }

    private Funcionario funcionario(int id, String nome) {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(id);
        funcionario.setNome(nome);
        funcionario.setCpf("12345678901");
        funcionario.setCargo("Auxiliar");
        funcionario.setData_nasc(Date.valueOf("1990-01-01"));
        funcionario.setTelefone("81999999999");
        funcionario.setAtivo(true);
        return funcionario;
    }
}
