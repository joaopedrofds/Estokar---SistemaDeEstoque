package com.studiomuda.estoque.service;

import com.studiomuda.estoque.jpa.entity.MovimentacaoEstoqueJpaEntity;
import com.studiomuda.estoque.jpa.entity.ProdutoJpaEntity;
import com.studiomuda.estoque.jpa.repository.MovimentacaoEstoqueJpaRepository;
import com.studiomuda.estoque.jpa.repository.ProdutoJpaRepository;
import com.studiomuda.estoque.model.MovimentacaoEstoque;
import com.studiomuda.estoque.model.Produto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EstoqueService {

    private final MovimentacaoEstoqueJpaRepository movimentacaoRepository;
    private final ProdutoJpaRepository produtoRepository;
    private final SuprimentoService suprimentoService;

    public EstoqueService(MovimentacaoEstoqueJpaRepository movimentacaoRepository,
                          ProdutoJpaRepository produtoRepository,
                          SuprimentoService suprimentoService) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
        this.suprimentoService = suprimentoService;
    }

    // -------------------------------------------------------------------------
    // Listagem / busca
    // -------------------------------------------------------------------------

    public List<MovimentacaoEstoque> listarMovimentacoes() {
        return movimentacaoRepository.listarComProdutoNome()
                .stream()
                .map(this::mapRowToMovimentacao)
                .collect(Collectors.toList());
    }

    public List<MovimentacaoEstoque> buscarMovimentacoesComFiltros(
            String produto, String tipo, String dataInicio, String dataFim) {

        String produtoParam = (produto != null && !produto.trim().isEmpty())
                ? "%" + produto.trim() + "%" : null;
        String tipoParam = (tipo != null && !tipo.trim().isEmpty())
                ? tipo.trim() : null;
        Date dataInicioParam = (dataInicio != null && !dataInicio.trim().isEmpty())
                ? Date.valueOf(dataInicio.trim()) : null;
        Date dataFimParam = (dataFim != null && !dataFim.trim().isEmpty())
                ? Date.valueOf(dataFim.trim()) : null;

        return movimentacaoRepository
                .buscarComFiltrosNativo(produtoParam, tipoParam, dataInicioParam, dataFimParam)
                .stream()
                .map(this::mapRowToMovimentacao)
                .collect(Collectors.toList());
    }

    public MovimentacaoEstoque buscarMovimentacaoPorId(int id) {
        Optional<Object[]> row = movimentacaoRepository.buscarPorIdComProdutoNome(id);
        return row.map(this::mapRowToMovimentacao).orElse(null);
    }

    // -------------------------------------------------------------------------
    // Registro (entrada / saída) — replica MovimentacaoEstoqueDAO.registrar()
    // -------------------------------------------------------------------------

    @Transactional
    public void registrarMovimentacao(MovimentacaoEstoque movimentacao) {
        // Persiste a movimentação
        MovimentacaoEstoqueJpaEntity entity = new MovimentacaoEstoqueJpaEntity(
                movimentacao.getIdProduto(),
                movimentacao.getTipo(),
                movimentacao.getQuantidade(),
                movimentacao.getMotivo(),
                movimentacao.getData()
        );
        entity = movimentacaoRepository.save(entity);
        movimentacao.setId(entity.getId());

        // Atualiza o estoque do produto
        int fator = movimentacao.getTipo().equalsIgnoreCase("saida") ? -1 : 1;
        ProdutoJpaEntity produto = produtoRepository.findById(movimentacao.getIdProduto())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Produto não encontrado com ID: " + movimentacao.getIdProduto()));
        produto.adicionarQuantidade(fator * movimentacao.getQuantidade());
        produtoRepository.save(produto);

        // Tenta gerar rascunho de suprimento após saída (best-effort)
        if (movimentacao.getTipo().equalsIgnoreCase("saida")) {
            tryGerarRascunho(movimentacao.getIdProduto());
        }
    }

    // -------------------------------------------------------------------------
    // Exclusão — replica MovimentacaoEstoqueDAO.deletar()
    // -------------------------------------------------------------------------

    /**
     * Exclui uma movimentação após verificar que a exclusão não resultará em estoque negativo.
     * Lança IllegalStateException se a regra de negócio for violada.
     */
    @Transactional
    public void excluirMovimentacao(int id) {
        MovimentacaoEstoque movimentacao = buscarMovimentacaoPorId(id);
        if (movimentacao == null) {
            throw new IllegalArgumentException("Movimentação não encontrada com ID: " + id);
        }

        // Regra de negócio: excluir uma entrada reduziria o estoque; verificar se ficaria negativo
        if (movimentacao.getTipo().equalsIgnoreCase("entrada")) {
            int produtoId = movimentacao.getIdProduto();
            int quantidadeMovimentada = movimentacao.getQuantidade();
            ProdutoJpaEntity produto = produtoRepository.findById(produtoId).orElse(null);
            int estoqueAtual = (produto != null && produto.getQuantidade() != null)
                    ? produto.getQuantidade() : 0;
            if (estoqueAtual < quantidadeMovimentada) {
                throw new IllegalStateException(
                        "Não é possível excluir esta movimentação pois resultaria em estoque negativo. " +
                        "Estoque atual: " + estoqueAtual);
            }
        }

        // Estorna o estoque: inverso da operação original
        ProdutoJpaEntity produto = produtoRepository.findById(movimentacao.getIdProduto()).orElse(null);
        if (produto != null) {
            int fator = movimentacao.getTipo().equalsIgnoreCase("saida") ? -1 : 1;
            // Estorno = reverter: saída vira +, entrada vira -
            produto.adicionarQuantidade(-fator * movimentacao.getQuantidade());
            produtoRepository.save(produto);
        }

        movimentacaoRepository.deleteById(id);
    }

    // -------------------------------------------------------------------------
    // Produtos
    // -------------------------------------------------------------------------

    public List<Produto> listarProdutos() {
        return produtoRepository.findAll()
                .stream()
                .map(this::mapEntityToProduto)
                .collect(Collectors.toList());
    }

    public Produto buscarProdutoPorId(int id) {
        return produtoRepository.findById(id)
                .map(this::mapEntityToProduto)
                .orElse(null);
    }

    // -------------------------------------------------------------------------
    // Helpers de mapeamento
    // -------------------------------------------------------------------------

    /**
     * Mapeia uma linha de native query (id, id_produto, tipo, quantidade, motivo, data, produto_nome)
     * para o modelo de domínio MovimentacaoEstoque.
     */
    private MovimentacaoEstoque mapRowToMovimentacao(Object[] row) {
        MovimentacaoEstoque m = new MovimentacaoEstoque(
                toInt(row[0]),       // id
                toInt(row[1]),       // id_produto
                (String) row[2],     // tipo
                toInt(row[3]),       // quantidade
                (String) row[4],     // motivo
                toDate(row[5])       // data
        );
        m.setProdutoNome((String) row[6]); // produto_nome
        return m;
    }

    private Produto mapEntityToProduto(ProdutoJpaEntity e) {
        Produto p = new Produto(
                e.getId() != null ? e.getId() : 0,
                e.getNome(),
                null,   // descricao não está no JPA entity — campo não mapeado
                null,   // tipo não está no JPA entity
                e.getQuantidade() != null ? e.getQuantidade() : 0,
                e.getValor() != null ? e.getValor().doubleValue() : 0.0
        );
        if (e.getCusto() != null) {
            p.setCusto(e.getCusto().doubleValue());
        }
        return p;
    }

    private int toInt(Object o) {
        if (o instanceof Number) return ((Number) o).intValue();
        return 0;
    }

    private Date toDate(Object o) {
        if (o instanceof Date) return (Date) o;
        if (o instanceof java.util.Date) return new Date(((java.util.Date) o).getTime());
        return null;
    }

    // -------------------------------------------------------------------------
    // Auxiliar: suprimento (best-effort)
    // -------------------------------------------------------------------------

    private void tryGerarRascunho(int produtoId) {
        try {
            suprimentoService.gerarRascunhoSeNecessario(produtoId);
        } catch (Exception ignored) {
        }
    }
}
