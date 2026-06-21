package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.MovimentacaoEstoqueJpaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface MovimentacaoEstoqueJpaRepository extends JpaRepository<MovimentacaoEstoqueJpaEntity, Integer> {

    /**
     * Lista todas as movimentações com o nome do produto, ordenadas por data DESC.
     * Replica MovimentacaoEstoqueDAO.listar().
     */
    @Query(value =
        "SELECT me.id, me.id_produto, me.tipo, me.quantidade, me.motivo, me.data, p.nome AS produto_nome " +
        "FROM movimentacao_estoque me JOIN produto p ON me.id_produto = p.id " +
        "ORDER BY me.data DESC",
        nativeQuery = true)
    List<Object[]> listarComProdutoNome();

    /**
     * Busca uma movimentação por id, incluindo o nome do produto.
     * Replica MovimentacaoEstoqueDAO.buscarPorId().
     */
    @Query(value =
        "SELECT me.id, me.id_produto, me.tipo, me.quantidade, me.motivo, me.data, p.nome AS produto_nome " +
        "FROM movimentacao_estoque me JOIN produto p ON me.id_produto = p.id " +
        "WHERE me.id = :id",
        nativeQuery = true)
    Optional<Object[]> buscarPorIdComProdutoNome(@Param("id") int id);

    /**
     * Busca movimentações com filtros opcionais (produto, tipo, dataInicio, dataFim).
     * Replica MovimentacaoEstoqueDAO.buscarComFiltros().
     */
    @Query(value =
        "SELECT me.id, me.id_produto, me.tipo, me.quantidade, me.motivo, me.data, p.nome AS produto_nome " +
        "FROM movimentacao_estoque me JOIN produto p ON me.id_produto = p.id " +
        "WHERE (:produto IS NULL OR p.nome LIKE :produto) " +
        "  AND (:tipo IS NULL OR me.tipo = :tipo) " +
        "  AND (:dataInicio IS NULL OR me.data >= :dataInicio) " +
        "  AND (:dataFim IS NULL OR me.data <= :dataFim) " +
        "ORDER BY me.data DESC, me.id DESC",
        nativeQuery = true)
    List<Object[]> buscarComFiltrosNativo(
        @Param("produto") String produto,
        @Param("tipo") String tipo,
        @Param("dataInicio") Date dataInicio,
        @Param("dataFim") Date dataFim
    );
    @Query("select coalesce(sum(m.quantidade), 0) " +
            "from MovimentacaoEstoqueJpaEntity m " +
            "where m.produtoId = :produtoId " +
            "and lower(m.tipo) = 'saida' " +
            "and m.data >= :dataMinima")
    Long somarSaidasUltimos90Dias(@Param("produtoId") Integer produtoId, @Param("dataMinima") Date dataMinima);

    @Query("select coalesce(function('datediff', max(m.data), min(m.data)) + 1, 1) " +
            "from MovimentacaoEstoqueJpaEntity m " +
            "where m.produtoId = :produtoId " +
            "and lower(m.tipo) = 'saida' " +
            "and m.data >= :dataMinima")
    Long calcularDiasConsumo(@Param("produtoId") Integer produtoId, @Param("dataMinima") Date dataMinima);

    /**
     * Soma o custo (quantidade * produto.valor) das saídas de estoque no período.
     * Replica a lógica de MovimentacaoEstoqueDAO.somarCustoSaida().
     */
    @Query(value =
        "SELECT COALESCE(SUM(me.quantidade * p.valor), 0) " +
        "FROM movimentacao_estoque me " +
        "JOIN produto p ON p.id = me.id_produto " +
        "WHERE LOWER(me.tipo) = 'saida' AND me.data BETWEEN :inicio AND :fim",
        nativeQuery = true)
    Double somarCustoSaidaNativo(@Param("inicio") Date inicio, @Param("fim") Date fim);

    /**
     * Soma o valor (quantidade * produto.valor) das entradas de devolução/estorno no período.
     * Replica a lógica de MovimentacaoEstoqueDAO.somarDevolucoes().
     */
    @Query(value =
        "SELECT COALESCE(SUM(me.quantidade * p.valor), 0) " +
        "FROM movimentacao_estoque me " +
        "JOIN produto p ON p.id = me.id_produto " +
        "WHERE LOWER(me.tipo) = 'entrada' " +
        "AND (LOWER(me.motivo) LIKE '%devolu%' OR LOWER(me.motivo) LIKE '%estorno%') " +
        "AND me.data BETWEEN :inicio AND :fim",
        nativeQuery = true)
    Double somarDevolucoeNativo(@Param("inicio") Date inicio, @Param("fim") Date fim);
}
