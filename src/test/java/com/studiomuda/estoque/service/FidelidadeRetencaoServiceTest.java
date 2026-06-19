package com.studiomuda.estoque.service;

import com.studiomuda.estoque.jpa.entity.AcaoRetencaoJpaEntity;
import com.studiomuda.estoque.jpa.entity.ClienteJpaEntity;
import com.studiomuda.estoque.jpa.entity.FaixaFidelidadeJpaEntity;
import com.studiomuda.estoque.jpa.repository.AcaoRetencaoJpaRepository;
import com.studiomuda.estoque.jpa.repository.BeneficioCategoriaJpaRepository;
import com.studiomuda.estoque.jpa.repository.ClienteJpaRepository;
import com.studiomuda.estoque.jpa.repository.FaixaFidelidadeJpaRepository;
import com.studiomuda.estoque.jpa.repository.PedidoJpaRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FidelidadeRetencaoServiceTest {
    @Test
    void calculaMediaAritmeticaDosIntervalos() {
        FidelidadeService service = novoFidelidadeService(mock(FaixaFidelidadeJpaRepository.class));

        double media = service.calcularMediaDias(Arrays.asList(
                Date.valueOf("2026-02-01"),
                Date.valueOf("2026-01-21"),
                Date.valueOf("2026-01-01")
        ));

        assertEquals(15.5, media);
    }

    @Test
    void rejeitaFaixaSobreposta() {
        FaixaFidelidadeJpaRepository faixaRepository = mock(FaixaFidelidadeJpaRepository.class);
        when(faixaRepository.contarSobreposicoes(null, 10, 20)).thenReturn(1L);
        FidelidadeService service = novoFidelidadeService(faixaRepository);
        FaixaFidelidadeJpaEntity faixa = faixa("Regular", 10, 20);

        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class, () -> service.salvarFaixa(faixa));

        assertTrue(erro.getMessage().contains("intersecta"));
        verify(faixaRepository, never()).save(any());
    }

    @Test
    void geraUmaAcaoParaClienteEmRisco() {
        AcaoRetencaoJpaRepository acaoRepository = mock(AcaoRetencaoJpaRepository.class);
        ClienteJpaRepository clienteRepository = mock(ClienteJpaRepository.class);
        FidelidadeService fidelidadeService = mock(FidelidadeService.class);
        ClienteJpaEntity cliente = clienteNaFaixa("Em Risco");
        when(fidelidadeService.recalcularCategoria(7)).thenReturn(cliente);
        when(acaoRepository.findFirstByClienteIdAndAtivaTrue(7)).thenReturn(Optional.empty());
        when(acaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        RetencaoService service = new RetencaoService(acaoRepository, clienteRepository, fidelidadeService);

        AcaoRetencaoJpaEntity acao = service.gerarAcao(7, new BigDecimal("12.5"), LocalDate.now().plusDays(20));

        assertTrue(acao.getCodigoCupom().startsWith("RET-7-"));
        assertEquals("Em Risco", acao.getFaixa().getNome());
    }

    @Test
    void rejeitaSegundaAcaoAtiva() {
        AcaoRetencaoJpaRepository acaoRepository = mock(AcaoRetencaoJpaRepository.class);
        FidelidadeService fidelidadeService = mock(FidelidadeService.class);
        when(fidelidadeService.recalcularCategoria(7)).thenReturn(clienteNaFaixa("INATIVO"));
        when(acaoRepository.findFirstByClienteIdAndAtivaTrue(7)).thenReturn(Optional.of(new AcaoRetencaoJpaEntity()));
        RetencaoService service = new RetencaoService(acaoRepository, mock(ClienteJpaRepository.class), fidelidadeService);

        assertThrows(IllegalStateException.class,
                () -> service.gerarAcao(7, BigDecimal.TEN, LocalDate.now().plusDays(10)));
    }

    private FidelidadeService novoFidelidadeService(FaixaFidelidadeJpaRepository faixaRepository) {
        return new FidelidadeService(
                faixaRepository,
                mock(BeneficioCategoriaJpaRepository.class),
                mock(ClienteJpaRepository.class),
                mock(PedidoJpaRepository.class)
        );
    }

    private FaixaFidelidadeJpaEntity faixa(String nome, int minimo, int maximo) {
        FaixaFidelidadeJpaEntity faixa = new FaixaFidelidadeJpaEntity();
        faixa.setNome(nome);
        faixa.setDiasMinimo(minimo);
        faixa.setDiasMaximo(maximo);
        return faixa;
    }

    private ClienteJpaEntity clienteNaFaixa(String nome) {
        ClienteJpaEntity cliente = new ClienteJpaEntity();
        cliente.setId(7);
        cliente.setFaixaFidelidade(faixa(nome, 31, 9999));
        return cliente;
    }
}
