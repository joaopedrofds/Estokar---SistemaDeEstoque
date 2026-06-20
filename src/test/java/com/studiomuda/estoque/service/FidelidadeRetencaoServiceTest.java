package com.studiomuda.estoque.service;

import com.studiomuda.estoque.jpa.entity.AcaoRetencaoJpaEntity;
import com.studiomuda.estoque.jpa.entity.BeneficioCategoriaJpaEntity;
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
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

	@Test
	void recalcularCategoriaComZeroPedidosNaoLancaExcecao() {
		FaixaFidelidadeJpaRepository faixaRepository = mock(FaixaFidelidadeJpaRepository.class);
		PedidoJpaRepository pedidoRepository = mock(PedidoJpaRepository.class);
		ClienteJpaRepository clienteRepository = mock(ClienteJpaRepository.class);
		when(faixaRepository.buscarPorMedia(anyInt())).thenReturn(Optional.empty());

		ClienteJpaEntity cliente = new ClienteJpaEntity();
		cliente.setId(42);
		when(clienteRepository.findById(42)).thenReturn(Optional.of(cliente));
		when(pedidoRepository.listarDatasComprasConfirmadas(42)).thenReturn(Collections.emptyList());
		when(clienteRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		FidelidadeService service = new FidelidadeService(
				faixaRepository,
				mock(BeneficioCategoriaJpaRepository.class),
				clienteRepository,
				pedidoRepository,
				mock(AcaoRetencaoJpaRepository.class)
		);

		assertDoesNotThrow(() -> service.recalcularCategoria(42));
		verify(clienteRepository).save(cliente);
	}

	@Test
	void recalcularCategoriaComMediaFracionariaArredondaAntesDeComparar() {
		FaixaFidelidadeJpaRepository faixaRepository = mock(FaixaFidelidadeJpaRepository.class);
		PedidoJpaRepository pedidoRepository = mock(PedidoJpaRepository.class);
		ClienteJpaRepository clienteRepository = mock(ClienteJpaRepository.class);

		ClienteJpaEntity cliente = new ClienteJpaEntity();
		cliente.setId(42);
		when(clienteRepository.findById(42)).thenReturn(Optional.of(cliente));
		when(clienteRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
		when(faixaRepository.buscarPorMedia(anyInt())).thenReturn(Optional.empty());

		when(pedidoRepository.listarDatasComprasConfirmadas(42)).thenReturn(Arrays.asList(
				Date.valueOf("2026-03-10"),
				Date.valueOf("2026-03-05"),
				Date.valueOf("2026-02-23")
		));

		FidelidadeService service = new FidelidadeService(
				faixaRepository,
				mock(BeneficioCategoriaJpaRepository.class),
				clienteRepository,
				pedidoRepository,
				mock(AcaoRetencaoJpaRepository.class)
		);

		assertDoesNotThrow(() -> service.recalcularCategoria(42));
		verify(faixaRepository).buscarPorMedia(8);
		verify(clienteRepository).save(cliente);
	}

	@Test
	void excluirFaixaSemVinculosRemoveRegistro() {
		FaixaFidelidadeJpaRepository faixaRepository = mock(FaixaFidelidadeJpaRepository.class);
		BeneficioCategoriaJpaRepository beneficioRepository = mock(BeneficioCategoriaJpaRepository.class);
		ClienteJpaRepository clienteRepository = mock(ClienteJpaRepository.class);
		AcaoRetencaoJpaRepository acaoRepository = mock(AcaoRetencaoJpaRepository.class);
		when(beneficioRepository.countByFaixaId(1)).thenReturn(0L);
		when(clienteRepository.countByFaixaFidelidadeId(1)).thenReturn(0L);
		when(acaoRepository.countByFaixaId(1)).thenReturn(0L);
		when(faixaRepository.findById(1)).thenReturn(Optional.of(faixa("Teste", 1, 10)));

		FidelidadeService service = new FidelidadeService(
				faixaRepository, beneficioRepository, clienteRepository,
				mock(PedidoJpaRepository.class), acaoRepository
		);

		assertDoesNotThrow(() -> service.excluirFaixa(1));
		verify(faixaRepository).delete(any());
	}

	@Test
	void excluirFaixaComBeneficioVinculadoLancaExcecao() {
		FaixaFidelidadeJpaRepository faixaRepository = mock(FaixaFidelidadeJpaRepository.class);
		BeneficioCategoriaJpaRepository beneficioRepository = mock(BeneficioCategoriaJpaRepository.class);
		ClienteJpaRepository clienteRepository = mock(ClienteJpaRepository.class);
		AcaoRetencaoJpaRepository acaoRepository = mock(AcaoRetencaoJpaRepository.class);
		when(beneficioRepository.countByFaixaId(1)).thenReturn(2L);
		when(clienteRepository.countByFaixaFidelidadeId(1)).thenReturn(0L);
		when(acaoRepository.countByFaixaId(1)).thenReturn(0L);
		when(faixaRepository.findById(1)).thenReturn(Optional.of(faixa("Teste", 1, 10)));

		FidelidadeService service = new FidelidadeService(
				faixaRepository, beneficioRepository, clienteRepository,
				mock(PedidoJpaRepository.class), acaoRepository
		);

		IllegalStateException erro = assertThrows(IllegalStateException.class, () -> service.excluirFaixa(1));
		assertTrue(erro.getMessage().contains("vinculo"));
		verify(faixaRepository, never()).delete(any());
	}

	@Test
	void excluirFaixaComClienteVinculadoLancaExcecao() {
		FaixaFidelidadeJpaRepository faixaRepository = mock(FaixaFidelidadeJpaRepository.class);
		BeneficioCategoriaJpaRepository beneficioRepository = mock(BeneficioCategoriaJpaRepository.class);
		ClienteJpaRepository clienteRepository = mock(ClienteJpaRepository.class);
		AcaoRetencaoJpaRepository acaoRepository = mock(AcaoRetencaoJpaRepository.class);
		when(beneficioRepository.countByFaixaId(1)).thenReturn(0L);
		when(clienteRepository.countByFaixaFidelidadeId(1)).thenReturn(5L);
		when(acaoRepository.countByFaixaId(1)).thenReturn(0L);
		when(faixaRepository.findById(1)).thenReturn(Optional.of(faixa("Teste", 1, 10)));

		FidelidadeService service = new FidelidadeService(
				faixaRepository, beneficioRepository, clienteRepository,
				mock(PedidoJpaRepository.class), acaoRepository
		);

		IllegalStateException erro = assertThrows(IllegalStateException.class, () -> service.excluirFaixa(1));
		assertTrue(erro.getMessage().contains("vinculo"));
		verify(faixaRepository, never()).delete(any());
	}

	@Test
	void salvarFaixaComSobreposicaoRejeitaNaEdicao() {
		FaixaFidelidadeJpaRepository faixaRepository = mock(FaixaFidelidadeJpaRepository.class);
		// Edicao: id=1, intervalo 10-20, ja existe faixa sobrepondo
		when(faixaRepository.contarSobreposicoes(1, 10, 20)).thenReturn(1L);

		FidelidadeService service = novoFidelidadeService(faixaRepository);
		FaixaFidelidadeJpaEntity faixa = faixa("Regular", 10, 20);
		faixa.setId(1);

		IllegalArgumentException erro = assertThrows(IllegalArgumentException.class, () -> service.salvarFaixa(faixa));
		assertTrue(erro.getMessage().contains("intersecta"));
		verify(faixaRepository, never()).save(any());
	}

	@Test
	void alternarStatusAtivaInativa() {
		FaixaFidelidadeJpaRepository faixaRepository = mock(FaixaFidelidadeJpaRepository.class);
		FaixaFidelidadeJpaEntity faixa = faixa("Teste", 1, 10);
		faixa.setAtiva(true);
		when(faixaRepository.findById(1)).thenReturn(Optional.of(faixa));
		when(faixaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		FidelidadeService service = novoFidelidadeService(faixaRepository);

		FaixaFidelidadeJpaEntity result = service.alternarStatusFaixa(1);
		assertEquals(false, result.getAtiva());
		verify(faixaRepository, times(1)).save(faixa);
	}

	@Test
	void excluirFaixaInexistenteLancaExcecao() {
		FaixaFidelidadeJpaRepository faixaRepository = mock(FaixaFidelidadeJpaRepository.class);
		when(faixaRepository.findById(99)).thenReturn(Optional.empty());

		FidelidadeService service = novoFidelidadeService(faixaRepository);

		assertThrows(IllegalArgumentException.class, () -> service.excluirFaixa(99));
		verify(faixaRepository, never()).delete(any());
	}

	@Test
	void alternarStatusFaixaInexistenteLancaExcecao() {
		FaixaFidelidadeJpaRepository faixaRepository = mock(FaixaFidelidadeJpaRepository.class);
		when(faixaRepository.findById(99)).thenReturn(Optional.empty());

		FidelidadeService service = novoFidelidadeService(faixaRepository);

		assertThrows(IllegalArgumentException.class, () -> service.alternarStatusFaixa(99));
	}

	@Test
	void excluirBeneficioSemPedidosVinculadosRemoveRegistro() {
		BeneficioCategoriaJpaRepository beneficioRepository = mock(BeneficioCategoriaJpaRepository.class);
		PedidoJpaRepository pedidoRepository = mock(PedidoJpaRepository.class);
		BeneficioCategoriaJpaEntity beneficio = new BeneficioCategoriaJpaEntity();
		beneficio.setId(1);
		when(beneficioRepository.findById(1)).thenReturn(Optional.of(beneficio));
		when(pedidoRepository.countByBeneficioAplicadoId(1)).thenReturn(0L);

		FidelidadeService service = new FidelidadeService(
				mock(FaixaFidelidadeJpaRepository.class), beneficioRepository,
				mock(ClienteJpaRepository.class), pedidoRepository,
				mock(AcaoRetencaoJpaRepository.class)
		);

		assertDoesNotThrow(() -> service.excluirBeneficio(1));
		verify(beneficioRepository).delete(beneficio);
	}

	@Test
	void excluirBeneficioComPedidoVinculadoLancaExcecao() {
		BeneficioCategoriaJpaRepository beneficioRepository = mock(BeneficioCategoriaJpaRepository.class);
		PedidoJpaRepository pedidoRepository = mock(PedidoJpaRepository.class);
		BeneficioCategoriaJpaEntity beneficio = new BeneficioCategoriaJpaEntity();
		beneficio.setId(1);
		when(beneficioRepository.findById(1)).thenReturn(Optional.of(beneficio));
		when(pedidoRepository.countByBeneficioAplicadoId(1)).thenReturn(3L);

		FidelidadeService service = new FidelidadeService(
				mock(FaixaFidelidadeJpaRepository.class), beneficioRepository,
				mock(ClienteJpaRepository.class), pedidoRepository,
				mock(AcaoRetencaoJpaRepository.class)
		);

		IllegalStateException erro = assertThrows(IllegalStateException.class, () -> service.excluirBeneficio(1));
		assertTrue(erro.getMessage().contains("ja foi aplicado"));
		verify(beneficioRepository, never()).delete(any());
	}

	@Test
	void excluirBeneficioInexistenteLancaExcecao() {
		BeneficioCategoriaJpaRepository beneficioRepository = mock(BeneficioCategoriaJpaRepository.class);
		when(beneficioRepository.findById(99)).thenReturn(Optional.empty());

		FidelidadeService service = new FidelidadeService(
				mock(FaixaFidelidadeJpaRepository.class), beneficioRepository,
				mock(ClienteJpaRepository.class), mock(PedidoJpaRepository.class),
				mock(AcaoRetencaoJpaRepository.class)
		);

		assertThrows(IllegalArgumentException.class, () -> service.excluirBeneficio(99));
		verify(beneficioRepository, never()).delete(any());
	}

	@Test
	void alternarStatusBeneficioAtivaInativa() {
		BeneficioCategoriaJpaRepository beneficioRepository = mock(BeneficioCategoriaJpaRepository.class);
		BeneficioCategoriaJpaEntity beneficio = new BeneficioCategoriaJpaEntity();
		beneficio.setId(1);
		beneficio.setAtivo(true);
		when(beneficioRepository.findById(1)).thenReturn(Optional.of(beneficio));
		when(beneficioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		FidelidadeService service = new FidelidadeService(
				mock(FaixaFidelidadeJpaRepository.class), beneficioRepository,
				mock(ClienteJpaRepository.class), mock(PedidoJpaRepository.class),
				mock(AcaoRetencaoJpaRepository.class)
		);

		BeneficioCategoriaJpaEntity result = service.alternarStatusBeneficio(1);
		assertEquals(false, result.getAtivo());
		verify(beneficioRepository).save(beneficio);
	}

	@Test
	void alternarStatusBeneficioInexistenteLancaExcecao() {
		BeneficioCategoriaJpaRepository beneficioRepository = mock(BeneficioCategoriaJpaRepository.class);
		when(beneficioRepository.findById(99)).thenReturn(Optional.empty());

		FidelidadeService service = new FidelidadeService(
				mock(FaixaFidelidadeJpaRepository.class), beneficioRepository,
				mock(ClienteJpaRepository.class), mock(PedidoJpaRepository.class),
				mock(AcaoRetencaoJpaRepository.class)
		);

		assertThrows(IllegalArgumentException.class, () -> service.alternarStatusBeneficio(99));
	}

	@Test
	void editarBeneficioTrocaFaixaEPercentual() {
		FaixaFidelidadeJpaRepository faixaRepository = mock(FaixaFidelidadeJpaRepository.class);
		BeneficioCategoriaJpaRepository beneficioRepository = mock(BeneficioCategoriaJpaRepository.class);

		FaixaFidelidadeJpaEntity faixaOriginal = new FaixaFidelidadeJpaEntity();
		faixaOriginal.setId(1);
		faixaOriginal.setNome("Regular");
		FaixaFidelidadeJpaEntity faixaNova = new FaixaFidelidadeJpaEntity();
		faixaNova.setId(2);
		faixaNova.setNome("Premium");

		BeneficioCategoriaJpaEntity beneficio = new BeneficioCategoriaJpaEntity();
		beneficio.setId(1);
		beneficio.setFaixa(faixaOriginal);
		beneficio.setPercentualDesconto(new BigDecimal("10"));
		beneficio.setDescricao("Antigo");

		when(faixaRepository.findById(2)).thenReturn(Optional.of(faixaNova));
		when(beneficioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		FidelidadeService service = new FidelidadeService(
				faixaRepository, beneficioRepository,
				mock(ClienteJpaRepository.class), mock(PedidoJpaRepository.class),
				mock(AcaoRetencaoJpaRepository.class)
		);

		beneficio.setPercentualDesconto(new BigDecimal("25"));
		beneficio.setDescricao("Editado");
		BeneficioCategoriaJpaEntity result = service.salvarBeneficio(beneficio, 2);

		assertEquals(new BigDecimal("25"), result.getPercentualDesconto());
		assertEquals("Editado", result.getDescricao());
		assertEquals(faixaNova, result.getFaixa());
		verify(beneficioRepository).save(beneficio);
	}

	private FidelidadeService novoFidelidadeService(FaixaFidelidadeJpaRepository faixaRepository) {
		return new FidelidadeService(
				faixaRepository,
				mock(BeneficioCategoriaJpaRepository.class),
				mock(ClienteJpaRepository.class),
				mock(PedidoJpaRepository.class),
				mock(AcaoRetencaoJpaRepository.class)
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