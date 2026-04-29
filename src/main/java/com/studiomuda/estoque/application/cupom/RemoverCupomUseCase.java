package com.studiomuda.estoque.application.cupom;

import com.studiomuda.estoque.domain.cupom.CupomRepository;
import org.springframework.stereotype.Service;

@Service
public class RemoverCupomUseCase {
    private final CupomRepository cupomRepository;

    public RemoverCupomUseCase(CupomRepository cupomRepository) {
        this.cupomRepository = cupomRepository;
    }

    public void executar(int id) {
        cupomRepository.remover(id);
    }
}
