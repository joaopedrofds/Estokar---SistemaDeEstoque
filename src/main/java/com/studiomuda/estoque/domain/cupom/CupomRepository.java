package com.studiomuda.estoque.domain.cupom;

import java.util.List;
import java.util.Optional;

public interface CupomRepository {

    Cupom salvar(Cupom cupom);

    void atualizar(Cupom cupom);

    Optional<Cupom> buscarPorId(int id);

    Optional<Cupom> buscarPorCodigo(String codigo);

    List<Cupom> listarTodos();

    List<Cupom> listarValidos();

    List<Cupom> buscarComFiltros(String codigo, String status);

    void remover(int id);
}
