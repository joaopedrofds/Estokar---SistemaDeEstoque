package com.studiomuda.estoque.application.cupom;

import com.studiomuda.estoque.application.cupom.dto.SalvarCupomCommand;
import com.studiomuda.estoque.domain.cupom.Cupom;
import com.studiomuda.estoque.domain.cupom.CupomRepository;
import com.studiomuda.estoque.domain.cupom.exceptions.CupomNaoEncontradoException;
import org.springframework.stereotype.Service;

@Service
public class AtualizarCupomUseCase {
    private final CupomRepository cupomRepository;

    public AtualizarCupomUseCase(CupomRepository cupomRepository) {
        this.cupomRepository = cupomRepository;
    }

    public void executar(SalvarCupomCommand cmd) {
        Cupom existente = cupomRepository.buscarPorId(cmd.id())
                .orElseThrow(() -> new CupomNaoEncontradoException(cmd.id()));
        existente.atualizarDados(cmd.codigo(), cmd.descricao(), cmd.valor(),
                cmd.dataInicio(), cmd.validade(), cmd.condicoesUso());
        cupomRepository.atualizar(existente);
    }
}
