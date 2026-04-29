package com.studiomuda.estoque.application.cupom;

import com.studiomuda.estoque.application.cupom.dto.SalvarCupomCommand;
import com.studiomuda.estoque.domain.cupom.Cupom;
import com.studiomuda.estoque.domain.cupom.CupomRepository;
import org.springframework.stereotype.Service;

@Service
public class CadastrarCupomUseCase {
    private final CupomRepository cupomRepository;

    public CadastrarCupomUseCase(CupomRepository cupomRepository) {
        this.cupomRepository = cupomRepository;
    }

    public Cupom executar(SalvarCupomCommand cmd) {
        Cupom cupom = Cupom.novo(cmd.codigo(), cmd.descricao(), cmd.valor(),
                cmd.dataInicio(), cmd.validade(), cmd.condicoesUso());
        return cupomRepository.salvar(cupom);
    }
}
