package com.studiomuda.estoque.service;

import com.studiomuda.estoque.dao.FuncionarioDAO;
import com.studiomuda.estoque.dao.PedidoDAO;
import com.studiomuda.estoque.jpa.entity.ClienteJpaEntity;
import com.studiomuda.estoque.jpa.repository.ClienteJpaRepository;
import com.studiomuda.estoque.model.Cliente;
import com.studiomuda.estoque.model.Funcionario;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    private final ClienteJpaRepository clienteRepository;
    private final PedidoDAO pedidoDAO;
    private final FuncionarioDAO funcionarioDAO;

    public ClienteService(ClienteJpaRepository clienteRepository,
                          PedidoDAO pedidoDAO,
                          FuncionarioDAO funcionarioDAO) {
        this.clienteRepository = clienteRepository;
        this.pedidoDAO = pedidoDAO;
        this.funcionarioDAO = funcionarioDAO;
    }

    public List<Cliente> listar(String nome, String tipo, String status) throws SQLException {
        Boolean ativo = parseStatus(status);
        List<Cliente> clientes;
        if (isBlank(nome) && isBlank(tipo) && ativo == null) {
            clientes = clienteRepository.findAllByOrderByNomeAsc().stream()
                    .map(this::toModel)
                    .toList();
        } else {
            clientes = clienteRepository.buscarComFiltros(valorOuNull(nome), valorOuNull(tipo), ativo).stream()
                    .map(this::toModel)
                    .toList();
        }
        aplicarAnaliseFrequencia(clientes);
        return clientes;
    }

    public Cliente buscarPorId(int id) {
        return clienteRepository.findById(id).map(this::toModel).orElse(null);
    }

    public Cliente buscarPorCpfCnpj(String cpfCnpj) {
        if (isBlank(cpfCnpj)) {
            return null;
        }
        return clienteRepository.findByCpfCnpj(cpfCnpj).map(this::toModel).orElse(null);
    }

    public void salvar(Cliente cliente) throws SQLException {
        validarCliente(cliente);
        validarDocumentoDuplicado(cliente);

        ClienteJpaEntity entity = cliente.getId() == 0
                ? new ClienteJpaEntity()
                : clienteRepository.findById(cliente.getId()).orElse(new ClienteJpaEntity());

        entity.setNome(cliente.getNome());
        entity.setCpfCnpj(cliente.getCpfCnpj());
        entity.setTelefone(cliente.getTelefone());
        entity.setEmail(cliente.getEmail());
        entity.setTipo(cliente.getTipo());
        entity.setCep(cliente.getCep());
        entity.setRua(cliente.getRua());
        entity.setNumero(cliente.getNumero());
        entity.setBairro(cliente.getBairro());
        entity.setCidade(cliente.getCidade());
        entity.setEstado(cliente.getEstado());
        entity.setAtivo(cliente.isAtivo());
        entity.setDataNascimento(cliente.getDataNascimento());
        clienteRepository.save(entity);
    }

    public void inativar(int id) {
        Optional<ClienteJpaEntity> entity = clienteRepository.findById(id);
        if (entity.isPresent()) {
            entity.get().setAtivo(false);
            clienteRepository.save(entity.get());
        }
    }

    public long contar() {
        return clienteRepository.count();
    }

    public List<String> listarTiposDisponiveis() {
        return clienteRepository.listarTiposDisponiveis();
    }

    public List<String> listarStatusDisponiveis() {
        return Arrays.asList("ativo", "inativo");
    }

    void validarCliente(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        if (cliente.getCpfCnpj() == null || cliente.getCpfCnpj().trim().isEmpty()) {
            throw new IllegalArgumentException("O CPF/CNPJ é obrigatório.");
        }
        String cpfCnpjLimpo = cliente.getCpfCnpj().replaceAll("[^0-9]", "");
        String tipo = cliente.getTipo();
        if (tipo == null || tipo.trim().isEmpty()) {
            tipo = "PF";
        }
        if ("PF".equalsIgnoreCase(tipo) && cpfCnpjLimpo.length() != 11) {
            throw new IllegalArgumentException("O CPF deve conter exatamente 11 dígitos. Valor informado: " + cpfCnpjLimpo);
        }
        if ("PJ".equalsIgnoreCase(tipo) && cpfCnpjLimpo.length() != 14) {
            throw new IllegalArgumentException("O CNPJ deve conter exatamente 14 dígitos. Valor informado: " + cpfCnpjLimpo);
        }
        cliente.setCpfCnpj(cpfCnpjLimpo);
        cliente.setTipo(tipo.toUpperCase());
        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("O e-mail é obrigatório.");
        }
        if (!cliente.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("E-mail em formato inválido.");
        }
        if (cliente.getId() == 0 && !cliente.isAtivo()) {
            cliente.setAtivo(true);
        }
    }

    void validarDocumentoDuplicado(Cliente cliente) throws SQLException {
        Funcionario funcionario = funcionarioDAO.buscarPorCpf(cliente.getCpfCnpj());
        boolean documentoEmUsoPorFuncionario = funcionario != null;
        boolean documentoEmUsoPorOutroCliente = cliente.getId() == 0
                ? clienteRepository.existsByCpfCnpj(cliente.getCpfCnpj())
                : clienteRepository.existsByCpfCnpjAndIdNot(cliente.getCpfCnpj(), cliente.getId());

        if (documentoEmUsoPorFuncionario || documentoEmUsoPorOutroCliente) {
            throw new IllegalArgumentException("Já existe um cliente ou funcionário com esse CPF/CNPJ cadastrado.");
        }
    }

    void aplicarAnaliseFrequencia(List<Cliente> clientes) throws SQLException {
        for (Cliente cliente : clientes) {
            List<LocalDate> datasCompra = pedidoDAO.listarDatasCompraPorCliente(cliente.getId());
            cliente.setTotalPedidos(datasCompra.size());

            if (datasCompra.isEmpty()) {
                cliente.setMediaDiasEntreCompras(null);
                cliente.setClassificacaoFrequencia("Sem Compras");
                continue;
            }

            double mediaDias = calcularMediaDiasEntreCompras(datasCompra, LocalDate.now());
            cliente.setMediaDiasEntreCompras(Math.round(mediaDias * 100.0) / 100.0);
            cliente.setClassificacaoFrequencia(classificarFrequencia(mediaDias));
        }
    }

    double calcularMediaDiasEntreCompras(List<LocalDate> datasCompra, LocalDate dataReferencia) {
        if (datasCompra == null || datasCompra.isEmpty()) {
            return 0.0;
        }
        if (datasCompra.size() == 1) {
            return ChronoUnit.DAYS.between(datasCompra.get(0), dataReferencia);
        }
        long somaIntervalos = 0L;
        for (int i = 1; i < datasCompra.size(); i++) {
            somaIntervalos += ChronoUnit.DAYS.between(datasCompra.get(i - 1), datasCompra.get(i));
        }
        return (double) somaIntervalos / (datasCompra.size() - 1);
    }

    String classificarFrequencia(double mediaDias) {
        if (mediaDias < 15) {
            return "Cliente VIP";
        }
        if (mediaDias <= 30) {
            return "Regular";
        }
        return "Em Risco/Inativo";
    }

    private Cliente toModel(ClienteJpaEntity entity) {
        Cliente cliente = new Cliente();
        cliente.setId(entity.getId() != null ? entity.getId() : 0);
        cliente.setNome(entity.getNome());
        cliente.setCpfCnpj(entity.getCpfCnpj());
        cliente.setTelefone(entity.getTelefone());
        cliente.setEmail(entity.getEmail());
        cliente.setTipo(entity.getTipo());
        cliente.setCep(entity.getCep());
        cliente.setRua(entity.getRua());
        cliente.setNumero(entity.getNumero());
        cliente.setBairro(entity.getBairro());
        cliente.setCidade(entity.getCidade());
        cliente.setEstado(entity.getEstado());
        cliente.setAtivo(Boolean.TRUE.equals(entity.getAtivo()));
        cliente.setDataNascimento(entity.getDataNascimento());
        return cliente;
    }

    private Boolean parseStatus(String status) {
        if (isBlank(status)) {
            return null;
        }
        if ("ativo".equalsIgnoreCase(status)) {
            return true;
        }
        if ("inativo".equalsIgnoreCase(status)) {
            return false;
        }
        return null;
    }

    private String valorOuNull(String valor) {
        return isBlank(valor) ? null : valor.trim();
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
