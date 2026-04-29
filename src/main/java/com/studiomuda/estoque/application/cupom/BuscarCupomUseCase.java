package com.studiomuda.estoque.application.cupom;

import com.studiomuda.estoque.domain.cupom.Cupom;
import com.studiomuda.estoque.domain.cupom.CupomRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BuscarCupomUseCase {
    private final CupomRepository cupomRepository;

    public BuscarCupomUseCase(CupomRepository cupomRepository) {
        this.cupomRepository = cupomRepository;
    }

    public Optional<Cupom> porId(int id) {
        return cupomRepository.buscarPorId(id);
    }

    public Optional<Cupom> porCodigo(String codigo) {
        return cupomRepository.buscarPorCodigo(codigo);
    }
}
