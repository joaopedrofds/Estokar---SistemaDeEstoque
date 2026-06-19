package com.studiomuda.estoque.service;

import com.studiomuda.estoque.jpa.entity.ContagemItemJpaEntity;
import com.studiomuda.estoque.jpa.entity.InventarioEscopoProdutoJpaEntity;
import com.studiomuda.estoque.jpa.entity.MovimentacaoEstoqueJpaEntity;
import com.studiomuda.estoque.jpa.entity.ParametroInventarioJpaEntity;
import com.studiomuda.estoque.jpa.entity.ProdutoJpaEntity;
import com.studiomuda.estoque.jpa.entity.SessaoInventarioJpaEntity;
import com.studiomuda.estoque.jpa.repository.ContagemItemJpaRepository;
import com.studiomuda.estoque.jpa.repository.InventarioEscopoProdutoJpaRepository;
import com.studiomuda.estoque.jpa.repository.MovimentacaoEstoqueJpaRepository;
import com.studiomuda.estoque.jpa.repository.ParametroInventarioJpaRepository;
import com.studiomuda.estoque.jpa.repository.ProdutoJpaRepository;
import com.studiomuda.estoque.jpa.repository.SessaoInventarioJpaRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class InventarioService {
    public static final String STATUS_EM_ANDAMENTO = "EM_ANDAMENTO";
    public static final String STATUS_AGUARDANDO_APROVACAO = "AGUARDANDO_APROVACAO";
    public static final String STATUS_FECHADO = "FECHADO";

    private final SessaoInventarioJpaRepository sessaoRepository;
    private final InventarioEscopoProdutoJpaRepository escopoRepository;
    private final ContagemItemJpaRepository contagemRepository;
    private final ProdutoJpaRepository produtoRepository;
    private final MovimentacaoEstoqueJpaRepository movimentacaoRepository;
    private final ParametroInventarioJpaRepository parametroRepository;

    public InventarioService(ObjectProvider<SessaoInventarioJpaRepository> sessaoRepository,
                             ObjectProvider<InventarioEscopoProdutoJpaRepository> escopoRepository,
                             ObjectProvider<ContagemItemJpaRepository> contagemRepository,
                             ObjectProvider<ProdutoJpaRepository> produtoRepository,
                             ObjectProvider<MovimentacaoEstoqueJpaRepository> movimentacaoRepository,
                             ObjectProvider<ParametroInventarioJpaRepository> parametroRepository) {
        this.sessaoRepository = sessaoRepository.getIfAvailable();
        this.escopoRepository = escopoRepository.getIfAvailable();
        this.contagemRepository = contagemRepository.getIfAvailable();
        this.produtoRepository = produtoRepository.getIfAvailable();
        this.movimentacaoRepository = movimentacaoRepository.getIfAvailable();
        this.parametroRepository = parametroRepository.getIfAvailable();
    }

    @Transactional
    public SessaoInventarioJpaEntity abrirSessao(String setor,
                                                 UsuarioOperacao gerente,
                                                 boolean bloqueiaSaidas) {
        validarSetor(setor);
        validarUsuario(gerente, "gerente");
        validarJpaDisponivel();

        String setorNormalizado = setor.trim();
        if (sessaoRepository.existsBySetorIgnoreCaseAndStatus(setorNormalizado, STATUS_EM_ANDAMENTO)
                || sessaoRepository.existsBySetorIgnoreCaseAndStatus(setorNormalizado, STATUS_AGUARDANDO_APROVACAO)) {
            throw new IllegalStateException("Ja existe sessao de inventario ativa para este setor.");
        }

        List<ProdutoJpaEntity> produtos = produtoRepository.findAllByOrderByNomeAsc();
        if (produtos.isEmpty()) {
            throw new IllegalStateException("Nao existem produtos cadastrados para inventariar.");
        }

        SessaoInventarioJpaEntity sessao = new SessaoInventarioJpaEntity();
        sessao.setSetor(setorNormalizado);
        sessao.setDataAbertura(Date.valueOf(LocalDate.now()));
        sessao.setGerenteId(gerente.getId());
        sessao.setGerenteNome(gerente.getNome());
        sessao.setStatus(STATUS_EM_ANDAMENTO);
        sessao.setBloqueiaSaidas(bloqueiaSaidas);
        sessao.setToleranciaQuantidade(buscarToleranciaQuantidade());
        sessao = sessaoRepository.save(sessao);

        for (ProdutoJpaEntity produto : produtos) {
            InventarioEscopoProdutoJpaEntity escopo = new InventarioEscopoProdutoJpaEntity();
            escopo.setSessaoId(sessao.getId());
            escopo.setProdutoId(produto.getId());
            escopo.setQuantidadeSistemaAbertura(valorOuZero(produto.getQuantidade()));
            escopoRepository.save(escopo);
        }
        return sessao;
    }

    @Transactional
    public ContagemItemJpaEntity registrarContagem(int sessaoId,
                                                   int produtoId,
                                                   int quantidadeFisica,
                                                   UsuarioOperacao auxiliar) {
        if (quantidadeFisica < 0) {
            throw new IllegalArgumentException("A quantidade fisica deve ser maior ou igual a zero.");
        }
        validarUsuario(auxiliar, "auxiliar");
        validarJpaDisponivel();

        SessaoInventarioJpaEntity sessao = buscarSessaoObrigatoria(sessaoId);
        if (!STATUS_EM_ANDAMENTO.equals(sessao.getStatus())) {
            throw new IllegalStateException("Contagens so podem ser registradas em sessoes em andamento.");
        }
        escopoRepository.findBySessaoIdAndProdutoId(sessaoId, produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto nao pertence ao escopo desta sessao."));

        ContagemItemJpaEntity contagem = new ContagemItemJpaEntity();
        contagem.setSessaoId(sessaoId);
        contagem.setProdutoId(produtoId);
        contagem.setQuantidadeFisica(quantidadeFisica);
        contagem.setAuxiliarId(auxiliar.getId());
        contagem.setAuxiliarNome(auxiliar.getNome());
        contagem.setDataContagem(Timestamp.valueOf(LocalDateTime.now()));
        return contagemRepository.save(contagem);
    }

    @Transactional
    public ResultadoFechamento fecharSessao(int sessaoId,
                                            UsuarioOperacao usuario,
                                            Collection<String> autoridades) {
        validarUsuario(usuario, "usuario");
        validarJpaDisponivel();
        SessaoInventarioJpaEntity sessao = buscarSessaoObrigatoria(sessaoId);

        if (STATUS_FECHADO.equals(sessao.getStatus())) {
            throw new IllegalStateException("Sessao de inventario ja fechada e imutavel.");
        }
        if (!STATUS_EM_ANDAMENTO.equals(sessao.getStatus())
                && !STATUS_AGUARDANDO_APROVACAO.equals(sessao.getStatus())) {
            throw new IllegalStateException("Sessao de inventario nao esta apta para fechamento.");
        }

        List<ItemConciliacao> conciliacoes = montarConciliacao(sessao, true);
        boolean exigeAprovacao = conciliacoes.stream()
                .anyMatch(item -> Math.abs(item.getDiferenca()) > valorOuZero(sessao.getToleranciaQuantidade()));
        boolean aprovador = possuiAlcadaAprovacao(autoridades);

        if (exigeAprovacao && !aprovador) {
            sessao.setStatus(STATUS_AGUARDANDO_APROVACAO);
            sessao.setObservacao("Divergencia acima da tolerancia. Fechamento retido para aprovacao gerencial.");
            sessaoRepository.save(sessao);
            return ResultadoFechamento.aguardandoAprovacao(sessao.getId(), conciliacoes);
        }

        aplicarAjustes(sessao, conciliacoes, exigeAprovacao ? usuario : null);
        return ResultadoFechamento.fechado(sessao.getId(), conciliacoes, exigeAprovacao);
    }

    @Transactional(readOnly = true)
    public List<SessaoInventarioJpaEntity> listarSessoes() {
        if (sessaoRepository == null) {
            return java.util.Collections.emptyList();
        }
        return sessaoRepository.findAllByOrderByDataAberturaDescIdDesc();
    }

    @Transactional(readOnly = true)
    public DetalheInventario buscarDetalhe(int sessaoId) {
        validarJpaDisponivel();
        SessaoInventarioJpaEntity sessao = buscarSessaoObrigatoria(sessaoId);
        List<ItemConciliacao> itens = montarConciliacao(sessao, false);
        List<ContagemItemJpaEntity> historico = contagemRepository.findBySessaoIdOrderByDataContagemDescIdDesc(sessaoId);
        historico.forEach(contagem -> {
            if (contagem.getProduto() != null) {
                contagem.getProduto().getNome();
            }
        });
        return new DetalheInventario(sessao, itens, historico);
    }

    @Transactional(readOnly = true)
    public List<ProdutoJpaEntity> listarProdutos() {
        if (produtoRepository == null) {
            return java.util.Collections.emptyList();
        }
        return produtoRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public int buscarToleranciaQuantidade() {
        if (parametroRepository == null) {
            return 3;
        }
        return parametroRepository.findFirstByOrderByIdAsc()
                .map(ParametroInventarioJpaEntity::getToleranciaQuantidade)
                .orElse(3);
    }

    public boolean possuiAlcadaAprovacao(Collection<String> autoridades) {
        if (autoridades == null) {
            return false;
        }
        for (String autoridade : autoridades) {
            if (autoridade == null) {
                continue;
            }
            String valor = autoridade.toUpperCase();
            if (valor.equals("ROLE_DIRETOR")
                    || valor.equals("ROLE_ADMINISTRADOR")
                    || valor.equals("ROLE_GERENTE")
                    || valor.equals("ROLE_GERENTE_OPERACIONAL")) {
                return true;
            }
        }
        return false;
    }

    private List<ItemConciliacao> montarConciliacao(SessaoInventarioJpaEntity sessao, boolean exigirTodasContadas) {
        List<InventarioEscopoProdutoJpaEntity> escopo = escopoRepository.findBySessaoIdOrderByProduto_NomeAsc(sessao.getId());
        if (escopo.isEmpty()) {
            throw new IllegalStateException("Sessao sem escopo de produtos.");
        }

        List<ItemConciliacao> itens = new ArrayList<>();
        for (InventarioEscopoProdutoJpaEntity itemEscopo : escopo) {
            ContagemItemJpaEntity ultimaContagem = contagemRepository
                    .findTopBySessaoIdAndProdutoIdOrderByDataContagemDescIdDesc(sessao.getId(), itemEscopo.getProdutoId())
                    .orElse(null);
            if (ultimaContagem == null && exigirTodasContadas) {
                throw new IllegalStateException("Existem itens do escopo sem contagem registrada.");
            }
            ProdutoJpaEntity produto = itemEscopo.getProduto();
            if (produto != null) {
                produto.getNome();
                produto.getQuantidade();
            }
            itens.add(new ItemConciliacao(itemEscopo, ultimaContagem));
        }
        return itens;
    }

    private void aplicarAjustes(SessaoInventarioJpaEntity sessao,
                               List<ItemConciliacao> conciliacoes,
                               UsuarioOperacao aprovador) {
        for (ItemConciliacao item : conciliacoes) {
            int diferenca = item.getDiferenca();
            ProdutoJpaEntity produto = produtoRepository.findById(item.getProdutoId())
                    .orElseThrow(() -> new IllegalStateException("Produto do inventario nao encontrado."));

            if (diferenca != 0) {
                String tipo = diferenca > 0 ? "entrada" : "saida";
                String motivo = diferenca > 0
                        ? "EntradaPorSobra - Inventario #" + sessao.getId()
                        : "SaidaPorQuebra - Inventario #" + sessao.getId();
                movimentacaoRepository.save(new MovimentacaoEstoqueJpaEntity(
                        produto.getId(),
                        tipo,
                        Math.abs(diferenca),
                        motivo,
                        Date.valueOf(LocalDate.now())
                ));
            }

            produto.setQuantidade(item.getQuantidadeFisica());
            produtoRepository.save(produto);
        }

        sessao.setStatus(STATUS_FECHADO);
        sessao.setDataFechamento(Timestamp.valueOf(LocalDateTime.now()));
        sessao.setObservacao("Inventario fechado com ajuste de saldos pelo saldo fisico.");
        if (aprovador != null) {
            sessao.setAprovadorId(aprovador.getId());
            sessao.setAprovadorNome(aprovador.getNome());
            sessao.setDataAprovacao(Timestamp.valueOf(LocalDateTime.now()));
        }
        sessaoRepository.save(sessao);
    }

    private SessaoInventarioJpaEntity buscarSessaoObrigatoria(int sessaoId) {
        return sessaoRepository.findById(sessaoId)
                .orElseThrow(() -> new IllegalArgumentException("Sessao de inventario nao encontrada."));
    }

    private void validarJpaDisponivel() {
        if (sessaoRepository == null || escopoRepository == null || contagemRepository == null
                || produtoRepository == null || movimentacaoRepository == null || parametroRepository == null) {
            throw new IllegalStateException("Modulo de inventario requer JPA habilitado.");
        }
    }

    private void validarSetor(String setor) {
        if (setor == null || setor.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o setor do inventario.");
        }
    }

    private void validarUsuario(UsuarioOperacao usuario, String papel) {
        if (usuario == null || usuario.getId() <= 0 || usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o " + papel + " responsavel pela operacao.");
        }
    }

    private int valorOuZero(Integer valor) {
        return valor != null ? valor : 0;
    }

    public static class UsuarioOperacao {
        private final int id;
        private final String nome;

        public UsuarioOperacao(int id, String nome) {
            this.id = id;
            this.nome = nome;
        }

        public int getId() { return id; }
        public String getNome() { return nome; }
    }

    public static class DetalheInventario {
        private final SessaoInventarioJpaEntity sessao;
        private final List<ItemConciliacao> itens;
        private final List<ContagemItemJpaEntity> historico;

        public DetalheInventario(SessaoInventarioJpaEntity sessao,
                                 List<ItemConciliacao> itens,
                                 List<ContagemItemJpaEntity> historico) {
            this.sessao = sessao;
            this.itens = itens;
            this.historico = historico;
        }

        public SessaoInventarioJpaEntity getSessao() { return sessao; }
        public List<ItemConciliacao> getItens() { return itens; }
        public List<ContagemItemJpaEntity> getHistorico() { return historico; }
    }

    public static class ItemConciliacao {
        private final InventarioEscopoProdutoJpaEntity escopo;
        private final ContagemItemJpaEntity ultimaContagem;

        public ItemConciliacao(InventarioEscopoProdutoJpaEntity escopo, ContagemItemJpaEntity ultimaContagem) {
            this.escopo = escopo;
            this.ultimaContagem = ultimaContagem;
        }

        public Integer getProdutoId() { return escopo.getProdutoId(); }
        public String getProdutoNome() {
            return escopo.getProduto() != null ? escopo.getProduto().getNome() : "Produto #" + escopo.getProdutoId();
        }
        public int getQuantidadeSistema() { return escopo.getQuantidadeSistemaAbertura(); }
        public boolean isContado() { return ultimaContagem != null; }
        public Integer getQuantidadeFisica() { return ultimaContagem != null ? ultimaContagem.getQuantidadeFisica() : null; }
        public int getDiferenca() { return isContado() ? getQuantidadeFisica() - getQuantidadeSistema() : 0; }
        public String getAuxiliarNome() { return ultimaContagem != null ? ultimaContagem.getAuxiliarNome() : null; }
        public Timestamp getDataContagem() { return ultimaContagem != null ? ultimaContagem.getDataContagem() : null; }
    }

    public static class ResultadoFechamento {
        private final int sessaoId;
        private final String status;
        private final List<ItemConciliacao> itens;
        private final boolean exigiuAprovacao;

        private ResultadoFechamento(int sessaoId, String status, List<ItemConciliacao> itens, boolean exigiuAprovacao) {
            this.sessaoId = sessaoId;
            this.status = status;
            this.itens = itens;
            this.exigiuAprovacao = exigiuAprovacao;
        }

        public static ResultadoFechamento fechado(int sessaoId, List<ItemConciliacao> itens, boolean exigiuAprovacao) {
            return new ResultadoFechamento(sessaoId, STATUS_FECHADO, itens, exigiuAprovacao);
        }

        public static ResultadoFechamento aguardandoAprovacao(int sessaoId, List<ItemConciliacao> itens) {
            return new ResultadoFechamento(sessaoId, STATUS_AGUARDANDO_APROVACAO, itens, true);
        }

        public int getSessaoId() { return sessaoId; }
        public String getStatus() { return status; }
        public List<ItemConciliacao> getItens() { return itens; }
        public boolean isExigiuAprovacao() { return exigiuAprovacao; }
    }
}
