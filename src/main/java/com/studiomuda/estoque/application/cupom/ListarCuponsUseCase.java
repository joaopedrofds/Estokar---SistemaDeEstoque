package com.studiomuda.estoque.application.cupom;

import com.studiomuda.estoque.domain.cupom.Cupom;
import com.studiomuda.estoque.domain.cupom.CupomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarCuponsUseCase {
    private final CupomRepository cupomRepository;

    public ListarCuponsUseCase(CupomRepository cupomRepository) {
        this.cupomRepository = cupomRepository;
    }

    public List<Cupom> listarTodos() {
        return cupomRepository.listarTodos();
    }

    public List<Cupom> listarValidos() {
        return cupomRepository.listarValidos();
    }

    public List<Cupom> buscarComFiltros(String codigo, String status) {
        return cupomRepository.buscarComFiltros(codigo, status);
    }
}
