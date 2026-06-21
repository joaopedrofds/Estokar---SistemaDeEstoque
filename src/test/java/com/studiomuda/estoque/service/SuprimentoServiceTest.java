package com.studiomuda.estoque.service;

import com.studiomuda.estoque.dao.SuprimentoDAO;
import com.studiomuda.estoque.jpa.entity.FornecedorJpaEntity;
import com.studiomuda.estoque.jpa.entity.OrdemCompraJpaEntity;
import com.studiomuda.estoque.jpa.entity.ParametroEstoqueJpaEntity;
import com.studiomuda.estoque.jpa.entity.ProdutoJpaEntity;
import com.studiomuda.estoque.jpa.repository.FornecedorJpaRepository;
import com.studiomuda.estoque.jpa.repository.MovimentacaoEstoqueJpaRepository;
import com.studiomuda.estoque.jpa.repository.OrdemCompraJpaRepository;
import com.studiomuda.estoque.jpa.repository.ParametroEstoqueJpaRepository;
import com.studiomuda.estoque.jpa.repository.ProdutoJpaRepository;
import com.studiomuda.estoque.model.OrdemCompra;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SuprimentoServiceTest {

    @Test
    void salvarParametroJpaReaproveitaFornecedorExistente() throws Exception {
        ProdutoJpaRepository produtos = mock(ProdutoJpaRepository.class);
        FornecedorJpaRepository fornecedores = mock(FornecedorJpaRepository.class);
        ParametroEstoqueJpaRepository parametros = mock(ParametroEstoqueJpaRepository.class);

        ProdutoJpaEntity produto = mock(ProdutoJpaEntity.class);
        when(produto.getId()).thenReturn(10);

        FornecedorJpaEntity fornecedorExistente = new FornecedorJpaEntity();
        fornecedorExistente.setNome("Fornecedor A");
        fornecedorExistente.setLeadTimeDias(7);
        fornecedorExistente.setAtivo(true);

        when(produtos.findById(10)).thenReturn(Optional.of(produto));
        when(fornecedores.findFirstByNomeIgnoreCaseAndLeadTimeDias("Fornecedor A", 7)).thenReturn(Optional.of(fornecedorExistente));
        when(parametros.buscarPorProduto(10)).thenReturn(Optional.empty());
        when(parametros.save(any(ParametroEstoqueJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SuprimentoService service = new SuprimentoService(
                mock(SuprimentoDAO.class),
                provider(produtos),
                provider(fornecedores),
                provider(parametros),
                provider(mock(OrdemCompraJpaRepository.class)),
                provider(mock(MovimentacaoEstoqueJpaRepository.class)),
                provider(mock(AlertaReposicaoService.class))
        );

        service.salvarParametro(10, "Fornecedor A", 7, 12);

        verify(fornecedores).findFirstByNomeIgnoreCaseAndLeadTimeDias("Fornecedor A", 7);
        verify(fornecedores, never()).save(any(FornecedorJpaEntity.class));
        verify(parametros).save(any(ParametroEstoqueJpaEntity.class));
    }

    @Test
    void aprovarFluxoJpaAtualizaStatusEData() throws Exception {
        OrdemCompraJpaRepository ordens = mock(OrdemCompraJpaRepository.class);

        OrdemCompraJpaEntity ordem = new OrdemCompraJpaEntity();
        ordem.setStatus(OrdemCompra.STATUS_RASCUNHO);

        when(ordens.buscarCompletaPorId(3)).thenReturn(Optional.of(ordem));
        when(ordens.save(any(OrdemCompraJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SuprimentoService service = new SuprimentoService(
                mock(SuprimentoDAO.class),
                provider(mock(ProdutoJpaRepository.class)),
                provider(mock(FornecedorJpaRepository.class)),
                provider(mock(ParametroEstoqueJpaRepository.class)),
                provider(ordens),
                provider(mock(MovimentacaoEstoqueJpaRepository.class)),
                provider(mock(AlertaReposicaoService.class))
        );

        service.aprovar(3);

        assertEquals(OrdemCompra.STATUS_APROVADA, ordem.getStatus());
        assertEquals(Date.valueOf(LocalDate.now()), ordem.getDataAprovacao());
        verify(ordens).save(ordem);
    }

    @Test
    void aprovarFluxoJpaRecusaOrdemNaoRascunho() {
        OrdemCompraJpaRepository ordens = mock(OrdemCompraJpaRepository.class);

        OrdemCompraJpaEntity ordem = new OrdemCompraJpaEntity();
        ordem.setStatus(OrdemCompra.STATUS_APROVADA);

        when(ordens.buscarCompletaPorId(5)).thenReturn(Optional.of(ordem));

        SuprimentoService service = new SuprimentoService(
                provider(mock(ProdutoJpaRepository.class)),
                provider(mock(FornecedorJpaRepository.class)),
                provider(mock(ParametroEstoqueJpaRepository.class)),
                provider(ordens),
                provider(mock(MovimentacaoEstoqueJpaRepository.class)),
                provider(mock(AlertaReposicaoService.class))
        );

        IllegalStateException erro = assertThrows(IllegalStateException.class, () -> service.aprovar(5));
        assertEquals("Apenas ordens em rascunho podem ser alteradas.", erro.getMessage());
        verify(ordens, never()).save(any(OrdemCompraJpaEntity.class));
    }

    private <T> ObjectProvider<T> provider(T bean) {
        ObjectProvider<T> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(bean);
        return provider;
    }
}
